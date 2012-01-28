package org.tetrevil.runner;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;

import org.tetrevil.EvilShapeProvider;
import org.tetrevil.Field;
import org.tetrevil.event.TetrevilAdapter;
import org.tetrevil.event.TetrevilEvent;
import org.tetrevil.swing.TetrevilComponent;
import org.tetrevil.swing.TetrevilTable;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		JFrame frame = new JFrame("Tetrevil");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		
		Field field = new Field();
		field.setProvider(new EvilShapeProvider(2));
		
		TetrevilComponent c = new TetrevilComponent(field);
		c.getTable().setFocusable(true);
		c.getTable().addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				Field f = ((TetrevilTable) e.getComponent()).getField();
				if(e.getKeyCode() == KeyEvent.VK_LEFT)
					f.shiftLeft();
				else if(e.getKeyCode() == KeyEvent.VK_RIGHT)
					f.shiftRight();
				else if(e.getKeyCode() == KeyEvent.VK_UP)
					f.rotateLeft();
				else if(e.getKeyCode() == KeyEvent.VK_DOWN)
					f.rotateRight();
				else if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					while(f.getShape() != null) {
						f.clockTick();
					}
				} else if(e.getKeyCode() == KeyEvent.VK_R)
					f.reset();
			}
		});
		
		field.addTetrevilListener(new TetrevilAdapter() {
			@Override
			public void gameOver(TetrevilEvent e) {
				e.getField().reset();
			}
		});
		
		frame.add(c, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);
		
		while(true) {
			field.clockTick();
			Thread.sleep(1000);
		}
	}

}
