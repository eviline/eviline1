package org.eviline.mp;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.eviline.Field;
import org.eviline.event.EvilineAdapter;
import org.eviline.event.EvilineEvent;

public class TetrevilTableSender extends EvilineAdapter {
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
	
	protected void writePartialField(EvilineEvent e) {
		Field f = e.getField().copyInto(new Field());
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
			e.getField().removeEvilineListener(this);
		}
	}
	
	protected void writeFullField(EvilineEvent e) {
		try {
			out.reset();
			out.writeObject(e.getField());
			if(flusherFuture == null) {
				out.flush();
				flusherFuture = executor.schedule(flusher, 150, TimeUnit.MILLISECONDS);
			}
		} catch(IOException ioe) {
			ioe.printStackTrace();
			e.getField().removeEvilineListener(this);
		}
	}
	
	@Override
	public void clockTicked(EvilineEvent e) {
		writePartialField(e);
	}
	
	@Override
	public void shapeSpawned(EvilineEvent e) {
		writePartialField(e);
	}
	
	@Override
	public void shapeLocked(EvilineEvent e) {
		writeFullField(e);
	}
	
	@Override
	public void linesCleared(EvilineEvent e) {
		writeFullField(e);
		try {
			out.reset();
			out.writeObject(e.getLines());
			out.flush();
		} catch(IOException ioe) {
			ioe.printStackTrace();
			e.getField().removeEvilineListener(this);
		}
	}
	
	@Override
	public void garbageReceived(EvilineEvent e) {
		writeFullField(e);
	}
	
	@Override
	public void gameOver(EvilineEvent e) {
		writeFullField(e);
	}
}
