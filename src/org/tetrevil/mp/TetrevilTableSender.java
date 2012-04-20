package org.tetrevil.mp;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.tetrevil.event.TetrevilAdapter;
import org.tetrevil.event.TetrevilEvent;

public class TetrevilTableSender extends TetrevilAdapter {
	protected ObjectOutputStream out;
	
	public TetrevilTableSender(OutputStream out) throws IOException {
		this.out = new ObjectOutputStream(out);
	}
	
	@Override
	public void clockTicked(TetrevilEvent e) {
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
	public void linesCleared(TetrevilEvent e) {
		try {
			out.reset();
			out.writeObject(e.getLines());
			out.writeObject(e.getField());
			out.flush();
		} catch(IOException ioe) {
			ioe.printStackTrace();
			e.getField().removeTetrevilListener(this);
		}
	}
}
