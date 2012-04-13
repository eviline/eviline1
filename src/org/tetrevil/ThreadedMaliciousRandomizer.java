package org.tetrevil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.tetrevil.MaliciousRandomizer.Score;

public class ThreadedMaliciousRandomizer extends MaliciousRandomizer {
	protected static ExecutorService EXECUTOR = Executors.newCachedThreadPool();
	
	protected Map<ShapeType, MaliciousRandomizer> children = new HashMap<ShapeType, MaliciousRandomizer>();
	
	public ThreadedMaliciousRandomizer() {
		for(ShapeType type : ShapeType.values()) {
			MaliciousRandomizer r;
			children.put(type, r = new MaliciousRandomizer());
			r.provideShape(new Field());
		}
	}
	
	public ThreadedMaliciousRandomizer(int depth, int distribution) {
		super(depth, distribution);
		for(ShapeType type : ShapeType.values()) {
			MaliciousRandomizer r;
			children.put(type, r = new MaliciousRandomizer(depth, distribution));
			r.provideShape(new Field());
		}
	}
	
	@Override
	public void setAdaptive(Field field, boolean adaptive) {
		super.setAdaptive(field, adaptive);
		for(MaliciousRandomizer child : children.values()) {
			child.setAdaptive(field, adaptive);
		}
	}
	
	@Override
	public void setDepth(int depth) {
		super.setDepth(depth);
		for(MaliciousRandomizer child : children.values()) {
			child.setDepth(depth - 1);
		}
	}
	
	@Override
	public void setFair(boolean fair) {
		super.setFair(fair);
		for(MaliciousRandomizer child : children.values()) {
			child.setFair(fair);
		}
	}
	
	@Override
	public void setRfactor(double rfactor) {
		super.setRfactor(rfactor);
		for(MaliciousRandomizer child : children.values()) {
			child.setRfactor(rfactor);
		}
	}
	
	@Override
	public void adjustDistribution(int adjustment) {
		super.adjustDistribution(adjustment);
		for(MaliciousRandomizer child : children.values()) {
			child.adjustDistribution(adjustment);
		}
	}
	
	@Override
	public Shape provideShape(Field field) {
		if(randomFirst) {
			randomFirst = false;
			ShapeType type;
			do {
				type = ShapeType.values()[(int)(Math.random() * ShapeType.values().length)];
			} while(type == ShapeType.S || type == ShapeType.Z);
			return type.starter();
		}
		field = field.copyInto(new Field());
		Shape shape = decideThreaded(field).shape;
		recent.add(shape.type());
		while(recent.size() > HISTORY_SIZE)
			recent.remove(0);
		typeCounts[shape.type().ordinal()]++;
		typeCounts[(int)(typeCounts.length * Math.random())]--;
		return shape;
	}

	protected Score decideThreaded(Field field) {
		return worstForThreaded(field);
	}
	
	protected Score worstForThreaded(final Field field) {
		ShapeType omit = null;
		if(depth == 0 && recent.size() > 0) {
			omit = recent.get(0);
			for(ShapeType t : recent) {
				if(omit != t)
					omit = null;
			}
		}

		Collection<Future<Score>> futures = new ArrayList<Future<Score>>();
		for(final ShapeType type : ShapeType.values()) {
			if(type == omit)
				continue;
			futures.add(EXECUTOR.submit(new Callable<Score>() {
				@Override
				public Score call() throws Exception {
					return children.get(type).decide(field.copyInto(new Field()), 1);
				}
			}));
		}
		
		double lowestScore = Double.NEGATIVE_INFINITY;
		Score lowest = null;
		for(Future<Score> f : futures) {
			Score score;
			try {
				score = f.get();
			} catch(InterruptedException ie) {
				throw new RuntimeException(ie);
			} catch(ExecutionException ee) {
				throw new RuntimeException(ee);
			}
			if(score.score > lowestScore)
				lowest = score;
		}
		
		return lowest;
	}


}
