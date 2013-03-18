package org.eviline.ai;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

import org.eviline.Field;
import org.eviline.PlayerAction;
import org.eviline.ShapeType;

public class DefaultPlayer extends AbstractPlayer {

	
	protected AIKernel ai;
	
	protected ExecutorService executor = null;
	protected Future<Queue<PlayerAction>> computeFuture;
	protected boolean blocking;
	
	public DefaultPlayer(Field field, AIKernel ai) {
		super(field);
		this.ai = ai;
		setBlocking(true);
	}
	
	protected Callable<Queue<PlayerAction>> newComputeTask(final Field field) {
		return new Callable<Queue<PlayerAction>>() {
			@Override
			public Queue<PlayerAction> call() throws Exception {
				if(field.getShape() == null)
					return new ArrayDeque<PlayerAction>();
				List<ShapeType> next = field.getProvider().getNext();
				if(next.size() == 0)
					return new ArrayDeque<PlayerAction>(ai.bestFor(field).bestPath);
				next.add(0, field.getShape().type());
				return new ArrayDeque<PlayerAction>(ai.bestFor(new QueueContext(ai, field, next.toArray(new ShapeType[0]))).bestPath);
			}
		};
	}

	@Override
	protected Queue<PlayerAction> compute(Field field) {
		Callable<Queue<PlayerAction>> task = newComputeTask(field);
		try {
			if(blocking)
				return task.call();
			else if(computeFuture == null) {
				computeFuture = executor.submit(task);
				return null;
			}
			else if(computeFuture.isDone()) {
				Queue<PlayerAction> moves = computeFuture.get();
				computeFuture = null;
				return moves;
			} else {
				return null;
			}
				
		} catch(Exception ex) {
			throw new RuntimeException("Unable to compute moves", ex);
		}
	}

	public AIKernel getAi() {
		return ai;
	}

	public void setAi(AIKernel ai) {
		this.ai = ai;
	}

	public boolean isBlocking() {
		return blocking;
	}

	public void setBlocking(boolean blocking) {
		this.blocking = blocking;
		if(blocking && executor != null)
			executor.shutdownNow();
		if(!blocking && executor == null)
			executor = Executors.newSingleThreadExecutor(new ThreadFactory() {
				@Override
				public Thread newThread(Runnable r) {
					Thread t = new Thread(r);
					t.setName(DefaultPlayer.this.toString());
					t.setDaemon(true);
					return t;
				}
			});
	}

	public Deque<PlayerAction> getMoves() {
		return moves;
	}

}
