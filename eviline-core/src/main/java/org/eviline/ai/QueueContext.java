package org.eviline.ai;

import java.util.Arrays;

import org.eviline.Field;
import org.eviline.ShapeType;

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
		
		public int depth() {
			return shallowest().queue.length - remainingDepth;
		}
	}