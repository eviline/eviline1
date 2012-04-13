package org.tetrevil.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.Timer;

import org.tetrevil.Field;
import org.tetrevil.ShapeDirection;
import org.tetrevil.ShapeType;

/**
 * {@link KeyListener} that implements the controls for tetrevil
 * @author robin
 *
 */
public class TetrevilKeyListener extends KeyAdapter {
	/**
	 * Convert a string to a key code, used when parsing parameters
	 * @param code
	 * @return
	 */
	public static int getKeyCode(String code) {
		if(code.matches("[0-9]+"))
			return Integer.parseInt(code);
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

	protected ShapeDirection dasDirection = null;
	protected boolean dasActive = false;
	protected Timer dasTimer = new Timer(DAS_TIME, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			dasActive = true;
			field.setAutoShift(dasDirection);
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
		if(e.getKeyCode() == LEFT) {
			f.shiftLeft();
			if(dasActive) {
				dasDirection = ShapeDirection.LEFT;
				field.setAutoShift(ShapeDirection.LEFT);
				field.autoshift();
			} else if(dasDirection != ShapeDirection.LEFT) {
				dasDirection = ShapeDirection.LEFT;
				dasTimer.restart();
			}
		} else if(e.getKeyCode() == RIGHT) {
			f.shiftRight();
			if(dasActive) {
				dasDirection = ShapeDirection.RIGHT;
				field.setAutoShift(ShapeDirection.RIGHT);
				field.autoshift();
			} else if(dasDirection != ShapeDirection.RIGHT) {
				dasDirection = ShapeDirection.RIGHT;
				dasTimer.restart();
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
			if(dasDirection == ShapeDirection.LEFT) {
				if(dasActive) {
					if(pressed.contains(RIGHT)) {
						field.setAutoShift(dasDirection = ShapeDirection.RIGHT);
						field.autoshift();
					} else {
						field.setAutoShift(dasDirection = null);
						dasActive = false;
					}
				}
				dasTimer.stop();
			}
		}
		if(e.getKeyCode() == RIGHT) {
			if(dasDirection == ShapeDirection.RIGHT) {
				if(dasActive) {
					if(pressed.contains(LEFT)) {
						field.setAutoShift(dasDirection = ShapeDirection.LEFT);
						field.autoshift();
					} else {
						field.setAutoShift(dasDirection = null);
						dasActive = false;
					}
				}
				dasTimer.stop();
			}
		}
	}
}
