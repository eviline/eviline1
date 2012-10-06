package org.tetrevil;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.concurrent.Exchanger;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import org.tetrevil.MaliciousRandomizer.Score;

/**
 * {@link Randomizer} which runs concurrently with game play.  This {@link Randomizer} doesn't
 * use the state of the board after a piece is locked, but rather the state of the board while the piece
 * is falling, to determine what it will return.  Really it just delegates this question to its
 * argument, which is presumed to be a slow implementation.
 * @author robin
 *
 */
public class ConcurrentShapeProvider implements Randomizer, Serializable {

	protected Randomizer provider;

	protected transient Exchanger<Object> exchanger = new Exchanger<Object>();

	protected transient RunnableFuture<?> future;

	protected transient String taunt;

	public ConcurrentShapeProvider(Randomizer p) {
		this.provider = p;
	}

	@Override
	public Shape provideShape(Field f) {
		if(future == null) {
			final Field initial = f;
			this.future = new FutureTask<Object>(new Runnable() {
				@Override
				public void run() {
					Field next = initial.copyInto(new Field());
					try {
						while(true) {
							Shape shape = provider.provideShape(next);
							taunt = shape.type() + provider.getTaunt();
							next = (Field) exchanger.exchange(shape);
							next = bestDrop(next, shape.type());
						}
					} catch(InterruptedException ie) {
					}
				}
			}, null);
			new Thread(future).start();
		}
		try {
			return (Shape) exchanger.exchange(f);
		} catch(InterruptedException ie) {
			future.cancel(true);
			throw new RuntimeException(ie);
		}
	}

	@Override
	public String getTaunt() {
		return taunt;
	}

	@Override
	public String getRandomizerName() {
		return "Concurrent " + provider.getRandomizerName();
	}

	public Object writeReplace() throws ObjectStreamException {
		return provider;
	}

	@Override
	public MaliciousRandomizer getMaliciousRandomizer() {
		return provider.getMaliciousRandomizer();
	}

	private static Field bestDrop(Field field, ShapeType type) {
		Score typeScore = new Score(); // cache.typeScore[depth];
		typeScore.score = Double.POSITIVE_INFINITY;

		field = field.copyInto(new Field());
		Fitness.paintImpossibles(field);

		for(Shape shape : type.orientations()) {
			for(int x = Field.BUFFER-2; x < Field.WIDTH + Field.BUFFER+2; x++) {
				Field f = new Field();
				field.copyInto(f);
				f.setShape(shape);
				for(int y = 0; y < Field.HEIGHT + Field.BUFFER+2; y++) {
					f.setShapeX(x);
					f.setShapeY(y);
					if(!shape.intersects(f.getField(), x, y) && f.isGrounded()) {
						Field fc = new Field();
						f.copyInto(fc);
						Fitness.unpaintImpossibles(fc);
						fc.clockTick();
						double fscore = Fitness.score(fc);
						fscore -= 1000 * Math.pow(fc.getLines() - f.getLines(), 3);
						if(fscore < typeScore.score) {
							typeScore.score = fscore;
							typeScore.field = fc.copyInto(typeScore.field);
							typeScore.shape = shape;
						}
					}
				}
			}
		}

		Fitness.unpaintImpossibles(typeScore.field);
		return typeScore.field;
	}
}
