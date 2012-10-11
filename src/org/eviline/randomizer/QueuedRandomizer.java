package org.eviline.randomizer;

import java.io.ObjectStreamException;
import java.util.ArrayDeque;
import java.util.Deque;
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

public class QueuedRandomizer implements Randomizer {
	protected Randomizer provider;

	protected boolean concurrent;
	protected int size;
	protected Deque<ShapeType> queue = new ArrayDeque<ShapeType>();
	
	protected Exchanger<Object> exchanger = new Exchanger<Object>();
	protected RunnableFuture<?> future;

	public QueuedRandomizer(Randomizer p, int size, boolean concurrent) {
		this.provider = p;
		this.size = size;
		this.concurrent = concurrent;
	}
	
	@Override
	public Shape provideShape(Field field) {
		return concurrent ? concurrentProvideShape(field) : sequentialProvideShape(field);
	}

	public Shape sequentialProvideShape(Field f) {
		synchronized(queue) {
			while(queue.size() < size) {
				Field best = bestDrop(f, queue);
				queue.offerLast(provider.provideShape(best).type());
			}
		}
		
		if(size > 0)
			return queue.pollFirst().starter();
		else
			return provider.provideShape(f);
	}
	
	public Shape concurrentProvideShape(Field f) {
		if(future == null) {
			final Field initial = f;
			this.future = new FutureTask<Object>(new Runnable() {
				@Override
				public void run() {
					Field next = initial.copyInto(new Field());
					try {
						while(true) {
							
							synchronized(queue) {
								while(queue.size() < size) {
									Field best = bestDrop(next, queue);
									queue.offerLast(provider.provideShape(best).type());
								}
							}
							
							Shape shape;
							if(size > 0)
								shape = queue.peekFirst().starter();
							else
								shape = provider.provideShape(next);
							next = (Field) exchanger.exchange(shape);
							synchronized(queue) {
								queue.pollFirst();
							}
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
		StringBuilder sb = new StringBuilder(" ");
		synchronized(queue) {
			for(ShapeType type : queue)
				sb.append(type.name());
		}
		return sb.toString();
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
	
	private static Field bestDrop(Field field, Deque<ShapeType> queue) {
		for(ShapeType type : queue)
			field = bestDrop(field, type);
		return field;
	}

}
