package org.tetrevil.runner;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

import org.tetrevil.Field;
import org.tetrevil.swing.TetrevilComponent;
import org.tetrevil.swing.TetrevilKeyPanel;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		final JFrame frame = new JFrame("Tetrevil");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new GridBagLayout());
		frame.setBackground(Color.BLACK);
		
		Field field = new Field(true);
		
		final TetrevilComponent tc = new TetrevilComponent(field);
		
		TetrevilKeyPanel tkp = new TetrevilKeyPanel(tc.getTetrevilKeyListener());
		
//		for(String arg : args) {
//			if(arg.contains("=")) {
//				String[] f = arg.split("=", 2);
//				applet.setParameter(f[0], f[1]);
//			}
//		}
//		
//		applet.init();
		
		GridBagConstraints c = new GridBagConstraints(0, 0, 1, 2, 0, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0);
		
		frame.add(tc, c);
		c.gridx++; c.weightx = 1; frame.add(new JLabel(" "), c);
		c.gridx++; c.weightx = 0; c.gridheight = 1; c.weighty = 0; frame.add(tkp, c);
		c.gridy++; c.weighty = 1; frame.add(new JLabel(" "), c);
		frame.setUndecorated(true);
		frame.pack();
		frame.setVisible(true);
		
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
				tc.start();
			}
		});
	}

}
