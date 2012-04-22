package org.tetrevil.mp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import org.tetrevil.swing.TetrevilFrame;

public class ClientGameButton extends JButton implements ActionListener, Runnable {
	protected TetrevilFrame frame;
	protected String name;
	
	public ClientGameButton(TetrevilFrame frame) {
		super("Join Game by Name");
		this.frame = frame;
		addActionListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(name == null)
			name = JOptionPane.showInputDialog(this, "Please enter the name of their game");
		if(name == null)
			return;
		MultiplayerConnection.disableButtons(frame);
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
			while(socket.getInputStream().read() == 0)
				;
			
			socket.getOutputStream().write(0);
			socket.getInputStream().read();
			setText("Connected");
			
			MultiplayerConnection.init(frame, socket, false);
			
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	public String getGameName() {
		return name;
	}
	
	public void setGameName(String name) {
		this.name = name;
	}
}
