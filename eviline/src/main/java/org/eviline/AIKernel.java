package org.eviline;

import java.util.Arrays;

public class AIKernel {

	public static interface DecisionModifier {
		public void modifyPlannedDecision(Context context, Decision decision);
	}
	
	public static class Context {
		public DecisionModifier decisionModifier;
		public Field original;
		public Field paintedImpossible;
		public int remainingDepth;
		public ShapeType omit;
		
		public Context(DecisionModifier decisionModifier, Field original, int remainingDepth) {
			this.decisionModifier = decisionModifier;
			this.original = original.copy();
			this.paintedImpossible = original.copy();
			Fitness.paintImpossibles(paintedImpossible);
			this.remainingDepth = remainingDepth;
		}
		
		public Context deeper(Field deeperOriginal) {
			return new Context(decisionModifier, deeperOriginal, remainingDepth - 1);
		}
		
		@Override
		public String toString() {
			return String.valueOf(original);
		}
	}
	
	public static class QueueContext extends Context {
		public ShapeType[] queue;
		public QueueContext deeper;
		public QueueContext shallower;
		public ShapeType type;
		
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
		
		public QueueContext shallowest() {
			return shallower == null ? this : shallower.shallowest();
		}
		
		public QueueContext deepest() {
			return deeper == null ? this : deeper.deepest();
		}
	}
	
	public static class Decision {
		public double score;
		public ShapeType type;
		public Field field;
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
		public Decision copy() {
			Decision c = new Decision(type, score, field);
			c.deeper = deeper;
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
		public String taunt() {
			if(deeper == this)
				return String.valueOf(type);
			if(deeper != null)
				return type + deeper.taunt();
			return String.valueOf(type);
		}
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
	
	public Decision bestFor(QueueContext context) {
		Decision best = new Decision(context.type, context.original);
		if(context.remainingDepth == 0) {
			double score = Fitness.score(context.paintedImpossible);
			if(context.original.lines != context.shallowest().original.lines)
				score -= 100 * Math.pow(context.original.lines - context.shallowest().original.lines, 1.5);
			best.score = score;
			return best;
		}
		
		Field possibility = new Field();
		for(Shape shape : context.type.orientations()) {
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
						Decision option = bestFor(context.deeper(possibility));
						if(best.deeper == null || option.score < best.score) {
							best.deeper = option.copy();
							best.score = option.score;
						}
					}
				}
			}
		}
		
		return best.copy();
	}
	
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
						score -= 100 * Math.pow(possibility.lines - context.original.lines, 1.5);
						if(score < best.score) {
							best.score = score;
							possibility.copyInto(best.field);
						}
					}
				}
			}
		}
		
		return best;
	}
	
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
	
	public Decision planBest(Context context, Decision defaultDecision) {
		if(context.remainingDepth < 0)
			return defaultDecision;
		
		return bestFor(context);
	}
	
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
	
	public Decision planWorst(Context context, Decision defaultDecision) {
		if(context.remainingDepth < 0)
			return defaultDecision;
		
		Decision worst = worstFor(context);
		return worst;
	}
}
