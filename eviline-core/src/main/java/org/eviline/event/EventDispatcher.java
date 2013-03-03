package org.eviline.event;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class EventDispatcher {
	protected Executor dispatchThread = Executors.newSingleThreadExecutor();

	public void shapeSpawned(final EvilineListener l, final EvilineEvent e) {
		dispatchThread.execute(new Runnable() {
			@Override
			public void run() {
				l.shapeSpawned(e);
			}
		});
	}

	public void clockTicked(final EvilineListener l, final EvilineEvent e) {
		dispatchThread.execute(new Runnable() {
			@Override
			public void run() {
				l.clockTicked(e);
			}
		});
	}

	public void shapeLocked(final EvilineListener l, final EvilineEvent e) {
		dispatchThread.execute(new Runnable() {
			@Override
			public void run() {
				l.shapeLocked(e);
			}
		});
	}

	public void gameOver(final EvilineListener l, final EvilineEvent e) {
		dispatchThread.execute(new Runnable() {
			@Override
			public void run() {
				l.gameOver(e);
			}
		});
	}

	public void shiftedLeft(final EvilineListener l, final EvilineEvent e) {
		dispatchThread.execute(new Runnable() {
			@Override
			public void run() {
				l.shiftedLeft(e);
			}
		});
	}

	public void shiftedRight(final EvilineListener l, final EvilineEvent e) {
		dispatchThread.execute(new Runnable() {
			@Override
			public void run() {
				l.shiftedRight(e);
			}
		});
	}

	public void rotatedLeft(final EvilineListener l, final EvilineEvent e) {
		dispatchThread.execute(new Runnable() {
			@Override
			public void run() {
				l.rotatedLeft(e);
			}
		});
	}

	public void rotatedRight(final EvilineListener l, final EvilineEvent e) {
		dispatchThread.execute(new Runnable() {
			@Override
			public void run() {
				l.rotatedRight(e);
			}
		});
	}

	public void gameReset(final EvilineListener l, final EvilineEvent e) {
		dispatchThread.execute(new Runnable() {
			@Override
			public void run() {
				l.gameReset(e);
			}
		});
	}

	public void gamePaused(final EvilineListener l, final EvilineEvent e) {
		dispatchThread.execute(new Runnable() {
			@Override
			public void run() {
				l.gamePaused(e);
			}
		});
	}

	public void linesCleared(final EvilineListener l, final EvilineEvent e) {
		dispatchThread.execute(new Runnable() {
			@Override
			public void run() {
				l.linesCleared(e);
			}
		});
	}

	public void garbageReceived(final EvilineListener l, final EvilineEvent e) {
		dispatchThread.execute(new Runnable() {
			@Override
			public void run() {
				l.garbageReceived(e);
			}
		});
	}
	
	
}
