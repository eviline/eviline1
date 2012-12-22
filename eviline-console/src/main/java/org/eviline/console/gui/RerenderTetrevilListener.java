package org.eviline.console.gui;

import java.util.concurrent.Semaphore;

import org.eviline.event.TetrevilAdapter;
import org.eviline.event.TetrevilEvent;

import com.googlecode.lanterna.gui.Action;
import com.googlecode.lanterna.gui.GUIScreen;

public class RerenderTetrevilListener extends TetrevilAdapter {
	protected GUIScreen gui;
	protected Semaphore sync = new Semaphore(0);
	
	public RerenderTetrevilListener(GUIScreen gui) {
		this.gui = gui;
	}
	
	protected void rerenderLater() {
		gui.runInEventThread(new Action() {
			@Override
			public void doAction() {
				gui.invalidate();
			}
		});
	}
	
	protected void rerenderNow() {
		gui.runInEventThread(new Action() {
			@Override
			public void doAction() {
				gui.invalidate();
				gui.runInEventThread(new Action() {
					@Override
					public void doAction() {
						sync.release();
					}
				});
			}
		});
		sync.acquireUninterruptibly();
	}

	@Override
	public void shapeSpawned(TetrevilEvent e) {
		rerenderNow();
	}

	@Override
	public void clockTicked(TetrevilEvent e) {
		rerenderNow();
	}

	@Override
	public void shapeLocked(TetrevilEvent e) {
		rerenderLater();
	}

	@Override
	public void shiftedLeft(TetrevilEvent e) {
		rerenderLater();
	}

	@Override
	public void shiftedRight(TetrevilEvent e) {
		rerenderLater();
	}

	@Override
	public void rotatedLeft(TetrevilEvent e) {
		rerenderNow();
	}

	@Override
	public void rotatedRight(TetrevilEvent e) {
		rerenderNow();
	}
}
