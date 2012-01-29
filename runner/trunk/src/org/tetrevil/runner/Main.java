package org.tetrevil.runner;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.tetrevil.EvilShapeProvider;
import org.tetrevil.Field;
import org.tetrevil.event.TetrevilAdapter;
import org.tetrevil.event.TetrevilEvent;
import org.tetrevil.swing.TetrevilComponent;
import org.tetrevil.swing.TetrevilTable;

import com.sun.corba.se.impl.protocol.giopmsgheaders.KeyAddr;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		final JFrame frame = new JFrame("Tetrevil");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		
		final MainApplet applet = new MainApplet();
		applet.init();
		
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				frame.add(applet, BorderLayout.CENTER);
				frame.pack();
				frame.setSize(500, 500);
				
				frame.setVisible(true);
				applet.start();
			}
		});
		
	}

}
