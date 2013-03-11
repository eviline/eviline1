package org.eviline.console.engine;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eviline.Field;
import org.eviline.ShapeDirection;
import org.eviline.console.gui.RerenderTetrevilListener;
import org.eviline.event.EvilineEvent;
import org.eviline.event.EvilineListener;

import com.googlecode.lanterna.gui.Action;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.dialog.DialogButtons;
import com.googlecode.lanterna.gui.dialog.DialogResult;
import com.googlecode.lanterna.gui.dialog.MessageBox;

public class ConsoleEngine implements EvilineListener {
	protected Field field;
	protected GUIScreen gui;
	
	protected ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
	protected ScheduledFuture<?> tickerFuture;
	protected Runnable ticker = new Runnable() {
		@Override
		public void run() {
			if(!antigravity)
				field.clockTick();
		}
	};
	protected boolean antigravity = false;
	
	protected boolean lockDelaying;
	
	public ConsoleEngine(Field field, GUIScreen gui) {
		this.field = field;
		this.gui = gui;
		
		field.addEvilineListener(new RerenderTetrevilListener(gui));
		field.addEvilineListener(this);
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
		field.softDrop();
	}
	
	public void hardDrop() {
		field.hardDrop();
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
	public void shapeSpawned(EvilineEvent e) {
		restartTicker();
	}

	@Override
	public void clockTicked(EvilineEvent e) {
		if(field.isGrounded())
			restartTicker();
	}

	@Override
	public void shapeLocked(EvilineEvent e) {
		if(!isRunning())
			return;
		tickerFuture.cancel(false);
		if(!antigravity)
			field.clockTick();
	}

	@Override
	public void gameOver(EvilineEvent e) {
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
	public void shiftedLeft(EvilineEvent e) {
		restartTicker();
	}

	@Override
	public void shiftedRight(EvilineEvent e) {
		restartTicker();
	}

	@Override
	public void rotatedLeft(EvilineEvent e) {
		restartTicker();
	}

	@Override
	public void rotatedRight(EvilineEvent e) {
		restartTicker();
	}

	@Override
	public void gameReset(EvilineEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void gamePaused(EvilineEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void linesCleared(EvilineEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void garbageReceived(EvilineEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hardDropped(EvilineEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public Field getField() {
		return field;
	}

	public ScheduledFuture<?> getTickerFuture() {
		return tickerFuture;
	}

	public boolean isAntigravity() {
		return antigravity;
	}

	public void setAntigravity(boolean antigravity) {
		this.antigravity = antigravity;
	}
}
