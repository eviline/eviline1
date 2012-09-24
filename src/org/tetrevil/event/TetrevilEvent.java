package org.tetrevil.event;

import java.util.EventObject;

import org.tetrevil.Field;
import org.tetrevil.Shape;

/**
 * Event issued by a {@link Field} to {@link TetrevilListener} objects
 * @author robin
 *
 */
public class TetrevilEvent extends EventObject {
	private static final long serialVersionUID = 1L;

	public static final int CLOCK_TICKED = 0;
	public static final int GAME_OVER = 1;
	public static final int SHIFTED_LEFT = 2;
	public static final int SHIFTED_RIGHT = 3;
	public static final int ROTATED_LEFT = 4;
	public static final int ROTATED_RIGHT = 5;
	public static final int GAME_RESET = 6;
	public static final int GAME_PAUSED = 7;
	public static final int LINES_CLEARED = 8;
	public static final int GARBAGE_RECEIVED = 9;
	public static final int SHAPE_SPAWNED = 10;
	public static final int SHAPE_LOCKED = 11;
	
	protected int id;
	protected Field field;
	protected int lines;
	protected Shape shape;
	protected int x;
	protected int y;
	protected int ghostY;
	
	public TetrevilEvent(Object source, int id, Field field) {
		super(source);
		this.id = id;
		this.field = field;
		this.shape = field.getShape();
		this.x = field.getShapeX();
		this.y = field.getShapeY();
		this.ghostY = field.getGhostY();
	}
	
	public TetrevilEvent(Object source, int id, Field field, int lines) {
		this(source, id, field);
		this.lines = lines;
	}
	
	public int getId() {
		return id;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public Shape getShape() {
		return shape;
	}
	
	public int getGhostY() {
		return ghostY;
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
