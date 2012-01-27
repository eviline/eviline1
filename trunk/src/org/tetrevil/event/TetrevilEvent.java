package org.tetrevil.event;

import java.util.EventObject;

import org.tetrevil.Field;

public class TetrevilEvent extends EventObject {
	
	protected Field field;
	
	public TetrevilEvent(Object source, Field field) {
		super(source);
		this.field = field;
	}
	
	public Field getField() {
		return field;
	}
}
