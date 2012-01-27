package org.tetrevil.event;

import java.util.EventObject;

import org.tetrevil.Field;

public class TetrevilEvent extends EventObject {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Field field;
	
	public TetrevilEvent(Object source, Field field) {
		super(source);
		this.field = field;
	}
	
	public Field getField() {
		return field;
	}
}
