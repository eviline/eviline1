package org.tetrevil.mp;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.tetrevil.Field;
import org.tetrevil.event.TetrevilAdapter;
import org.tetrevil.event.TetrevilEvent;

public class TetrevilTableSender extends TetrevilAdapter {
	protected ObjectOutputStream out;
	
	protected ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	protected Runnable flusher = new Runnable() {
		@Override
		public void run() {
			try {
				out.flush();
			} catch(IOException ioe) {
				ioe.printStackTrace();
			} finally {
				flusherFuture = null;
			}
		}
	};
	protected Future<?> flusherFuture = null;
	
	public TetrevilTableSender(ObjectOutputStream out) throws IOException {
		this.out = out;
	}
	
	protected void writePartialField(TetrevilEvent e) {
		Field f = e.getField().copyInto(new Field(false));
		f.setField(null);
		f.setProvider(null);
		try {
			out.reset();
			out.writeObject(f);
			if(flusherFuture == null) {
				out.flush();
				flusherFuture = executor.schedule(flusher, 150, TimeUnit.MILLISECONDS);
			}
		} catch(IOException ioe) {
			ioe.printStackTrace();
			e.getField().removeTetrevilListener(this);
		}
	}
	
	protected void writeFullField(TetrevilEvent e) {
		try {
			out.reset();
			out.writeObject(e.getField());
			if(flusherFuture == null) {
				out.flush();
				flusherFuture = executor.schedule(flusher, 150, TimeUnit.MILLISECONDS);
			}
		} catch(IOException ioe) {
			ioe.printStackTrace();
			e.getField().removeTetrevilListener(this);
		}
	}
	
	@Override
	public void clockTicked(TetrevilEvent e) {
		writePartialField(e);
	}
	
	@Override
	public void shapeSpawned(TetrevilEvent e) {
		writePartialField(e);
	}
	
	@Override
	public void shapeLocked(TetrevilEvent e) {
		writeFullField(e);
	}
	
	@Override
	public void linesCleared(TetrevilEvent e) {
		writeFullField(e);
		try {
			out.reset();
			out.writeObject(e.getLines());
			out.flush();
		} catch(IOException ioe) {
			ioe.printStackTrace();
			e.getField().removeTetrevilListener(this);
		}
	}
	
	@Override
	public void garbageReceived(TetrevilEvent e) {
		writeFullField(e);
	}
	
	@Override
	public void gameOver(TetrevilEvent e) {
		writeFullField(e);
	}
}
