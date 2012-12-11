package org.eviline;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eviline.PlayerAction.Node;
import org.eviline.PlayerAction.Type;

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
	public static class Context {
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
			Fitness.paintImpossibles(paintedImpossible);
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
	public static class QueueContext extends Context {
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
			deeper.original = deeperOriginal.copy();
			deeper.paintedImpossible = deeper.original.copy();
			Fitness.paintImpossibles(deeper.paintedImpossible);
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
		/**
		 * One level deeper in the final decision path
		 */
		public Decision deeper;
		
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
			c.deeper = deeper;
			c.bestPath = bestPath;
			c.bestShape = bestShape;
			c.bestShapeX = bestShapeX;
			c.bestShapeY = bestShapeY;
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
	
	private AIKernel() {}

	public Map<Node, List<PlayerAction>> allPathsFrom(Field field) {
		Map<Node, List<PlayerAction>> shortestPaths = new HashMap<Node, List<PlayerAction>>();
		
		field = field.copy();
		
		Node start = new Node(field.shape, field.shapeX, field.shapeY);
		shortestPaths.put(start, new ArrayList<PlayerAction>());
		ArrayDeque<Node> pending = new ArrayDeque<Node>();
		pending.add(start);
		Node n = start;
		for(int i = 0; i < 5; i++) {
			field.shapeX = n.getX();
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
		for(int i = 0; i < 5; i++) {
			field.shapeX = n.getX();
			PlayerAction pa = new PlayerAction(field, Type.SHIFT_RIGHT);
			if(!pa.isPossible())
				break;
			List<PlayerAction> nl = shortestPaths.get(n);
			nl = new ArrayList<PlayerAction>(nl);
			nl.add(pa);
			shortestPaths.put(n = pa.getEndNode(), nl);
			pending.add(pa.getEndNode());
		}
		
		ArrayDeque<Node> toprow = new ArrayDeque<PlayerAction.Node>();
		toprow.addAll(pending);
		while(toprow.size() > 0) {
			n = toprow.pollFirst();
			List<PlayerAction> nl = shortestPaths.get(n);
			field.shape = n.getShape();
			field.shapeX = n.getX();
			field.shapeY = n.getY();
			for(Type t : new Type[] {Type.ROTATE_LEFT, Type.ROTATE_RIGHT}) {
				PlayerAction pa = new PlayerAction(field, t);
				if(!pa.isPossible())
					continue;
				Node dest = pa.getEndNode();
				List<PlayerAction> destPath = new ArrayList<PlayerAction>(nl);
				destPath.add(pa);
				if(!shortestPaths.containsKey(dest) || destPath.size() < shortestPaths.get(dest).size()) {
					shortestPaths.put(dest, destPath);
					pending.offerLast(dest);
				}
			}
		}

		toprow.addAll(pending);
		while(toprow.size() > 0) {
			List<PlayerAction> nl = shortestPaths.get(n);
			n = toprow.pollFirst();
			field.shape = n.getShape();
			field.shapeX = n.getX();
			field.shapeY = n.getY();
			Type t = Type.ROTATE_LEFT;
			PlayerAction pa = new PlayerAction(field, t);
			if(!pa.isPossible())
				continue;
			n = pa.getEndNode();
			List<PlayerAction> nl2 = shortestPaths.get(n);
			if(nl2 == null) {
				nl2 = new ArrayList<PlayerAction>(nl);
				nl2.add(pa);
				shortestPaths.put(n, nl2);
			}
			field.shape = n.getShape();
			field.shapeX = n.getX();
			field.shapeY = n.getY();
			t = Type.ROTATE_LEFT;
			pa = new PlayerAction(field, t);
			if(!pa.isPossible())
				continue;
			
			Node dest = pa.getEndNode();
			List<PlayerAction> destPath = new ArrayList<PlayerAction>(nl2);
			destPath.add(pa);
			if(!shortestPaths.containsKey(dest) || destPath.size() < shortestPaths.get(dest).size()) {
				shortestPaths.put(dest, destPath);
				pending.offerLast(dest);
			}
		}
		
		while(pending.size() > 0) {
			n = pending.pollFirst();
			List<PlayerAction> nl = shortestPaths.get(n);
			field.shape = n.getShape();
			field.shapeX = n.getX();
			field.shapeY = n.getY();
			for(Type t : Type.values()) {
				PlayerAction pa = new PlayerAction(field, t);
				if(!pa.isPossible())
					continue;
				Node dest = pa.getEndNode();
				List<PlayerAction> destPath = new ArrayList<PlayerAction>(nl);
				destPath.add(pa);
				if(!shortestPaths.containsKey(dest) || destPath.size() < shortestPaths.get(dest).size()) {
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
	public Decision bestFor(QueueContext context) {
		Decision best = new Decision(context.type, context.original);
		if(context.remainingDepth == 0) {
			double score = Fitness.score(context.paintedImpossible);
			if(context.original.lines != context.shallowest().original.lines)
				score -= 10000 * Math.pow(context.original.lines - context.shallowest().original.lines, 2.5);
			best.score = score;
			return best;
		}

		
		Field possibility = new Field();
		Map<Node, List<PlayerAction>> paths;
		if(context.shallower == null) {
			context.original.lines = 0;
			Field starter = context.original.copy();
			if(starter.shape == null) {
				starter.shape = context.type.starter();
				starter.shapeY = context.type.starterY();
				starter.shapeX = Field.WIDTH / 2 + Field.BUFFER - 2 + context.type.starterX();
			}
			paths = allPathsFrom(starter);
		} else
			paths = null;
		if(context.type == ShapeType.O) { // Paint the unlikelies as impossible for O pieces
			Fitness.paintUnlikelies(context.paintedImpossible);
			for(int y = Field.BUFFER; y < Field.BUFFER + Field.HEIGHT; y++) {
				for(int x = Field.BUFFER; x < Field.BUFFER + Field.WIDTH; x++)
					if(context.paintedImpossible.field[y][x] == Block.G)
						context.paintedImpossible.field[y][x] = Block.X;
			}
		}
		for(Shape shape : context.type.orientations()) {
			for(int x = Field.BUFFER - 2; x < Field.WIDTH + Field.BUFFER + 2; x++) {
				boolean grounded = shape.intersects(context.paintedImpossible.field, x, 0);
				for(int y = 0; y < Field.HEIGHT + Field.BUFFER + 2; y++) {
					boolean groundedAbove = grounded;
					grounded = shape.intersects(paths == null ? context.paintedImpossible.field : context.original.field, x, y+1);
					if(!groundedAbove && grounded) {
						context.original.copyInto(possibility);
						possibility.shape = shape;
						possibility.shapeX = x;
						possibility.shapeY = y;
						possibility.clockTick();
						Decision option = bestFor(context.deeper(possibility));
						if(best.deeper == null || option.score < best.score) {
							Node n = new Node(shape, x, y);
							if(paths != null && !paths.containsKey(n))
								continue;
							best.bestShape = shape;
							best.bestShapeX = x;
							best.bestShapeY = y;
							if(paths != null)
								best.bestPath = paths.get(n);
							best.deeper = option.copy();
							best.score = option.score;
						}
					}
				}
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
				boolean grounded = shape.intersects(context.paintedImpossible.field, x, 0);
				for(int y = 0; y < Field.HEIGHT + Field.BUFFER + 2; y++) {
					boolean groundedAbove = grounded;
					grounded = shape.intersects(context.paintedImpossible.field, x, y+1);
					if(!groundedAbove && grounded) {
						context.original.copyInto(possibility);
						possibility.shape = shape;
						possibility.shapeX = x;
						possibility.shapeY = y;
						possibility.clockTick();
						possibility.copyInto(paintedPossibility);
						Fitness.paintImpossibles(paintedPossibility);
						double score = Fitness.score(paintedPossibility);
						score -= 10000 * Math.pow(possibility.lines - context.original.lines, 1.5);
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
		Context context = new Context(null, inPlayField, 0);
		ShapeType type = inPlayField.shape.type();
		
		Decision best = new Decision(type, Double.POSITIVE_INFINITY, context.original.copy());
		
		Field starter = inPlayField;
		Field possibility = new Field();
		Field paintedPossibility = new Field();
		Field pretick = new Field();
		
//		Set<PlayerAction> visitedActions = new HashSet<PlayerAction>();
		Map<Node, List<PlayerAction>> allPaths = allPathsFrom(inPlayField);
		for(Shape shape : type.orientations()) {
			
			for(int x = Field.BUFFER - 2; x < Field.WIDTH + Field.BUFFER + 2; x++) {
				boolean grounded = shape.intersects(context.paintedImpossible.field, x, 0);
				for(int y = 0; y < Field.HEIGHT + Field.BUFFER + 2; y++) {
					boolean groundedAbove = grounded;
					grounded = shape.intersects(context.paintedImpossible.field, x, y+1);
					if(!groundedAbove && grounded) {
						context.original.copyInto(possibility);
						possibility.shape = shape;
						possibility.shapeX = x;
						possibility.shapeY = y;
						possibility.copyInto(pretick);
						possibility.clockTick();
						possibility.copyInto(paintedPossibility);
						Fitness.paintImpossibles(paintedPossibility);
						double score = Fitness.score(paintedPossibility);
						score -= 10000 * Math.pow(possibility.lines - context.original.lines, 1.5);
						if(score < best.score) {
							List<PlayerAction> pa = allPaths.get(new Node(shape, x, y));
							if(pa == null)
								continue;
							best.bestPath = pa;
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
	
	/**
	 * Determine the shape that would be best for the player
	 * @param context
	 * @return
	 */
	public Decision bestFor(Context context) {
		Decision best = new Decision(null, Double.POSITIVE_INFINITY, context.original.copy());
		double originalScore = Fitness.scoreWithPaint(best.field);
		
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
}
