package org.eviline.ai;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.eviline.Block;
import org.eviline.Field;
import org.eviline.PlayerAction;
import org.eviline.PlayerActionNode;
import org.eviline.Shape;
import org.eviline.ShapeType;
import org.eviline.PlayerAction.NodeMap;
import org.eviline.PlayerAction.Type;
import org.eviline.fitness.Fitness;

/**
 * Class which holds the AI algorithms.  These algorithms are documented in
 * the associated methods.
 * @author robin
 *
 */
public class AIKernel {

	/**
	 * Interface for objects which wish to modify the decision reached by an iteration
	 * of an AI algorithm.  Modifications include but are not limited to adjusting the
	 * score attached to a decision based on other factors, such as the recent frequency
	 * of shapes.
	 * @author robin
	 *
	 */
	public static interface DecisionModifier {
		/**
		 * Modify the planned decision.
		 * @param context The context in which the decision was made
		 * @param decision The decision that was made.
		 */
		public void modifyPlannedDecision(Context context, Decision decision);
	}
	
	/**
	 * A decision context.  Contains the original {@link Field}, a copy
	 * of the field with impossible areas marked, and some book-keeping info.
	 * There is one {@link Context} at each node in the decision tree.
	 * @author robin
	 *
	 */
	public class Context {
		/**
		 * The decision modifier in use for this context.
		 */
		public DecisionModifier decisionModifier;
		/**
		 * The original {@link Field} based on which a decision is to be made
		 */
		public Field original;
		/**
		 * The original {@link Field} (see {@link #original}) but painted impossible.
		 * (see {@link Fitness#paintImpossibles(Field)})
		 */
		public Field paintedImpossible;
		/**
		 * The remaining decision depth at this context in the decision tree.
		 */
		public int remainingDepth;
		/**
		 * The {@link ShapeType} to omit from the decision.
		 */
		public ShapeType omit;
		
		/**
		 * Create a new {@link Context}.
		 * @param decisionModifier
		 * @param original
		 * @param remainingDepth
		 */
		public Context(DecisionModifier decisionModifier, Field original, int remainingDepth) {
			this.decisionModifier = decisionModifier;
			this.original = original.copy();
			this.paintedImpossible = original.copy();
			fitness.paintImpossibles(paintedImpossible);
			this.remainingDepth = remainingDepth;
		}
		
		/**
		 * Construct a {@link Context} that is a copy of this one, but for use at one level
		 * deeper in the decision tree.
		 * @param deeperOriginal
		 * @return
		 */
		public Context deeper(Field deeperOriginal) {
			return new Context(decisionModifier, deeperOriginal, remainingDepth - 1);
		}
		
		@Override
		public String toString() {
			return String.valueOf(original);
		}
	}
	
	/**
	 * {@link Context} for a decision-tree whose list of shapes is already decided.
	 * A {@link QueueContext} is used instead for determining the optimal shape placements
	 * that a user will make.
	 * @author robin
	 *
	 */
	public class QueueContext extends Context {
		/**
		 * The queue of known upcoming shapes
		 */
		public ShapeType[] queue;
		/**
		 * The decided-on context, one level deeper
		 */
		public QueueContext deeper;
		/**
		 * The parent of this context
		 */
		public QueueContext shallower;
		/**
		 * The current shape type in the shape queue
		 */
		public ShapeType type;
		
		/**
		 * Create a {@link QueueContext} for determining the optimal placement of shapes
		 * (from a player's perspective) for the argument {@link Field} and {@link ShapeType} array.
		 * @param original
		 * @param queue
		 */
		public QueueContext(Field original, ShapeType[] queue) {
			super(null, original, queue.length);
			this.queue = queue;
			if(remainingDepth > 0) {
				type = queue[0];
				deeper = new QueueContext(original, Arrays.copyOfRange(queue, 1, queue.length));
				deeper.shallower = this;
			}
		}
		
		@Override
		public QueueContext deeper(Field deeperOriginal) {
			QueueContext deeper = new QueueContext(deeperOriginal.copy(), Arrays.copyOfRange(queue, 1, queue.length));
//			deeper.original = deeperOriginal.copy();
//			deeper.paintedImpossible = deeper.original.copy();
//			fitness.paintImpossibles(deeper.paintedImpossible);
			deeper.shallower = this;
			return deeper;
		}
		
		/**
		 * Returns the "root" parent {@link QueueContext}.
		 * @return
		 */
		public QueueContext shallowest() {
			return shallower == null ? this : shallower.shallowest();
		}
		
		/**
		 * Returns the "decided" context, the deepest of the children.
		 * @return
		 */
		public QueueContext deepest() {
			return deeper == null ? this : deeper.deepest();
		}
	}
	
	/**
	 * A decision that can be made by the {@link AIKernel}.
	 * @author robin
	 *
	 */
	public static class Decision {
		/**
		 * The score of this decision
		 */
		public double score;
		/**
		 * The shape that was decided on
		 */
		public ShapeType type;
		/**
		 * The field that was decided on
		 */
		public Field field;
		
		public Shape bestShape;
		
		public List<PlayerAction> bestPath;
		
		public int bestShapeX;
		
		public int bestShapeY;
		
		public double worstScore = Double.NEGATIVE_INFINITY;
		/**
		 * One level deeper in the final decision path
		 */
		public volatile Decision deeper;
		
