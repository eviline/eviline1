package org.eviline.console.engine;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eviline.Field;
import org.eviline.ShapeDirection;
import org.eviline.console.gui.RerenderTetrevilListener;
import org.eviline.event.TetrevilEvent;
import org.eviline.event.TetrevilListener;

import com.googlecode.lanterna.gui.Action;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.dialog.DialogButtons;
import com.googlecode.lanterna.gui.dialog.DialogResult;
import com.googlecode.lanterna.gui.dialog.MessageBox;

public class ConsoleEngine implements TetrevilListener {
	protected Field field;
	protected GUIScreen gui;
	
	protected ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
	protected ScheduledFuture<?> tickerFuture;
	protected Runnable ticker = new Runnable() {
		@Override
		public void run() {
			field.clockTick();
		}
	};
	
	protected boolean lockDelaying;
	
	public ConsoleEngine(Field field, GUIScreen gui) {
		this.field = field;
		this.gui = gui;
		
		field.addTetrevilListener(new RerenderTetrevilListener(gui));
		field.addTetrevilListener(this);
	}
	
	protected long calculateDelay() {
		if(field.isGrounded())
			return 1000;
		int level = Math.max(field.getLines() / 10, 0);
		double fss = Math.pow(0.8 - (level - 1) * 0.007, level - 1);
		return (long)(1000 * fss);
	}
	
	public boolean isRunning() {
		return tickerFuture != null;
	}
	
	public void startEngine() {
		if(isRunning())
			return;
		field.reset();
		long delay = calculateDelay();
		tickerFuture = exec.scheduleAtFixedRate(ticker, delay, delay, TimeUnit.MILLISECONDS);
	}
	
	public void stopEngine() {
		if(!isRunning())
			return;
		tickerFuture.cancel(false);
		tickerFuture = null;
	}
	
	public void systemExit() {
		gui.getScreen().stopScreen();
		gui.getScreen().clear();
		gui.getScreen().getTerminal().setCursorVisible(true);
		System.exit(0);
	}
	
	public void promptExit() {
		field.setPaused(true);
		DialogResult r = MessageBox.showMessageBox(gui, "Really Quit?", "Really quit EVILINE?", DialogButtons.YES_NO);
		if(r == DialogResult.YES) {
			systemExit();
		}
		field.setPaused(false);
	}
	
	public void shiftDown() {
		field.clockTick();
	}
	
	public void softDrop() {
		while(!field.isGrounded())
			field.clockTick();
	}
	
	public void hardDrop() {
		softDrop();
		field.clockTick();
	}
	
	public void shiftLeft() {
		field.shiftLeft();
	}
	
	public void shiftRight() {
		field.shiftRight();
	}
	
	public void dasLeft() {
		field.setAutoShift(ShapeDirection.LEFT);
		field.autoshift();
		field.setAutoShift(null);
	}
	
	public void dasRight() {
		field.setAutoShift(ShapeDirection.RIGHT);
		field.autoshift();
		field.setAutoShift(null);
	}
	
	public void rotateLeft() {
		field.rotateLeft();
	}
	
	public void rotateRight() {
		field.rotateRight();
	}

	
	public void restartTicker() {
		if(!isRunning())
			return;
		tickerFuture.cancel(false);
		long delay = calculateDelay();
		tickerFuture = exec.scheduleAtFixedRate(ticker, delay, delay, TimeUnit.MILLISECONDS);
	}

	@Override
	public void shapeSpawned(TetrevilEvent e) {
		restartTicker();
	}

	@Override
	public void clockTicked(TetrevilEvent e) {
		if(field.isGrounded())
			restartTicker();
	}

	@Override
	public void shapeLocked(TetrevilEvent e) {
		if(!isRunning())
			return;
		tickerFuture.cancel(false);
		field.clockTick();
	}

	@Override
	public void gameOver(TetrevilEvent e) {
		gui.runInEventThread(new Action() {
			@Override
			public void doAction() {
				DialogResult r = MessageBox.showMessageBox(gui, "Game Over", "Game Over!  Reset?", DialogButtons.YES_NO);
				if(r == DialogResult.YES) {
					field.reset();
					return;
				}
				systemExit();
			}
		});
	}

	@Override
	public void shiftedLeft(TetrevilEvent e) {
		restartTicker();
	}

	@Override
	public void shiftedRight(TetrevilEvent e) {
		restartTicker();
	}

	@Override
	public void rotatedLeft(TetrevilEvent e) {
		restartTicker();
	}

	@Override
	public void rotatedRight(TetrevilEvent e) {
		restartTicker();
	}

	@Override
	public void gameReset(TetrevilEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void gamePaused(TetrevilEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void linesCleared(TetrevilEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void garbageReceived(TetrevilEvent e) {
		// TODO Auto-generated method stub
		
	}

	public Field getField() {
		return field;
	}
}
