package org.eviline.ai;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Callable;

import org.eviline.Field;
import org.eviline.PlayerAction;

public class DefaultPlayer extends AbstractPlayer {

	protected AIKernel ai;
	
	public DefaultPlayer(Field field, AIKernel ai) {
		super(field);
		this.ai = ai;
	}
	
	protected Callable<Queue<PlayerAction>> newComputeTask(final Field field) {
		return new Callable<Queue<PlayerAction>>() {
			@Override
			public Queue<PlayerAction> call() throws Exception {
				return new ArrayDeque<PlayerAction>(ai.bestFor(field).bestPath);
			}
		};
	}

	@Override
	protected Queue<PlayerAction> compute(Field field) {
		try {
			return newComputeTask(field).call();
		} catch(Exception ex) {
			throw new RuntimeException("Unable to compute moves", ex);
		}
	}

}