		public Decision() {}
		public Decision(ShapeType type) {
			this.type = type;
		}
		public Decision(ShapeType type, double score) {
			this.type = type;
			this.score = score;
		}
		public Decision(ShapeType type, Field field) {
			this.type = type;
			this.field = field;
		}
		public Decision(ShapeType type, double score, Field field) {
			this.type = type;
			this.score = score;
			this.field = field;
		}
		/**
		 * Copy this {@link Decision}
		 * @return
		 */
		public Decision copy() {
			Decision c = new Decision(type, score, field);
			if(deeper == null)
				c.deeper = null;
			else if(deeper != this)
				c.deeper = deeper.copy();
			else
				c.deeper = c;
			c.bestPath = bestPath;
			c.bestShape = bestShape;
			c.bestShapeX = bestShapeX;
			c.bestShapeY = bestShapeY;
			c.worstScore = worstScore;
			return c;
		}
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder("[");
			sb.append(score);
			sb.append(":");
			sb.append(type);
			Decision d = deeper;
			while(d != null) {
				sb.append(" -> ");
				sb.append(d.type);
				if(d == d.deeper)
					break;
				d = d.deeper;
			}
			sb.append("]");
			return sb.toString();
		}
		/**
		 * Generate a string of shapes that represent the decision path
		 * @return
		 */
		public String taunt() {
			if(deeper == this)
				return String.valueOf(type);
			if(deeper != null)
				return type + deeper.taunt();
			return String.valueOf(type);
		}
		/**
		 * Return the deepest decision in the decision path
		 * @return
		 */
		public Decision deepest() {
			return (deeper == null || deeper == this) ? this : deeper.deepest();
		}
	}
	
	private static AIKernel instance;
	public static AIKernel getInstance() {
		if(instance == null)
			instance = new AIKernel();
		return instance;
	}
	
	public AIKernel() {}
	
	private boolean highGravity = false;
	private boolean hardDropOnly = false;
	private Fitness fitness = Fitness.getDefaultInstance();
	private ExecutorService pool = Executors.newFixedThreadPool(4);

	public Map<PlayerActionNode, List<PlayerAction>> allPathsFrom(Field field) {
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
					PlayerAction pa = new PlayerAction(field, Type.SHIFT_LEFT);
					if(!pa.isPossible())
						break;
					List<PlayerAction> nl = shortestPaths.get(n);
					nl = new ArrayList<PlayerAction>(nl);
					nl.add(pa);
					shortestPaths.put(n = pa.getEndNode(), nl);
					pending.add(pa.getEndNode());
				}
				n = start;
				for(int i = 0; i < 10; i++) {
					field.setShapeX(n.getX());
					PlayerAction pa = new PlayerAction(field, Type.SHIFT_RIGHT);
					if(!pa.isPossible())
						break;
					List<PlayerAction> nl = shortestPaths.get(n);
					nl = new ArrayList<PlayerAction>(nl);
					nl.add(pa);
					shortestPaths.put(n = pa.getEndNode(), nl);
					pending.add(pa.getEndNode());
				}
				field.setShapeX(start.getX());
				field.setShapeY(start.getY());
				PlayerAction pa = new PlayerAction(field, Type.ROTATE_LEFT);
				field.setShape(pa.getEndShape());
				start = pa.getEndNode();
				if(r < 3) {
					List<PlayerAction> nl = new ArrayList<PlayerAction>(shortestPaths.get(pa.getStartNode()));
					nl.add(pa);
					if(nl.size() == 3) {
						field.setShape(field.getShape().rotateLeft());
						PlayerAction rpa = new PlayerAction(field, Type.ROTATE_RIGHT);
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
			Type[] values;
			if(!highGravity && field.getShapeY() < 0) {
				values = Type.shiftFirstValues();
			} else
				values = Type.dropFirstValues();
			for(Type t : values) {
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
						&& t != Type.DOWN_ONE)
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
	
	/**
	 * Determine the best way for a player to play a particular queue of shapes.
	 * @param context
	 * @return
	 */
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
			}
			paths = Collections.synchronizedMap(allPathsFrom(starter));
		} else
			paths = null;
		if(context.type == ShapeType.O) { // Paint the unlikelies as impossible for O pieces
			fitness.paintUnlikelies(context.paintedImpossible);
			for(int y = Field.BUFFER; y < Field.BUFFER + Field.HEIGHT; y++) {
				for(int x = Field.BUFFER; x < Field.BUFFER + Field.WIDTH; x++)
					if(context.paintedImpossible.getField()[y][x] == Block.G)
						context.paintedImpossible.getField()[y][x] = Block.X;
			}
		}
		List<Future<?>> futures = new ArrayList<Future<?>>();
		for(final Shape shape : context.type.orientations()) {
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
	
	public Decision bestFor(Field inPlayField) {
		final Context context = new Context(null, inPlayField, 0);
		ShapeType type = inPlayField.getShape().type();
		
		final Decision best = new Decision(type, Double.POSITIVE_INFINITY, context.original.copy());
		
		Field starter = inPlayField;
		
//		Set<PlayerAction> visitedActions = new HashSet<PlayerAction>();
		final Map<PlayerActionNode, List<PlayerAction>> allPaths = Collections.synchronizedMap(allPathsFrom(inPlayField));
		List<Future<?>> futures = new ArrayList<Future<?>>();
		for(final Shape shape : type.orientations()) {
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
										List<PlayerAction> pa = allPaths.get(new PlayerActionNode(shape, x, y));
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

	public Fitness getFitness() {
		return fitness;
	}

	public void setFitness(Fitness fitness) {
		this.fitness = fitness;
	}
}
