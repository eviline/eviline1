package org.tetrevil.runner;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import org.tetrevil.Field;
import org.tetrevil.swing.TetrevilComponent;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		JFrame frame = new JFrame("Tetrevil");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		
		Field field = new Field();
		
		frame.add(new TetrevilComponent(field), BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);
		
		while(true) {
			field.clockTick();
			Thread.sleep(100);
		}
	}

}
