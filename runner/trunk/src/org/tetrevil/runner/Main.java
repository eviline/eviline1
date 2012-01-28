package org.tetrevil.runner;

import java.awt.BorderLayout;
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
		JFrame frame = new JFrame("Tetrevil");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		
		Field field = new Field();
		field.setProvider(new EvilShapeProvider(2));
		
		TetrevilComponent c = new TetrevilComponent(field);
		c.getTable().setFocusable(true);
		KeyAdapter k;
		c.getTable().addKeyListener(k = new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.isConsumed())
					return;
				Field f = ((TetrevilTable) e.getComponent()).getField();
				if(e.getKeyCode() == KeyEvent.VK_LEFT)
					f.shiftLeft();
				else if(e.getKeyCode() == KeyEvent.VK_RIGHT)
					f.shiftRight();
				else if(e.getKeyCode() == KeyEvent.VK_UP)
					f.rotateLeft();
				else if(e.getKeyCode() == KeyEvent.VK_DOWN)
					f.clockTick();
				else if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					while(f.getShape() != null) {
						f.clockTick();
					}
				} else if(e.getKeyCode() == KeyEvent.VK_R)
					f.reset();
				else if(e.getKeyCode() == KeyEvent.VK_P)
					f.setPaused(!f.isPaused());
				else
					return;
				e.consume();
			}
		});
		frame.addKeyListener(k);
		
		field.addTetrevilListener(new TetrevilAdapter() {
			@Override
			public void gameOver(TetrevilEvent e) {
				e.getField().reset();
			}
		});
		
		frame.add(c, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);
		frame.setSize(500, 500);
		
		JOptionPane.showMessageDialog(frame, "Controls:\n\n" +
				"LEFT: Shift left 1\n" +
				"RIGHT: Shift right 1\n" +
				"UP: Rotate left\n" +
				"DOWN: Shift down 1\n" +
				"ENTER: Drop\n" +
				"P: Pause\n" +
				"R: Reset");
		
		while(true) {
			field.clockTick();
			Thread.sleep(1000);
		}
	}

}
