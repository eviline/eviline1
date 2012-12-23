package org.eviline.console.engine;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eviline.Field;
import org.eviline.console.gui.KeyboardInputHandler;
import org.eviline.console.gui.RerenderTetrevilListener;
import org.eviline.event.TetrevilEvent;
import org.eviline.event.TetrevilListener;

import com.googlecode.lanterna.gui.GUIScreen;

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
	
	protected void restartTicker() {
		if(!isRunning())
			return;
		tickerFuture.cancel(false);
		long delay = calculateDelay();
		tickerFuture = exec.scheduleAtFixedRate(ticker, delay, delay, TimeUnit.MILLISECONDS);
	}

	@Override
	public void shapeSpawned(TetrevilEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clockTicked(TetrevilEvent e) {
		if(field.isGrounded())
			restartTicker();
	}

	@Override
	public void shapeLocked(TetrevilEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void gameOver(TetrevilEvent e) {
		// TODO Auto-generated method stub
		
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
}
