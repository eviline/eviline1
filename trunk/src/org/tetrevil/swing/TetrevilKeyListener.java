package org.tetrevil.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.Timer;

import org.tetrevil.Field;
import org.tetrevil.ShapeDirection;

public class TetrevilKeyListener extends KeyAdapter {
	public static int getKeyCode(String code) {
		code = code.toUpperCase();
		try {
			java.lang.reflect.Field kf = KeyEvent.class.getField("VK_" + code);
			return (Integer) kf.get(null);
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
	public int LEFT = KeyEvent.VK_LEFT;
	public int RIGHT = KeyEvent.VK_RIGHT;
	public int ROTATE_LEFT = KeyEvent.VK_A;
	public int ROTATE_RIGHT = KeyEvent.VK_D;
	public int DOWN = KeyEvent.VK_DOWN;
	public int DROP = KeyEvent.VK_UP;
	public int RESET = KeyEvent.VK_R;
	public int PAUSE = KeyEvent.VK_P;
	public int DAS_TIME = 350;
	
	protected Field field;

	protected Timer dasLeft = new Timer(DAS_TIME, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			field.setAutoShift(ShapeDirection.LEFT);
			field.autoshift();
		}
	});
	protected Timer dasRight = new Timer(DAS_TIME, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			field.setAutoShift(ShapeDirection.RIGHT);
			field.autoshift();
		}
	});
	
	protected Set<Integer> pressed = new HashSet<Integer>();
	
	public TetrevilKeyListener(Field field) {
		this.field = field;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.isConsumed())
			return;
		Field f = field;
		boolean consume = true;
		if(e.getKeyCode() == LEFT && !dasLeft.isRunning()) {
			f.shiftLeft();
			dasLeft.setInitialDelay(DAS_TIME);
			dasLeft.start();
		} else if(e.getKeyCode() == RIGHT & !dasRight.isRunning()) {
			f.shiftRight();
			dasRight.setInitialDelay(DAS_TIME);
			dasRight.start();
		} else if(e.getKeyCode() == ROTATE_LEFT)
			f.rotateLeft();
		else if(e.getKeyCode() == ROTATE_RIGHT)
			f.rotateRight();
		else if(e.getKeyCode() == DROP) {
			while(f.getShape() != null) {
				field.clockTick();
			}
		} else if(e.getKeyCode() == DOWN) {
			if(!pressed.contains(DOWN) || !field.isGrounded())
				field.clockTick();
		} else if(e.getKeyCode() == RESET)
			f.reset();
		else if(e.getKeyCode() == PAUSE)
			f.setPaused(!f.isPaused());
		else
			consume = false;
		if(consume)
			e.consume();
		pressed.add(e.getKeyCode());
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		pressed.remove(e.getKeyCode());
		if(e.getKeyCode() == LEFT) {
			dasLeft.stop();
			if(field.getAutoShift() == ShapeDirection.LEFT)
				field.setAutoShift(null);
		}
		if(e.getKeyCode() == RIGHT) {
			dasRight.stop();
			if(field.getAutoShift() == ShapeDirection.RIGHT)
				field.setAutoShift(null);
		}
	}
}
