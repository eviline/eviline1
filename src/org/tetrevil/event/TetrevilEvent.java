package org.tetrevil.event;

import java.util.EventObject;

import org.tetrevil.Field;

/**
 * Event issued by a {@link Field} to {@link TetrevilListener} objects
 * @author robin
 *
 */
public class TetrevilEvent extends EventObject {
	private static final long serialVersionUID = 1L;

	protected Field field;
	protected int lines;
	
	public TetrevilEvent(Object source, Field field) {
		super(source);
		this.field = field;
	}
	
	public TetrevilEvent(Object source, Field field, int lines) {
		this(source, field);
		this.lines = lines;
	}
	
	/**
	 * Returns the {@link Field} that this event happened on
	 * @return
	 */
	public Field getField() {
		return field;
	}
	
	/**
	 * Returns the number of lines cleared in a multiline event
	 * @return
	 */
	public int getLines() {
		return lines;
	}
}
