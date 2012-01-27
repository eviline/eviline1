package org.tetrevil.runner;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import org.tetrevil.Field;
import org.tetrevil.swing.TetrevilTable;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		JFrame frame = new JFrame("Tetrevil");
		frame.setLayout(new BorderLayout());
		
		Field field = new Field();
		
		frame.add(new TetrevilTable(field), BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);
		
		while(true) {
			field.clockTick();
			Thread.sleep(100);
		}
	}

}
