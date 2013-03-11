package org.eviline.console.gui;

import java.util.concurrent.Semaphore;

import org.eviline.event.EvilineAdapter;
import org.eviline.event.EvilineEvent;

import com.googlecode.lanterna.gui.Action;
import com.googlecode.lanterna.gui.GUIScreen;

public class RerenderTetrevilListener extends EvilineAdapter {
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
	public void shapeSpawned(EvilineEvent e) {
		rerenderNow();
	}

	@Override
	public void clockTicked(EvilineEvent e) {
		rerenderLater();
	}

	@Override
	public void shapeLocked(EvilineEvent e) {
		rerenderLater();
	}

	@Override
	public void shiftedLeft(EvilineEvent e) {
		rerenderLater();
	}

	@Override
	public void shiftedRight(EvilineEvent e) {
		rerenderLater();
	}

	@Override
	public void rotatedLeft(EvilineEvent e) {
		rerenderLater();
	}

	@Override
	public void rotatedRight(EvilineEvent e) {
		rerenderLater();
	}
}
