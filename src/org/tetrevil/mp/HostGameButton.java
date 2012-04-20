package org.tetrevil.mp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import org.tetrevil.swing.TetrevilFrame;
import org.tetrevil.swing.TetrevilTable;

public class HostGameButton extends JButton implements ActionListener, Runnable {
	protected TetrevilFrame frame;
	protected String name;
	
	public HostGameButton(TetrevilFrame frame) {
		super("Host Multiplayer");
		this.frame = frame;
		
		addActionListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		name = JOptionPane.showInputDialog("Please enter the name of your game");
		if(name == null)
			return;
		new Thread(this).start();
	}
	@Override
	public void run() {
		try {
			Socket socket = new HostSocketFactory(frame.getParameter("score_host")).newHostSocket(name, null);
			setText("Waiting for client...");
			socket.getOutputStream().write(0);
			socket.getInputStream().read();
			setText("Connected");
			
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			
			frame.getCenter().removeAll();
			frame.getDp().setEnabled(false);
			frame.getStart().doClick();
			
			out.writeObject(frame.getField().getProvider());
			
			frame.getField().setUnpausable(true);
			
			frame.getField().addTetrevilListener(new TetrevilTableSender(out));
			frame.getCenter().add(new RemoteTetrevilTable(socket, frame.getField()));
			frame.getCenter().revalidate();
			frame.repaint();
			
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}
}
