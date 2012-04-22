package org.tetrevil;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.SynchronousQueue;

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
	
	protected SynchronousQueue<Shape> next = new SynchronousQueue<Shape>();
	protected transient Field field;
	protected transient RunnableFuture<?> future;
	
	public ConcurrentShapeProvider(Randomizer p) {
		this.provider = p;
	}
	
	@Override
	public Shape provideShape(Field f) {
		if(future == null) {
			this.field = f;
			this.future = new FutureTask<Object>(new Runnable() {
				@Override
				public void run() {
					try {
						next.put(provider.provideShape(field));
						while(true) {
							while(field.getShape() == null)
								Thread.sleep(100);
							next.put(provider.provideShape(field));
						}
					} catch(InterruptedException ie) {
					}
				}
			}, null);
			new Thread(future).start();
		}
		try {
			this.field = f;
			return next.take();
		} catch(InterruptedException ie) {
			future.cancel(true);
			throw new RuntimeException(ie);
		}
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
		return null;
	}
}
