package org.tetrevil.mp;

import java.awt.Dimension;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import javax.swing.BorderFactory;
import javax.swing.SwingUtilities;

import org.tetrevil.Field;
import org.tetrevil.swing.TetrevilComponent;
import org.tetrevil.swing.TetrevilTable;

public class RemoteTetrevilTable extends TetrevilTable implements Runnable {
	protected Socket socket;
	public RemoteTetrevilTable(Socket socket) {
		super(new Field(false));
		this.socket = socket;
		
		setBorder(BorderFactory.createTitledBorder("Remote Player"));
		
		setPreferredSize(new Dimension(200, 400));
		
		new Thread(this).start();
	}
	
	@Override
	public void run() {
		try {
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			while(true) {
				final Field f = (Field) in.readObject();
				System.out.println("Received field");
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						setField(f);
						repaint();
					}
				});
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
}
