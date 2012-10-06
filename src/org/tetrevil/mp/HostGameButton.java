package org.tetrevil.mp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.JOptionPane;

import org.tetrevil.swing.TetrevilFrame;

public class HostGameButton extends JButton implements ActionListener, Runnable {
	protected TetrevilFrame frame;
	protected String name;
	protected boolean privateGame = false;
	
	public HostGameButton(TetrevilFrame frame) {
		super("Host Public Multiplayer");
		this.frame = frame;
		
		addActionListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		name = JOptionPane.showInputDialog(this, "Please enter the name of your game");
		if(name == null)
			return;
		MultiplayerConnection.disableButtons(frame);
		new Thread(this).start();
	}
	@Override
	public void run() {
		try {
			frame.getStart().setEnabled(false);
			frame.getDp().setEnabled(false);
			
			Socket socket = new HostSocketFactory(frame.getParameter("score_host")).newHostSocket(name, null, privateGame);
			setText("<html>Hosting multiplayer \"" + name + "\"<br>Waiting for client...</html>");
			while(socket.getInputStream().read() == 0)
				;
			

			socket.getOutputStream().write(0);
			socket.getInputStream().read();
			setText("Connected");
			
			frame.getStart().setEnabled(true);
			
			MultiplayerConnection.init(frame, socket, true);
			
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
	
	public boolean isPrivateGame() {
		return privateGame;
	}
	
	public void setPrivateGame(boolean privateGame) {
		this.privateGame = privateGame;
	}
}
