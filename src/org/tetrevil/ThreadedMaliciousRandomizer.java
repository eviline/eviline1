package org.tetrevil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.tetrevil.AIKernel.Context;
import org.tetrevil.AIKernel.Decision;
import org.tetrevil.AIKernel.DecisionModifier;

public class ThreadedMaliciousRandomizer extends MaliciousRandomizer {
	public static ExecutorService EXECUTOR = Executors.newCachedThreadPool();
	private static final long serialVersionUID = -2530461350140162944L;
	
	public ThreadedMaliciousRandomizer() {
	}
	
	public ThreadedMaliciousRandomizer(int depth, int distribution) {
		super(depth, distribution);
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
//		taunt = score.taunt;
		recent.add(shape.type());
		while(recent.size() > HISTORY_SIZE)
			recent.remove(0);
		typeCounts[shape.type().ordinal()]++;
		typeCounts[(int)(typeCounts.length * random.nextDouble())]--;
		return shape;
	}
	
	@Override
	public String getRandomizerName() {
		return MaliciousRandomizer.class.getName();
	}

	protected Score decideThreaded(Field field) {
		return worstForThreaded(field);
	}
	
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
				ThreadedMaliciousRandomizer.this.permuteScore(s);
				decision.score = s.score;
			}
		};
		final Context context = new Context(decisionModifier, field, depth - 1);
		context.omit = omit;
		
		Collection<Future<Score>> futures = new ArrayList<Future<Score>>();
		for(final ShapeType type : ShapeType.values()) {
			if(type == omit)
				continue;
			futures.add(EXECUTOR.submit(new Callable<Score>() {
				@Override
				public Score call() throws Exception {
					
					Decision best = AIKernel.getInstance().bestFor(context, type);
					Decision worstPlannable = AIKernel.getInstance().planWorst(context, best);
					context.decisionModifier.modifyPlannedDecision(context, worstPlannable);
					Score score = new Score(worstPlannable);
					return score;
					
				}
			}));
		}
		
		double highestScore = Double.NEGATIVE_INFINITY;
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
			if(score.score > highestScore) {
				worst = score;
				highestScore = score.score;
			}
		}
		
//		taunt = worst.taunt;
		
		return worst;
	}


}
