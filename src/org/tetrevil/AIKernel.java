package org.tetrevil;

public class AIKernel {

	public static interface DecisionModifier {
		public void modifyDecision(Context context, Decision decision);
	}
	
	public static class Context {
		public DecisionModifier decisionModifier;
		public Field original;
		public Field paintedImpossible;
		public int remainingDepth;
		
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
	}
	
	public static class Decision {
		public double score;
		public ShapeType type;
		public Field field;
		
		public Decision() {}
		public Decision(ShapeType type) {
			this.type = type;
		}
		public Decision(ShapeType type, double score) {
			this.type = type;
			this.score = score;
		}
		public Decision(ShapeType type, double score, Field field) {
			this.type = type;
			this.score = score;
			this.field = field;
		}
	}
	
	public static Decision bestFor(Context context, ShapeType type) {
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
	
	public static Decision worstFor(Context context) {
		Decision worst = new Decision(null, Double.NEGATIVE_INFINITY, context.original.copy());
		
		for(ShapeType type : ShapeType.values()) {
			Decision best = bestFor(context, type);
			Decision worstPlannable = planWorst(context.deeper(best.field), best);
			context.decisionModifier.modifyDecision(context, worstPlannable);
			if(worstPlannable.score > worst.score) {
				worst = worstPlannable;
				worst.type = type;
			}
		}
		
		return worst;
	}
	
	public static Decision planWorst(Context context, Decision defaultDecision) {
		if(context.remainingDepth <= 0)
			return defaultDecision;
		
		return worstFor(context);
	}
}
