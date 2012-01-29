package org.tetrevil.runner;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import javax.swing.JFrame;

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
