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
		
		Fitness.paintImpossibles(field);

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
					typeScore.taunt = "" + type;

					for(Shape shape : type.orientations()) {
						for(int x = Field.BUFFER-2; x < Field.WIDTH + Field.BUFFER+2; x++) {
							field.copyInto(f);
							f.setShape(shape);
							boolean grounded = !shape.intersects(f.getField(), x, 0);
							for(int y = 0; y < Field.HEIGHT + Field.BUFFER+2; y++) {
								f.setShapeX(x);
								f.setShapeY(y);
								boolean groundedAbove = grounded;
								grounded = f.isGrounded();
								if(!groundedAbove && grounded) {
									f.copyInto(fc);
									Fitness.unpaintImpossibles(fc);
									fc.clockTick();
									Fitness.paintImpossibles(fc);
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
					typeScore = child.decide(typeScore.field, typeScore.taunt, 1);
//					typeScore.score *= 1 + rfactor - 2 * rfactor * random.nextDouble();
//					if(fair)
//						typeScore.score *= (distribution + distAdjustment) / (double) typeCounts[type.ordinal()];
					child.permuteScore(typeScore);
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
		
//		taunt = worst.taunt;
		
		return worst;
	}


}
