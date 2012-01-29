package org.tetrevil.swing;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

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
		} else if(e.getKeyCode() == KeyEvent.VK_UP)
			f.rotateLeft();
		else if(e.getKeyCode() == KeyEvent.VK_DOWN)
			f.rotateRight();
		else if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			while(f.getShape() != null) {
				f.clockTick();
			}
		} else if(e.getKeyCode() == KeyEvent.VK_SPACE) {
			f.clockTick();
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
