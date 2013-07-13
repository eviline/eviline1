package org.eviline;

import java.awt.Color;

/**
 * A block on the playing {@link Field}.  There are two block types for each {@link ShapeType}: active and inactive.
 * Additionally, there is a block type for the outer border around the field.  Empty areas in the {@link Field}
 * are stored as nulls.<p>
 * 
 * A block is active if it is a part of the currently active {@link Shape} in the field.  Once a {@link Shape} locks
 * on the field its active blocks become inactive blocks and are stored.
 * @author robin
 *
 */
public enum BlockType {
	/*
	 * Inactive block types
	 */
	I,
	T,
	S,
	Z,
	O,
	J,
	L,
	
	EMPTY,
	
	/**
	 * Field border
	 */
	BORDER,
	
	/**
	 * Ghost
	 */
	GHOST,
	
	
	/**
	 * iMpossible block
	 */
	IMPOSSIBLE,
	/**
	 * Unlikely block
	 */
	UNLIKELY,
	
	GARBAGE,
	
	;
	
	public Color color() {
		switch(this) {
		case I:
			return new Color(0, 159, 218);
		case J: 
			return new Color(0, 101, 189);
		case L: 
			return new Color(255, 121, 0);
		case O: 
			return new Color(254, 203, 0);
		case S: 
			return new Color(105, 190, 40);
		case T: 
			return new Color(149, 45, 152);
		case Z: 
			return new Color(237, 41, 57);
		case EMPTY: return new Color(0, 0, 0, 0);
		case BORDER: return Color.DARK_GRAY;
		case GHOST: return Color.WHITE;
		case IMPOSSIBLE: return BORDER.color();
		case UNLIKELY: return BORDER.color();
		case GARBAGE: return Color.GRAY;
		}
		throw new InternalError("Impossible switch fall-through");
	}
	
	public boolean isSolid() {
		switch(this) {
		case I: case T: case S: case Z: case O: case J: case L:
		case BORDER: case GARBAGE:
			return true;
		default:
			return false;
		}
	}
}
