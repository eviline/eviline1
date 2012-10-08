package org.tetrevil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.tetrevil.AIKernel.Context;
import org.tetrevil.AIKernel.Decision;
import org.tetrevil.AIKernel.DecisionModifier;

public class AngelRandomizer extends ThreadedMaliciousRandomizer {
	private static final long serialVersionUID = 0;

	public AngelRandomizer() {
		super();
	}

	public AngelRandomizer(int depth, int distribution) {
		super(depth, distribution);
	}

	@Override
	public String getRandomizerName() {
		return getClass().getName();
	}
	
	@Override
	public Shape provideShape(Field field) {
		if(randomFirst) {
			randomFirst = false;
			ShapeType type;
			do {
				type = ShapeType.values()[(int)(random.nextDouble() * ShapeType.values().length)];
			} while(type == ShapeType.S || type == ShapeType.Z);
			return type.starter();
		}
		field = field.copyInto(new Field());
		Score score = decideThreaded(field);
		Shape shape = score.shape;
		taunt = score.taunt;
		if(taunt.length() > 0)
			taunt = taunt.substring(0, 1);
		recent.add(shape.type());
		while(recent.size() > HISTORY_SIZE)
			recent.remove(0);
		typeCounts[shape.type().ordinal()]++;
		typeCounts[(int)(typeCounts.length * random.nextDouble())]--;
		return shape;
	}
	
	@Override
	protected Score worstFor(Field field, String taunt, int depth) {
		ShapeType omit = null;
		if(depth == 0 && recent.size() > 0) {
			omit = recent.get(0);
			for(ShapeType t : recent) {
				if(omit != t)
					omit = null;
			}
		}
		final ShapeType fomit = omit;

		DecisionModifier decisionModifier = new DecisionModifier() {
			@Override
			public void modifyPlannedDecision(Context context, Decision decision) {
				if(decision.type == fomit) {
					decision.score = Double.POSITIVE_INFINITY;
					return;
				}
				Score s = new Score(decision);
				permuteScore(s);
				decision.score = s.score;
			}
		};
		Context context = new Context(decisionModifier, field, this.depth - depth);
		context.omit = omit;
		Decision defaultDecision = new Decision();
		defaultDecision.field = field.copy();
		defaultDecision.score = Fitness.scoreWithPaint(defaultDecision.field);
		Decision decision = AIKernel.getInstance().planBest(context, defaultDecision);
		
		return new Score(decision);

	}

	@Override
	protected Score worstForThreaded(final Field field) {
		ShapeType omit = null;
		if(recent.size() > 0) {
			omit = recent.get(0);
			for(ShapeType t : recent) {
				if(omit != t)
					omit = null;
			}
		}
		
		DecisionModifier decisionModifier = new DecisionModifier() {
			@Override
			public void modifyPlannedDecision(Context context, Decision decision) {
				Score s = new Score(decision);
				AngelRandomizer.this.permuteScore(s);
				decision.score = s.score;
			}
		};
		final Context context = new Context(decisionModifier, field, depth);
		context.omit = omit;

		Collection<Future<Score>> futures = new ArrayList<Future<Score>>();
		for(final ShapeType type : ShapeType.values()) {
			if(type == omit)
				continue;
			futures.add(EXECUTOR.submit(new Callable<Score>() {
				@Override
				public Score call() throws Exception {
					
					Decision best = AIKernel.getInstance().bestFor(context, type);
					Decision bestPlannable = AIKernel.getInstance().planBest(context.deeper(best.field), best);
					bestPlannable.type = type;
					context.decisionModifier.modifyPlannedDecision(context, bestPlannable);
					return new Score(bestPlannable);

				}
			}));
		}
		
		double highestScore = Double.POSITIVE_INFINITY;
		Score worst = null;
		for(Future<Score> f : futures) {
			Score score;
			try {
				score = f.get();
			} catch(InterruptedException ie) {
				throw new RuntimeException(ie);
			} catch(ExecutionException ee) {
				throw new RuntimeException(ee);
			}
			if(score.score < highestScore) {
				worst = score;
				highestScore = score.score;
			}
		}
		
//		worst.taunt = "";
		
		return worst;
	}

	@Override
	protected void permuteScore(Score typeScore) {
		if(typeScore.score == Double.POSITIVE_INFINITY)
			return;
		typeScore.score *= 1 + rfactor - 2 * rfactor * random.nextDouble();
		if(fair)
			typeScore.score *= (distribution + distAdjustment) / (double) typeCounts[typeScore.shape.type().ordinal()];
		if(typeScore.shape.type() == ShapeType.O) {
			typeScore.score += 0.2 * Math.abs(typeScore.score);
		}
	}
	

}
