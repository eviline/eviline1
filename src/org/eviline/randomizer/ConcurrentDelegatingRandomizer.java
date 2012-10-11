package org.eviline.randomizer;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.concurrent.Exchanger;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

import org.eviline.AIKernel;
import org.eviline.Field;
import org.eviline.PropertySource;
import org.eviline.Shape;
import org.eviline.ShapeType;
import org.eviline.AIKernel.Context;
import org.eviline.AIKernel.Decision;

/**
 * {@link Randomizer} which runs concurrently with game play.  This {@link Randomizer} doesn't
 * use the state of the board after a piece is locked, but rather the state of the board while the piece
 * is falling, to determine what it will return.  Really it just delegates this question to its
 * argument, which is presumed to be a slow implementation.
 * @author robin
 *
 */
public class ConcurrentDelegatingRandomizer implements Randomizer, Serializable {

	protected Randomizer provider;

	protected transient Exchanger<Object> exchanger = new Exchanger<Object>();

	protected transient RunnableFuture<?> future;

	protected transient String taunt;

	public ConcurrentDelegatingRandomizer(Randomizer p) {
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
					} catch(RuntimeException re) {
						re.printStackTrace();
						throw re;
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

	public Object writeReplace() throws ObjectStreamException {
		return provider;
	}

	@Override
	public PropertySource config() {
		return provider.config();
	}

	@Override
	public String name() {
		return getClass().getName() + ":" + provider.name();
	}
	
	private static Field bestDrop(Field field, ShapeType type) {
		
		Context context = new Context(null, field, 1);
		Decision decision = AIKernel.getInstance().bestFor(context, type);
		return decision.field;
	}
}
