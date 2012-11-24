package org.eviline.randomizer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.eviline.AIKernel;
import org.eviline.Field;
import org.eviline.PropertySource;
import org.eviline.Shape;
import org.eviline.ShapeType;
import org.eviline.AIKernel.Context;
import org.eviline.AIKernel.Decision;
import org.eviline.AIKernel.DecisionModifier;

public class AngelRandomizer extends ThreadedMaliciousRandomizer {
	private static final long serialVersionUID = 0;

	public AngelRandomizer(PropertySource p) {
		super(p);
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
		Decision decision = decideThreaded(field);
		Shape shape = decision.type.starter();
		taunt = decision.taunt();
		if(taunt.length() > 0)
			taunt = taunt.substring(0, 1);
		recent.add(decision.type);
		history.add(decision.type);
		while(recent.size() > HISTORY_SIZE)
			recent.remove(0);
		typeCounts[shape.type().ordinal()]++;
//		typeCounts[(int)(typeCounts.length * random.nextDouble())]--;
		if(history.size() > config.distribution() * typeCounts.length) {
			ShapeType hdrop = history.remove(0);
			typeCounts[hdrop.ordinal()]--;
		}
		return shape;
	}

	@Override
	protected Decision worstForThreaded(final Field field) {
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
				AngelRandomizer.this.permuteDecision(decision);
			}
		};
		final Context context = new Context(decisionModifier, field, depth());
		context.omit = omit;

		Collection<Future<Decision>> futures = new ArrayList<Future<Decision>>();
		for(final ShapeType type : ShapeType.values()) {
			if(type == omit)
				continue;
			futures.add(EXECUTOR.submit(new Callable<Decision>() {
				@Override
				public Decision call() throws Exception {
					
					Decision best = AIKernel.getInstance().bestFor(context, type);
					Decision bestPlannable = AIKernel.getInstance().planBest(context.deeper(best.field), best);
					best.deeper = bestPlannable;
					best.score = bestPlannable.score;
					context.decisionModifier.modifyPlannedDecision(context, best);
					return best;

				}
			}));
		}
		
		double highestDecision = Double.POSITIVE_INFINITY;
		Decision worst = null;
		for(Future<Decision> f : futures) {
			Decision score;
			try {
				score = f.get();
			} catch(InterruptedException ie) {
				throw new RuntimeException(ie);
			} catch(ExecutionException ee) {
				throw new RuntimeException(ee);
			}
			if(score.score < highestDecision) {
				worst = score;
				highestDecision = score.score;
			}
		}
		
//		worst.taunt = "";
		
		return worst;
	}

	@Override
	protected void permuteDecision(Decision typeDecision) {
		if(typeDecision.score == Double.POSITIVE_INFINITY)
			return;
		typeDecision.score *= 1 + rfactor() - 2 * rfactor() * random.nextDouble();
		if(fair()) {
			double overuse = typeCounts[typeDecision.type.ordinal()] / (double) distribution() - 1;
			typeDecision.score += Math.abs(typeDecision.score) * overuse;
		}
		if(typeDecision.type == ShapeType.O) {
			typeDecision.score += 0.2 * Math.abs(typeDecision.score);
		}
	}
	

}
