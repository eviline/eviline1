package org.eviline.console.gui;

import java.util.HashMap;
import java.util.Map;

import org.eviline.Field;
import org.eviline.ShapeDirection;

import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.listener.WindowAdapter;
import com.googlecode.lanterna.input.InputProvider;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.input.Key.Kind;

public class KeyboardInputHandler extends WindowAdapter {
	protected Field field;
	
	protected Map<Key, Long> lastInputTimes = new HashMap<Key, Long>();
	protected long doubleTapTime = 200;
	
	public KeyboardInputHandler(Field field) {
		this.field = field;
	}
	
	@Override
	public void onUnhandledKeyboardInteraction(Window window, Key key) {
		long now = System.currentTimeMillis();
		long lastInputTime = lastInputTimes.containsKey(key) ? lastInputTimes.get(key) : 0;
		boolean doubleTab = now - lastInputTime < doubleTapTime;
		lastInputTimes.put(key, now);
		
		if(key.getKind() == Kind.ArrowDown && !doubleTab)
			shiftDown();
		else if(key.getKind() == Kind.ArrowDown && doubleTab)
			softDrop();
		else if(key.getKind() == Kind.ArrowUp)
			hardDrop();
		else if(key.getKind() == Kind.ArrowLeft && !doubleTab)
			shiftLeft();
		else if(key.getKind() == Kind.ArrowLeft && doubleTab)
			dasLeft();
		else if(key.getKind() == Kind.ArrowRight && !doubleTab)
			shiftRight();
		else if(key.getKind() == Kind.ArrowRight && doubleTab)
			dasRight();
		else if(key.getCharacter() == 'z')
			rotateLeft();
		else if(key.getCharacter() == 'x')
			rotateRight();
	}
	
	protected void shiftDown() {
		field.clockTick();
	}
	
	protected void softDrop() {
		while(!field.isGrounded())
			field.clockTick();
	}
	
	protected void hardDrop() {
		softDrop();
		field.clockTick();
	}
	
	protected void shiftLeft() {
		field.shiftLeft();
	}
	
	protected void shiftRight() {
		field.shiftRight();
	}
	
	protected void dasLeft() {
		field.setAutoShift(ShapeDirection.LEFT);
		field.autoshift();
		field.setAutoShift(null);
	}
	
	protected void dasRight() {
		field.setAutoShift(ShapeDirection.RIGHT);
		field.autoshift();
		field.setAutoShift(null);
	}
	
	protected void rotateLeft() {
		field.rotateLeft();
	}
	
	protected void rotateRight() {
		field.rotateRight();
	}
}
