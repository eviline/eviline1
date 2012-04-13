package org.tetrevil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
	
	@Override
	public String getRandomizerName() {
		return MaliciousRandomizer.class.getName();
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
		
		paintImpossibles(field);

		Collection<Future<Score>> futures = new ArrayList<Future<Score>>();
		for(final ShapeType type : ShapeType.values()) {
			if(type == omit)
				continue;
			futures.add(EXECUTOR.submit(new Callable<Score>() {
				@Override
				public Score call() throws Exception {
//					MaliciousRandomizer child = new MaliciousRandomizer(depth, distribution);
//					child.setFair(fair);
//					child.setRfactor(rfactor);
					
					ThreadedMaliciousRandomizer child;
					
					ByteArrayOutputStream bout = new ByteArrayOutputStream();
					ObjectOutputStream out = new ObjectOutputStream(bout);
					out.writeObject(ThreadedMaliciousRandomizer.this);
					out.close();
					ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
					ObjectInputStream in = new ObjectInputStream(bin);
					child = (ThreadedMaliciousRandomizer) in.readObject();
					
					Field f = new Field();
					Field fc = new Field();
					
					Score typeScore = new Score();
					typeScore.score = Double.POSITIVE_INFINITY;

					for(Shape shape : type.orientations()) {
						for(int x = 0; x < Field.WIDTH; x++) {
							field.copyInto(f);
							f.setShape(shape);
							f.setShapeX(x);
							for(int y = 0; y < Field.HEIGHT + Field.BUFFER; y++) {
								f.setShapeY(y);
								if(!shape.intersects(f.getField(), x, y) && f.isGrounded()) {
									f.copyInto(fc);
									fc.clockTick();
									double fscore = Fitness.score(fc);
									if(fscore < typeScore.score) {
										typeScore.score = fscore;
										typeScore.field = fc.copyInto(typeScore.field);
										typeScore.shape = shape;
									}
								}
							}
						}
					}
					typeScore = child.decide(typeScore.field, 1);
					typeScore.score *= 1 + rfactor - 2 * rfactor * Math.random();
					if(fair)
						typeScore.score *= (distribution + distAdjustment) / (double) typeCounts[type.ordinal()];
					typeScore.shape = type.orientations()[0];
					return typeScore;
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
		
		return worst;
	}


}