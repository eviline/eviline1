package org.tetrevil.mp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import org.tetrevil.swing.TetrevilFrame;

public class ClientGameButton extends JButton implements ActionListener {
	protected TetrevilFrame frame;
	
	public ClientGameButton(TetrevilFrame frame) {
		super("Connect to Multiplayer Host");
		this.frame = frame;
		addActionListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		final String name = JOptionPane.showInputDialog("Please enter the name of their game");
		if(name == null)
			return;
		Runnable r = new Runnable() {
			@Override
			public void run() {
				try {
					Socket socket = new ClientSocketFactory(frame.getParameter("score_host")).newClientSocket(name);
					setText("Waiting for host...");
					socket.getOutputStream().write(0);
					socket.getInputStream().read();
					setText("Connected");
					
					frame.getField().addTetrevilListener(new TetrevilTableSender(socket.getOutputStream()));
					frame.getCenter().add(new RemoteTetrevilTable(socket, frame.getField()));
					frame.getCenter().revalidate();

				} catch(IOException ioe) {
					ioe.printStackTrace();
				}
			}
		};
		new Thread(r).start();
	}
}
