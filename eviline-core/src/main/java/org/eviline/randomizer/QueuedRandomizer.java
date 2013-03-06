package org.eviline.randomizer;

import java.io.ObjectStreamException;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.SynchronousQueue;

import org.eviline.Field;
import org.eviline.PropertySource;
import org.eviline.Shape;
import org.eviline.ShapeType;
import org.eviline.ai.AI;
import org.eviline.ai.AIKernel.Context;
import org.eviline.ai.AIKernel.Decision;
import org.eviline.ai.AIKernel.QueueContext;

public class QueuedRandomizer extends AbstractRandomizer implements Randomizer {
	protected Randomizer provider;

	protected boolean concurrent;
	protected int size;
	protected BlockingQueue<ShapeType> queue;
	
	protected ExecutorService executor = Executors.newSingleThreadExecutor();
	protected Semaphore signal;
	protected Future<?> future;
	protected Field latest;

	protected volatile String taunt = "";
	
	public QueuedRandomizer(Randomizer p, int size, boolean concurrent) {
		this.provider = p;
		this.size = size;
		this.concurrent = concurrent;
		if(size == 0)
			queue = new SynchronousQueue<ShapeType>();
		else
			queue = new LinkedBlockingQueue<ShapeType>(size);
		this.signal = new Semaphore(0);
	}
	
	@Override
	public Shape provideShape(Field field) {
		return concurrent ? concurrentProvideShape(field) : sequentialProvideShape(field);
	}

	public Shape sequentialProvideShape(Field f) {
		synchronized(queue) {
			while(queue.size() < size) {
				Field best = bestDrop(f, queue);
				queue.offer(provider.provideShape(best).type());
			}
		}
		
		taunt = "";
		for(ShapeType type : queue)
			taunt += type.name();
		
		if(size > 0)
			return queue.poll().starter();
		else
			return provider.provideShape(f);
	}
	
	public Shape concurrentProvideShape(Field f) {
		latest = f;
		if(future == null) {
			Runnable task;
			if(size == 0) {
				task = new Runnable() {
					@Override
					public void run() {
						try {
							queue.put(provider.provideShape(latest).type());
							signal.acquire();
							future = executor.submit(this);
						} catch(InterruptedException ie) {
						} catch(RuntimeException re) {
							re.printStackTrace();
							throw re;
						}
					}
				};
			} else {
				task = new Runnable() {
					@Override
					public void run() {
						try {
							while(queue.size() < size) {
								synchronized(queue) {
									Field best = bestDrop(latest, queue);
									queue.put(provider.provideShape(best).type());
									taunt = " ";
									for(ShapeType type : queue)
										taunt += type.name();
								}
							}

							signal.acquire();
							future = executor.submit(this);
						} catch(InterruptedException ie) {
						} catch(RuntimeException re) {
							re.printStackTrace();
							throw re;
						}
					}
				};
			}
			future = executor.submit(task);
		}
		try {
			return queue.take().starter();
		} catch(InterruptedException ie) {
			future.cancel(true);
			throw new RuntimeException(ie);
		} finally {
			executor.submit(new Runnable() {
				@Override
				public void run() {
					synchronized(queue) {
						taunt = " ";
						for(ShapeType type : queue)
							taunt += type.name();
					}
				}
			});
			signal.release();
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
		Context context = new Context(AI.getInstance(), null, field, 1);
		Decision decision = AI.getInstance().bestFor(context, type);
		return decision.field;
	}
	
	private static Field bestDrop(Field field, Queue<ShapeType> queue) {
//		for(ShapeType type : queue)
//			field = bestDrop(field, type);
//		return field;
		ShapeType[] sq = queue.toArray(new ShapeType[queue.size()]);
		QueueContext context = new QueueContext(AI.getInstance(), field, sq);
		Decision best = AI.getInstance().bestFor(context);
		return best.deepest().field;
	}

}
