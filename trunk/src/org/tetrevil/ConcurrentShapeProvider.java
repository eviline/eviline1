package org.tetrevil;

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
public class ConcurrentShapeProvider implements Randomizer {

	protected SynchronousQueue<Shape> next = new SynchronousQueue<Shape>();
	protected Field field;
	protected Randomizer provider;
	protected RunnableFuture<?> future;
	
	public ConcurrentShapeProvider(Field f, Randomizer p) {
		this.field = f;
		this.provider = p;
		
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
	
	@Override
	public Shape provideShape(Field field) {
		try {
			return next.take();
		} catch(InterruptedException ie) {
			future.cancel(true);
			throw new RuntimeException(ie);
		}
	}

}
