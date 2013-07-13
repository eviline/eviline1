package org.eviline.ai;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.eviline.BlockType;
import org.eviline.Field;
import org.eviline.PlayerAction;
import org.eviline.PlayerActionNode;
import org.eviline.PlayerActionType;
import org.eviline.Shape;
import org.eviline.ShapeType;
import org.eviline.fitness.AbstractFitness;
import org.eviline.fitness.Fitness;
import org.eviline.fitness.WrapperFitness;
import org.funcish.core.para.ParaExecutors;

/**
 * Class which holds the AI algorithms.  These algorithms are documented in
 * the associated methods.
 * @author robin
 *
 */
public class DefaultAIKernel implements AIKernel {

	
	public DefaultAIKernel() {}
	
	protected boolean highGravity = false;
	protected boolean hardDropOnly = false;
	protected AbstractFitness fitness = AbstractFitness.getDefaultInstance();
	protected ExecutorService pool = ParaExecutors.AVAILABLE_X2;

	protected Map<PlayerActionNode, List<PlayerAction>> allPathsFrom(Field field) {
		Map<PlayerActionNode, List<PlayerAction>> shortestPaths = new PlayerAction.NodeMap<List<PlayerAction>>();
		
		field = field.copy();
		
		PlayerActionNode start = new PlayerActionNode(field.getShape(), field.getShapeX(), field.getShapeY());
		ArrayDeque<PlayerActionNode> pending = new ArrayDeque<PlayerActionNode>();
		shortestPaths.put(start, new ArrayList<PlayerAction>());
		
//		Node dasLeft = new PlayerAction(field, Type.DAS_LEFT).getEndNode();
//		Node dasRight = new PlayerAction(field, Type.DAS_RIGHT).getEndNode();
//		
//		shortestPaths.put(dasLeft, Arrays.asList(new PlayerAction(field, Type.DAS_LEFT)));
//		shortestPaths.put(dasRight, Arrays.asList(new PlayerAction(field, Type.DAS_RIGHT)));
		
			for(int r = 0; r < 4; r++) {
				if(!shortestPaths.containsKey(start))
					continue;
				pending.add(start);
				PlayerActionNode n = start;
				for(int i = 0; i < 10; i++) {
					field.setShapeX(n.getX());
					PlayerAction pa = new PlayerAction(field, PlayerActionType.SHIFT_LEFT);
					if(!pa.isPossible())
						break;
					List<PlayerAction> nl = shortestPaths.get(n);
					nl = new ArrayList<PlayerAction>(nl);
					nl.add(pa);
					if(shortestPaths.get(pa.getEndNode()) == null || shortestPaths.get(pa.getEndNode()).size() > nl.size())
						shortestPaths.put(n = pa.getEndNode(), nl);
					pending.add(pa.getEndNode());
				}
				n = start;
				for(int i = 0; i < 10; i++) {
					field.setShapeX(n.getX());
					PlayerAction pa = new PlayerAction(field, PlayerActionType.SHIFT_RIGHT);
					if(!pa.isPossible())
						break;
					List<PlayerAction> nl = shortestPaths.get(n);
					nl = new ArrayList<PlayerAction>(nl);
					nl.add(pa);
					if(shortestPaths.get(pa.getEndNode()) == null || shortestPaths.get(pa.getEndNode()).size() > nl.size())
						shortestPaths.put(n = pa.getEndNode(), nl);
					pending.add(pa.getEndNode());
				}
				field.setShapeX(start.getX());
				field.setShapeY(start.getY());
				PlayerAction pa = new PlayerAction(field, PlayerActionType.ROTATE_LEFT);
				field.setShape(pa.getEndShape());
				start = pa.getEndNode();
				field.setShapeX(start.getX());
				field.setShapeY(start.getY());
				if(r < 3) {
					List<PlayerAction> nl = new ArrayList<PlayerAction>(shortestPaths.get(pa.getStartNode()));
					nl.add(pa);
					if(nl.size() == 3) {
						field.setShape(field.getShape().rotateLeft());
						PlayerAction rpa = new PlayerAction(field, PlayerActionType.ROTATE_RIGHT);
						field.setShape(pa.getEndShape());
						nl.clear();
						nl.add(rpa);
					}
					if(pa.isPossible())
						shortestPaths.put(pa.getEndNode(), nl);
				}
			}
		
		
		int minY = Integer.MAX_VALUE;
		for(int x = Field.BUFFER; x < Field.BUFFER + Field.WIDTH; x++) {
			for(int y = Field.BUFFER; y < Field.BUFFER + Field.HEIGHT; y++) {
				if(field.getField()[y][x] != null) {
					minY = Math.min(minY, y);
					break;
				}
			}
		}
		
		PlayerActionNode n;
		while(pending.size() > 0) {
			n = pending.pollFirst();
			List<PlayerAction> nl = shortestPaths.get(n);
			PlayerActionType[] values;
			if(!highGravity && field.getShapeY() < 0) {
				values = PlayerActionType.shiftFirstValues();
			} else
				values = PlayerActionType.dropFirstValues();
			for(PlayerActionType t : values) {
				field.setShape(n.getShape());
				field.setShapeX(n.getX());
				field.setShapeY(n.getY());
				PlayerAction pa = new PlayerAction(field, t);
				if(!pa.isPossible()) {
					continue;
				}
				if((highGravity && !field.getShape().intersects(field.getField(), field.getShapeX(), field.getShapeY() + 1)
						|| field.getShapeY() < minY - 4
						|| hardDropOnly) 
						&& t != PlayerActionType.DOWN_ONE)
					continue;
				PlayerActionNode dest = pa.getEndNode();
				List<PlayerAction> destPath = new ArrayList<PlayerAction>(nl);
				destPath.add(pa);
				if(!shortestPaths.containsKey(dest) /* || destPath.size() < shortestPaths.get(dest).size() */ ) {
					shortestPaths.put(dest, destPath);
					pending.offerLast(dest);
				}
			}
		}
		
		return shortestPaths;
	}
	
