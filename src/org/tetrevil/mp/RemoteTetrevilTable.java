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
	protected Field local;
	
	public RemoteTetrevilTable(Socket socket, Field local) {
		super(new Field(false));
		this.socket = socket;
		this.local = local;
		
		setBorder(BorderFactory.createTitledBorder("Remote Player"));
		
		setPreferredSize(new Dimension(300, 600));
		
		new Thread(this).start();
	}
	
	@Override
	public void run() {
		try {
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			while(true) {
				Object obj = in.readObject();
				if(obj instanceof Field) {
					final Field f = (Field) obj;
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							setField(f);
							repaint();
						}
					});
				}
				if(obj instanceof Integer) {
					local.garbage(((Integer) obj) - 1);
				}
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
}
