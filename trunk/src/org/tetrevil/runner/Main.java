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
import org.tetrevil.swing.TetrevilFrame;
import org.tetrevil.swing.TetrevilKeyPanel;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		Field field = new Field(true);

		final TetrevilFrame frame = new TetrevilFrame(field);

		for(String arg : args) {
			if(arg.contains("=")) {
				String[] f = arg.split("=", 2);
				frame.setParameter(f[0], f[1]);
			}
		}
		
		frame.init();
		
		frame.setUndecorated(true);
		frame.pack();
		frame.setVisible(true);
		
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
			}
		});
	}

}