	protected List<PlayerAction> pathsTo(Field field, PlayerActionNode dest) {
		return allPathsFrom(field).get(dest);
	}
	
	/**
	 * Determine the best way for a player to play a particular queue of shapes.
	 * @param context
	 * @return
	 */
	@Override
	public Decision bestFor(final QueueContext context) {
		final Decision best = new Decision(context.type, context.original);
		if(context.remainingDepth == 0) {
			double score = fitness.score(context.paintedImpossible);
//			if(context.original.lines != context.shallowest().original.lines)
//				score -= 10000 * Math.pow(context.original.lines - context.shallowest().original.lines, 2.5);
			best.score = score;
			return best;
		}
		
		best.score = Double.POSITIVE_INFINITY;
		
		final Map<PlayerActionNode, List<PlayerAction>> paths;
		List<Shape> orientations = new ArrayList<Shape>(Arrays.asList(context.type.searchOrientations()));
		if(context.shallower == null) {
			context.original.setLines(0);
			Field starter = context.original.copy();
			if(starter.getShape() == null) {
				starter.setShape(context.type.starter());
				starter.setShapeY(context.type.starterY());
//				starter.shapeX = Field.WIDTH / 2 + Field.BUFFER - 2 + context.type.starterX();
//				starter.shapeX = (Field.WIDTH + Field.BUFFER * 2 - starter.shape.width()) / 2;
				starter.setShapeX(context.type.starterX());
				if(!starter.getShape().intersects(starter.getField(), starter.getShapeX(), starter.getShapeY() + 1))
					starter.setShapeY(starter.getShapeY() + 1);
			} else {
				orientations.remove(starter.getShape());
				orientations.add(0, starter.getShape());
			}
			paths = Collections.synchronizedMap(allPathsFrom(starter));
		} else
			paths = null;
		if(context.type == ShapeType.O) { // Paint the unlikelies as impossible for O pieces
			fitness.paintUnlikelies(context.paintedImpossible);
			for(int y = Field.BUFFER; y < Field.BUFFER + Field.HEIGHT; y++) {
				for(int x = Field.BUFFER; x < Field.BUFFER + Field.WIDTH; x++)
					if(context.paintedImpossible.getField()[y][x] == BlockType.G)
						context.paintedImpossible.getField()[y][x] = BlockType.X;
			}
		}
		List<Future<?>> futures = new ArrayList<Future<?>>();
		for(final Shape shape : orientations) {
			Runnable task = new Runnable() {
				@Override
				public void run() {
					for(int ix = Field.BUFFER - 2; ix < Field.WIDTH + Field.BUFFER + 2; ix++) {
						final int x = ix;
						Field possibility = new Field();
						boolean grounded = shape.intersects(context.paintedImpossible.getField(), x, 0);
						for(int y = 0; y < Field.HEIGHT + Field.BUFFER + 2; y++) {
							boolean groundedAbove = grounded;
							grounded = shape.intersects(paths == null ? context.paintedImpossible.getField() : context.original.getField(), x, y+1);
							PlayerActionNode n = new PlayerActionNode(shape, x, y);
							if(paths != null && !paths.containsKey(n))
								continue;
							if(!groundedAbove && grounded) {
								context.original.copyInto(possibility);
								possibility.setShape(shape);
								possibility.setShapeX(x);
								possibility.setShapeY(y);
								possibility.clockTick();
								possibility.setShape(shape);
								possibility.setShapeX(x);
								possibility.setShapeY(y);
								double base = fitness.scoreWithPaint(possibility);
								QueueContext deeper = context.deeper(possibility);
								Decision option = bestFor(deeper);
								synchronized(best) {
									if(best.deeper == null || option.score + base < best.score) {
										context.deeper = deeper;
										best.bestShape = shape;
										best.bestShapeX = x;
										best.bestShapeY = y;
										if(paths != null)
											best.bestPath = paths.get(n);
										best.deeper = option;
										best.score = option.score + base;
										best.field = possibility.copy();
									}
									if(best.worstScore < option.score)
										best.worstScore = option.score;
								}
							}
						}
					}
				}
			};
			if(context.shallower != null)
				task.run();
			else
				futures.add(pool.submit(task));
		}
		
		for(Future<?> f : futures) {
			try {
				f.get();
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		
		if(context.shallower == null) {
			Decision d = best.deeper;
			Field df = best.field.copy();
			while(d != null) {
				if(d.type == null)
					break;
				df.setShape(d.type.starter());
				df.setShapeX(d.type.starterX());
				df.setShapeY(d.type.starterY());
				Map<PlayerActionNode, List<PlayerAction>> pla = allPathsFrom(df);
				d.bestPath = pla.get(new PlayerActionNode(d.bestShape, d.bestShapeX, d.bestShapeY));
				df = d.field.copy();
				if(d == d.deeper)
					break;
				d = d.deeper;
			}
		}
		
		return best.copy();
	}
	
	/**
	 * Determine the best way for a player to play a single shape.
	 * @param context
	 * @param type
	 * @return
	 */
	@Override
	public Decision bestFor(Context context, ShapeType type) {
		Decision best = new Decision(type, Double.POSITIVE_INFINITY, context.original.copy());
		
		Field possibility = new Field();
		Field paintedPossibility = new Field();
		
		for(Shape shape : type.orientations()) {
			
			for(int x = Field.BUFFER - 2; x < Field.WIDTH + Field.BUFFER + 2; x++) {
				boolean grounded = shape.intersects(context.paintedImpossible.getField(), x, 0);
				for(int y = 0; y < Field.HEIGHT + Field.BUFFER + 2; y++) {
					boolean groundedAbove = grounded;
					grounded = shape.intersects(context.paintedImpossible.getField(), x, y+1);
					if(!groundedAbove && grounded) {
						context.original.copyInto(possibility);
						possibility.setLines(0);
						possibility.setShape(shape);
						possibility.setShapeX(x);
						possibility.setShapeY(y);
						possibility.clockTick();
						possibility.setShape(shape);
						possibility.setShapeX(x);
						possibility.setShapeY(y);
						possibility.copyInto(paintedPossibility);
						fitness.paintImpossibles(paintedPossibility);
						double score = fitness.score(paintedPossibility);
						score -= 10000 * Math.pow(possibility.getLines(), 1.5);
						if(score < best.score) {
							best.bestShape = shape;
							best.bestShapeX = x;
							best.bestShapeY = y;
							best.score = score;
							possibility.copyInto(best.field);
						}
					}
				}
			}
		}
		
		return best;
	}
	
	@Override
	public Decision bestFor(Field inPlayField) {
		final Context context = new Context(this, null, inPlayField, 0);
		ShapeType type = inPlayField.getShape().type();
		
		final Decision best = new Decision(type, Double.POSITIVE_INFINITY, context.original.copy());
		
		Field starter = inPlayField;
		
//		Set<PlayerAction> visitedActions = new HashSet<PlayerAction>();
		final Map<PlayerActionNode, List<PlayerAction>> allPaths = Collections.synchronizedMap(allPathsFrom(inPlayField));
		List<Future<?>> futures = new ArrayList<Future<?>>();
		List<Shape> orientations = new ArrayList<Shape>(Arrays.asList(type.orientations()));
		if(inPlayField.getShape() != null) {
			orientations.remove(inPlayField.getShape());
			orientations.add(0, inPlayField.getShape());
		}
		for(final Shape shape : orientations) {
			Runnable task = new Runnable() {
				@Override
				public void run() {
					Field possibility = new Field();
					Field paintedPossibility = new Field();
					for(int x = Field.BUFFER - 2; x < Field.WIDTH + Field.BUFFER + 2; x++) {
						boolean grounded = shape.intersects(context.paintedImpossible.getField(), x, 0);
						for(int y = 0; y < Field.HEIGHT + Field.BUFFER + 2; y++) {
							boolean groundedAbove = grounded;
							grounded = shape.intersects(context.paintedImpossible.getField(), x, y+1);
							if(!groundedAbove && grounded) {
								context.original.copyInto(possibility);
								possibility.setLines(0);
								possibility.setShape(shape);
								possibility.setShapeX(x);
								possibility.setShapeY(y);
								possibility.clockTick();
								possibility.setShape(shape);
								possibility.setShapeX(x);
								possibility.setShapeY(y);
								possibility.copyInto(paintedPossibility);
								fitness.paintImpossibles(paintedPossibility);
								double score = fitness.score(paintedPossibility);
								score -= 10000 * Math.pow(possibility.getLines(), 1.5);
								synchronized(best) {
									if(score < best.score) {
										PlayerActionNode node = new PlayerActionNode(shape, x, y);
										List<PlayerAction> pa = allPaths.get(node);
										if(pa == null)
											continue;
										best.bestPath = pa;
										best.bestShape = shape;
										best.bestShapeX = x;
										best.bestShapeY = y;
										best.score = score;
										possibility.copyInto(best.field);
									}
									if(best.worstScore < score)
										best.worstScore = score;
								}
							}
						}
					}
				}
			};
			futures.add(pool.submit(task));
		}
		
		for(Future<?> f : futures) {
			try {
				f.get();
			} catch(Exception ex) {
				throw new RuntimeException(ex);
			}
		}
		
		return best;
	}
	
	/**
	 * Determine the shape that would be best for the player
	 * @param context
	 * @return
	 */
	@Override
	public Decision bestFor(Context context) {
		Decision best = new Decision(null, Double.POSITIVE_INFINITY, context.original.copy());
		double originalScore = fitness.scoreWithPaint(best.field);
		
		for(ShapeType type : ShapeType.values()) {
			if(type == context.omit)
				continue;
			Decision bestForType = bestFor(context, type);
			Decision bestPlannable = planBest(context.deeper(bestForType.field), bestForType);
			bestForType.deeper = bestPlannable;
			bestForType.score = bestPlannable.score;
			context.decisionModifier.modifyPlannedDecision(context, bestForType);
			if(bestForType.score < best.score) {
				best = bestForType;
			}
		}
		
		best.score *= context.remainingDepth * context.remainingDepth;
		best.score += originalScore;
		
		return best;
	}
	
	/**
	 * Determine the shape that would be best for the player
	 * @param context
	 * @param defaultDecision
	 * @return
	 */
	@Override
	public Decision planBest(Context context, Decision defaultDecision) {
		if(context.remainingDepth < 0)
			return defaultDecision;
		
		return bestFor(context);
	}
	
	/**
	 * Determine the shape that would be worst for the player
	 * @param context
	 * @return
	 */
	@Override
	public Decision worstFor(Context context) {
		Decision worst = new Decision(null, Double.NEGATIVE_INFINITY, context.original.copy());
		
		for(ShapeType type : ShapeType.values()) {
			if(type == context.omit)
				continue;
			Decision best = bestFor(context, type);
			Decision worstPlannable = planWorst(context.deeper(best.field), best);
//			worstPlannable.type = type;
			best.deeper = worstPlannable;
			best.score = worstPlannable.score;
			context.decisionModifier.modifyPlannedDecision(context, best);
			if(best.score > worst.score) {
				worst = best;
			}
		}
		
		return worst;
	}
	
	/**
	 * Determine the shape that would be worst for the player
	 * @param context
	 * @param defaultDecision
	 * @return
	 */
	@Override
	public Decision planWorst(Context context, Decision defaultDecision) {
		if(context.remainingDepth < 0)
			return defaultDecision;
		
		Decision worst = worstFor(context);
		return worst;
	}

	public boolean isHighGravity() {
		return highGravity;
	}

	public void setHighGravity(boolean highGravity) {
		this.highGravity = highGravity;
	}

	public boolean isHardDropOnly() {
		return hardDropOnly;
	}

	public void setHardDropOnly(boolean hardDropOnly) {
		this.hardDropOnly = hardDropOnly;
	}

	@Override
	public AbstractFitness getFitness() {
		return fitness;
	}

	@Override
	public void setFitness(Fitness fitness) {
		if(fitness instanceof AbstractFitness)
			this.fitness = (AbstractFitness) fitness;
		else
			this.fitness = new WrapperFitness(fitness);
	}
}
