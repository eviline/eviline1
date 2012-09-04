package org.tetrevil.mp;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.tetrevil.Field;
import org.tetrevil.event.TetrevilAdapter;
import org.tetrevil.event.TetrevilEvent;

public class TetrevilTableSender extends TetrevilAdapter {
	protected ObjectOutputStream out;
	
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
			out.flush();
		} catch(IOException ioe) {
			ioe.printStackTrace();
			e.getField().removeTetrevilListener(this);
		}
	}
	
	protected void writeFullField(TetrevilEvent e) {
		try {
			out.reset();
			out.writeObject(e.getField());
			out.flush();
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
