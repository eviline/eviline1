package org.eviline.randomizer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.eviline.Field;
import org.eviline.PropertySource;
import org.eviline.Shape;
import org.eviline.ShapeType;
import org.eviline.ai.AI;
import org.eviline.ai.Context;
import org.eviline.ai.Decision;
import org.eviline.ai.DecisionModifier;
import org.funcish.core.para.ParaExecutors;

public class ThreadedMaliciousRandomizer extends MaliciousRandomizer {
	public static ExecutorService EXECUTOR = ParaExecutors.AVAILABLE_X2;
	private static final long serialVersionUID = -2530461350140162944L;
	
	public ThreadedMaliciousRandomizer(PropertySource p) {
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
//		taunt = score.taunt;
		recent.add(shape.type());
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
	
	protected Decision decideThreaded(Field field) {
		return worstForThreaded(field);
	}
	
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
				ThreadedMaliciousRandomizer.this.permuteDecision(decision);
			}
		};
		final Context context = new Context(AI.getInstance(), decisionModifier, field, depth());
		
		Collection<Future<Decision>> futures = new ArrayList<Future<Decision>>();
		for(final ShapeType type : ShapeType.values()) {
			if(type == omit)
				continue;
			futures.add(EXECUTOR.submit(new Callable<Decision>() {
				@Override
				public Decision call() throws Exception {
					
					Decision best = AI.getInstance().bestFor(context, type);
					Decision worstPlannable = AI.getInstance().planWorst(context.deeper(best.field), best);
					best.deeper = worstPlannable;
					best.score = worstPlannable.score;
					context.decisionModifier.modifyPlannedDecision(context, best);
					return best;
					
				}
			}));
		}
		
		double highestDecision = Double.NEGATIVE_INFINITY;
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
			if(score.score > highestDecision) {
				worst = score;
				highestDecision = score.score;
			}
		}
		
//		taunt = worst.taunt;
		
		return worst;
	}


}
