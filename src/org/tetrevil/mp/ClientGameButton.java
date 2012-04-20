package org.tetrevil.mp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import org.tetrevil.swing.TetrevilFrame;

public class ClientGameButton extends JButton implements ActionListener, Runnable {
	protected TetrevilFrame frame;
	protected String name;
	
	public ClientGameButton(TetrevilFrame frame) {
		super("Connect to Multiplayer Host");
		this.frame = frame;
		addActionListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		name = JOptionPane.showInputDialog(this, "Please enter the name of their game");
		if(name == null)
			return;
		new Thread(this).start();
	}
	
	@Override
	public void run() {
		try {
			Socket socket = new ClientSocketFactory(frame.getParameter("score_host")).newClientSocket(name);
			if(socket == null) {
				JOptionPane.showMessageDialog(this, "No game with that name.");
				return;
			}
			setText("Waiting for host...");
			socket.getOutputStream().write(0);
			socket.getInputStream().read();
			setText("Connected");
			
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			
			frame.getCenter().removeAll();
			frame.getDp().setEnabled(false);
			frame.getStart().doClick();
			
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
