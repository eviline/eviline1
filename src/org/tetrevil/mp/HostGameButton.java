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
		name = JOptionPane.showInputDialog(this, "Please enter the name of your game");
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
			
			MultiplayerConnection.init(frame, socket);
			
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}
}
