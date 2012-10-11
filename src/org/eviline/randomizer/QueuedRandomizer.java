package org.eviline.randomizer;

import java.io.ObjectStreamException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.Exchanger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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
	
	protected ExecutorService executor = Executors.newSingleThreadExecutor();
	protected Exchanger<Object> exchanger = new Exchanger<Object>();
	protected Future<?> future;

	protected volatile String taunt = "";
	
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
			Runnable task = new Runnable() {
				private Field next = initial.copyInto(new Field());
				
				@Override
				public void run() {
					try {
						while(queue.size() < size) {
							synchronized(queue) {
								Field best = bestDrop(next, queue);
								queue.offerLast(provider.provideShape(best).type());
								taunt = " ";
								for(ShapeType type : queue)
									taunt += type.name();
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
						if(taunt.length() > 0)
							taunt = taunt.substring(1);
						future = executor.submit(this);
					} catch(InterruptedException ie) {
					} catch(RuntimeException re) {
						re.printStackTrace();
						throw re;
					}
				}
			};
			future = executor.submit(task);
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
	
	private static Field bestDrop(Field field, Deque<ShapeType> queue) {
		for(ShapeType type : queue)
			field = bestDrop(field, type);
		return field;
	}

}
