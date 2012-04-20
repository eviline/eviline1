package org.tetrevil.mp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import org.tetrevil.swing.TetrevilFrame;
import org.tetrevil.swing.TetrevilTable;

public class HostGameButton extends JButton implements ActionListener {
	protected TetrevilFrame frame;
	
	public HostGameButton(TetrevilFrame frame) {
		super("Host Multiplayer");
		this.frame = frame;
		
		addActionListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		final String name = JOptionPane.showInputDialog("Please enter the name of your game");
		if(name == null)
			return;
		Runnable r = new Runnable() {
			@Override
			public void run() {
				try {
					Socket socket = new HostSocketFactory(frame.getParameter("score_host")).newHostSocket(name, null);
					setText("Waiting for client...");
					socket.getOutputStream().write(0);
					socket.getInputStream().read();
					setText("Connected");
					
					frame.getField().addTetrevilListener(new TetrevilTableSender(socket.getOutputStream()));
					frame.getCenter().add(new RemoteTetrevilTable(socket));
					frame.getCenter().revalidate();
					
				} catch(IOException ioe) {
					ioe.printStackTrace();
				}
			}
		};
		new Thread(r).start();
	}
}
