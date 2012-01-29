package org.tetrevil;

import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.SynchronousQueue;

public class ConcurrentShapeProvider implements ShapeProvider {

	protected SynchronousQueue<Shape> next = new SynchronousQueue<Shape>();
	protected Field field;
	protected ShapeProvider provider;
	protected RunnableFuture<?> future;
	
	public ConcurrentShapeProvider(Field f, ShapeProvider p) {
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
