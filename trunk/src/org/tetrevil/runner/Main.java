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
		
		for(String arg : args) {
			if(arg.contains("=")) {
				String[] f = arg.split("=", 2);
				applet.setParameter(f[0], f[1]);
			}
		}
		
		applet.init();
		
		frame.add(applet, BorderLayout.CENTER);
		frame.pack();
		frame.setSize(260, 500);
		
		frame.setVisible(true);
		
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				applet.start();
			}
		});
	}

}
