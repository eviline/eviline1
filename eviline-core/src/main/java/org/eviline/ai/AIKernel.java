package org.eviline.ai;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eviline.Field;
import org.eviline.PlayerAction;
import org.eviline.PlayerActionNode;
import org.eviline.Shape;
import org.eviline.ShapeType;
import org.eviline.fitness.Fitness;

public interface AIKernel {
	/**
	 * Interface for objects which wish to modify the decision reached by an iteration
	 * of an AI algorithm.  Modifications include but are not limited to adjusting the
	 * score attached to a decision based on other factors, such as the recent frequency
	 * of shapes.
	 * @author robin
	 *
	 */
	public interface DecisionModifier {
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
		public AIKernel ai;
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
		public Context(AIKernel ai, DecisionModifier decisionModifier, Field original, int remainingDepth) {
			this.ai = ai;
			this.decisionModifier = decisionModifier;
			this.original = original.copy();
			this.paintedImpossible = original.copy();
			ai.getFitness().paintImpossibles(paintedImpossible);
			this.remainingDepth = remainingDepth;
		}
		
		/**
		 * Construct a {@link Context} that is a copy of this one, but for use at one level
		 * deeper in the decision tree.
		 * @param deeperOriginal
		 * @return
		 */
		public Context deeper(Field deeperOriginal) {
			return new Context(ai, decisionModifier, deeperOriginal, remainingDepth - 1);
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
		public QueueContext(AIKernel ai, Field original, ShapeType[] queue) {
			super(ai, null, original, queue.length);
			this.queue = queue;
			if(remainingDepth > 0) {
				type = queue[0];
				deeper = new QueueContext(ai, original, Arrays.copyOfRange(queue, 1, queue.length));
				deeper.shallower = this;
			}
		}
		
		@Override
		public QueueContext deeper(Field deeperOriginal) {
			QueueContext deeper = new QueueContext(ai, deeperOriginal.copy(), Arrays.copyOfRange(queue, 1, queue.length));
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
	 * A decision that can be made by the {@link DefaultAIKernel}.
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
	
	public Map<PlayerActionNode, List<PlayerAction>> allPathsFrom(
			Field field);

	/**
	 * Determine the best way for a player to play a particular queue of shapes.
	 * @param context
	 * @return
	 */
	public Decision bestFor(final QueueContext context);

	/**
	 * Determine the best way for a player to play a single shape.
	 * @param context
	 * @param type
	 * @return
	 */
	public Decision bestFor(Context context, ShapeType type);

	public Decision bestFor(Field inPlayField);

	/**
	 * Determine the shape that would be best for the player
	 * @param context
	 * @return
	 */
	public Decision bestFor(Context context);

	/**
	 * Determine the shape that would be best for the player
	 * @param context
	 * @param defaultDecision
	 * @return
	 */
	public Decision planBest(Context context, Decision defaultDecision);

	/**
	 * Determine the shape that would be worst for the player
	 * @param context
	 * @return
	 */
	public Decision worstFor(Context context);

	/**
	 * Determine the shape that would be worst for the player
	 * @param context
	 * @param defaultDecision
	 * @return
	 */
	public Decision planWorst(Context context, Decision defaultDecision);

	public boolean isHighGravity();

	public void setHighGravity(boolean highGravity);

	public boolean isHardDropOnly();

	public void setHardDropOnly(boolean hardDropOnly);

	public Fitness getFitness();

	public void setFitness(Fitness fitness);

}