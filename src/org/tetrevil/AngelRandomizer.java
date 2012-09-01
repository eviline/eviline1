package org.tetrevil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.tetrevil.MaliciousRandomizer.Score;

public class AngelRandomizer extends ThreadedMaliciousRandomizer {

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
	
	
	protected Score worstFor(Field field, int depth) {
		ShapeType omit = null;
		if(depth == 0 && recent.size() > 0) {
			omit = recent.get(0);
			for(ShapeType t : recent) {
				if(omit != t)
					omit = null;
			}
		}

		Score worst = new Score(); // cache.worst[depth];
		worst.score = Double.POSITIVE_INFINITY;
		
		paintImpossibles(field);
		double startScore = Fitness.score(field);
		
		Field f = new Field(false); // cache.f[depth];
		Field fc = new Field(false); // cache.fc[depth];
		for(ShapeType type : ShapeType.values()) {
			Score typeScore = new Score(); // cache.typeScore[depth];
			typeScore.score = Double.POSITIVE_INFINITY;

			for(Shape shape : type.orientations()) {
				for(int x = Field.BUFFER-2; x < Field.WIDTH + Field.BUFFER+2; x++) {
					field.copyInto(f);
					f.setShape(shape);
					for(int y = 0; y < Field.HEIGHT + Field.BUFFER+2; y++) {
						f.setShapeX(x);
						f.setShapeY(y);
						if(!shape.intersects(f.getField(), x, y) && f.isGrounded()) {
							f.copyInto(fc);
							fc.clockTick();
							paintImpossibles(fc);
							double fscore = Fitness.score(fc);
							fscore -= 1000 * (fc.getLines() - f.getLines());
							if(fscore < typeScore.score) {
								typeScore.score = fscore;
								typeScore.field = fc.copyInto(typeScore.field);
								typeScore.shape = shape;
							}
						}
					}
				}
			}
			if(depth < this.depth)
				typeScore = decide(typeScore.field, depth + 1);
			typeScore.score *= 1 + rfactor - 2 * rfactor * random.nextDouble();
			if(fair)
				typeScore.score *= (distribution + distAdjustment) / (double) typeCounts[type.ordinal()];
			permuteScore(typeScore);
			typeScore.shape = type.orientations()[0];
			if(typeScore.score < worst.score && omit != typeScore.shape.type()) {
				worst.score = typeScore.score;
				worst.field = typeScore.field.copyInto(worst.field);
				worst.shape = typeScore.shape;
			}
		}
		
		worst.score /= depth + 1;
		worst.score += startScore;
		
		return worst;
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
					out.writeObject(AngelRandomizer.this);
					out.close();
					ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
					ObjectInputStream in = new ObjectInputStream(bin);
					child = (ThreadedMaliciousRandomizer) in.readObject();
					
					Field f = new Field();
					Field fc = new Field();
					
					Score typeScore = new Score();
					typeScore.score = Double.POSITIVE_INFINITY;

					for(Shape shape : type.orientations()) {
						for(int x = Field.BUFFER-2; x < Field.WIDTH + Field.BUFFER+2; x++) {
							field.copyInto(f);
							f.setShape(shape);
							for(int y = 0; y < Field.HEIGHT + Field.BUFFER+2; y++) {
								f.setShapeX(x);
								f.setShapeY(y);
								if(!shape.intersects(f.getField(), x, y) && f.isGrounded()) {
									f.copyInto(fc);
									fc.clockTick();
									paintImpossibles(fc);
									double fscore = Fitness.score(fc);
									fscore -= 1000 * (fc.getLines() - f.getLines());
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
					typeScore.score *= 1 + rfactor - 2 * rfactor * random.nextDouble();
					if(fair)
						typeScore.score *= (distribution + distAdjustment) / (double) typeCounts[type.ordinal()];
					permuteScore(typeScore);
					typeScore.shape = type.orientations()[0];
					return typeScore;
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
		
		return worst;
	}

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