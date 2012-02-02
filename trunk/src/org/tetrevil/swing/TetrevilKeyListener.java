package org.tetrevil.swing;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.SwingUtilities;

import org.tetrevil.Field;

public class TetrevilKeyListener extends KeyAdapter {
	public int LEFT = KeyEvent.VK_LEFT;
	public int RIGHT = KeyEvent.VK_RIGHT;
	public int ROTATE_LEFT = KeyEvent.VK_A;
	public int ROTATE_RIGHT = KeyEvent.VK_D;
	public int DOWN = KeyEvent.VK_DOWN;
	public int DROP = KeyEvent.VK_UP;
	public int RESET = KeyEvent.VK_R;
	public int PAUSE = KeyEvent.VK_P;
	
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
		if(e.getKeyCode() == LEFT) {
			f.shiftLeft();
			if(lastPressed == LEFT) {
				for(int i = 0; i < Field.WIDTH; i++)
					f.shiftLeft();
			}
		} else if(e.getKeyCode() == RIGHT) {
			f.shiftRight();
			if(lastPressed == RIGHT) {
				for(int i = 0; i < Field.WIDTH; i++)
					f.shiftRight();
			}
		} else if(e.getKeyCode() == ROTATE_LEFT)
			f.rotateLeft();
		else if(e.getKeyCode() == ROTATE_RIGHT)
			f.rotateRight();
		else if(e.getKeyCode() == DROP) {
			while(f.getShape() != null) {
				field.clockTick();
			}
		} else if(e.getKeyCode() == DOWN) {
			field.clockTick();
		} else if(e.getKeyCode() == RESET)
			f.reset();
		else if(e.getKeyCode() == PAUSE)
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
