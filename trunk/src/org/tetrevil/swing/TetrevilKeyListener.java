package org.tetrevil.swing;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.SwingUtilities;

import org.tetrevil.Field;

public class TetrevilKeyListener extends KeyAdapter {
	protected Field field;

	protected int lastPressed;
	
	public TetrevilKeyListener(Field field) {
		this.field = field;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.isConsumed())
			return;
		Field f = field;
		if(e.getKeyCode() == KeyEvent.VK_LEFT) {
			f.shiftLeft();
			if(lastPressed == KeyEvent.VK_LEFT) {
				for(int i = 0; i < Field.WIDTH; i++)
					f.shiftLeft();
			}
		} else if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
			f.shiftRight();
			if(lastPressed == KeyEvent.VK_RIGHT) {
				for(int i = 0; i < Field.WIDTH; i++)
					f.shiftRight();
			}
		} else if(e.getKeyCode() == KeyEvent.VK_A)
			f.rotateLeft();
		else if(e.getKeyCode() == KeyEvent.VK_D)
			f.rotateRight();
		else if(e.getKeyCode() == KeyEvent.VK_UP) {
			while(f.getShape() != null) {
				field.clockTick();
			}
		} else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
			field.clockTick();
		} else if(e.getKeyCode() == KeyEvent.VK_R)
			f.reset();
		else if(e.getKeyCode() == KeyEvent.VK_P)
			f.setPaused(!f.isPaused());
		else
			return;
		lastPressed = e.getKeyCode();
		e.consume();
	}
	
	@Override
	public void keyReleased(KeyEvent arg0) {
		lastPressed = 0;
	}
}
