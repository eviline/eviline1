package org.eviline.swing;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.Timer;

import org.eviline.Field;
import org.eviline.ShapeDirection;
import org.eviline.event.EvilineAdapter;
import org.eviline.event.EvilineEvent;

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
	public int DOWN_TIME = 50;
	
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
	
	protected Timer downTimer = new Timer(50, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			if(!field.isGrounded())
				field.shiftDown();
		}
	});
	
	protected Set<Integer> pressed = new HashSet<Integer>();
	
	public TetrevilKeyListener(Field field) {
		this.field = field;
		field.addEvilineListener(new EvilineAdapter() {
			@Override
			public void shapeLocked(EvilineEvent e) {
				downTimer.stop();
			}
		});
		updateTimers();
	}
	
	public void updateTimers() {
		dasTimer.setInitialDelay(DAS_TIME);
		dasTimer.setDelay(DAS_TIME);
		downTimer.setInitialDelay(DOWN_TIME);
		downTimer.setDelay(DOWN_TIME);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.isConsumed())
			return;
		Field f = field;
		boolean consume = true;
		if(e.getKeyCode() == LEFT && !pressed.contains(LEFT)) {
			f.shiftLeft();
			dasDirection = ShapeDirection.LEFT;
			dasTimer.restart();
		} else if(e.getKeyCode() == RIGHT && !pressed.contains(RIGHT)) {
			f.shiftRight();
			dasDirection = ShapeDirection.RIGHT;
			dasTimer.restart();
		} else if(e.getKeyCode() == ROTATE_LEFT)
			f.rotateLeft();
		else if(e.getKeyCode() == ROTATE_RIGHT)
			f.rotateRight();
		else if(e.getKeyCode() == DROP) {
			if(!pressed.contains(DROP)) {
				field.hardDrop();
			}
		} else if(e.getKeyCode() == DOWN) {
			if(!pressed.contains(DOWN)) {
				field.shiftDown();
				downTimer.start();
			}
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
					field.setAutoShift(dasDirection = null);
					dasActive = false;
				}
				dasTimer.stop();
			}
		}
		if(e.getKeyCode() == RIGHT) {
			if(dasDirection == ShapeDirection.RIGHT) {
				if(dasActive) {
					field.setAutoShift(dasDirection = null);
					dasActive = false;
				}
				dasTimer.stop();
			}
		}
		if(e.getKeyCode() == DOWN) {
			downTimer.stop();
		}
	}
}
