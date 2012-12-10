package org.eviline;

import static org.eviline.Shape.*;

import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Possible types of {@link Shape}s.
 * @author robin
 *
 */
public enum ShapeType {
	I,
	L,
	O,
	Z,
	T,
	J,
	S,
	;
	
	private Block inactive;
	
	private ShapeType() {
		Block b = Block.valueOf(name());
		inactive = b;
	}
	
	
	/**
	 * Returns the {@link Shape}s for this {@link ShapeType} that are distinct.
	 * @return
	 */
	public Shape[] orientations() {
		switch(this) {
		case O: return new Shape[] { O_UP };
		case S: return new Shape[] { S_UP, S_RIGHT, S_LEFT, S_DOWN};
		case Z: return new Shape[] { Z_UP, Z_RIGHT, Z_LEFT, Z_DOWN};
		case J: return new Shape[] { J_UP, J_RIGHT, J_LEFT, J_DOWN};
		case L: return new Shape[] { L_UP, L_RIGHT, L_LEFT, L_DOWN};
		case I: return new Shape[] { I_UP, I_RIGHT, I_LEFT, I_DOWN};
		case T: return new Shape[] { T_UP, T_RIGHT, T_LEFT, T_DOWN};
		}
		throw new InternalError("Fell through to default when all enums covered");
	}
	
	/**
	 * Returns the shape used to start a new round with this shape type
	 * @return
	 */
	public Shape starter() {
		switch(this) {
		case O: return O_UP;
		case S: return S_UP;
		case Z: return Z_UP;
		case T: return T_UP;
		case I: return I_UP;
		case J: return J_UP;
		case L: return L_UP;
		}
		throw new InternalError("Fell through to default when all enums covered");
	}
	
	/**
	 * Returns the Y offset of a shape of this type when starting
	 * @return
	 */
	public int starterY() {
		switch(this) {
		case O: return 1;
		case S: return 1;
		case Z: return 1;
		case I: return 1;
		case T: return 1;
		case J: return 1;
		case L: return 1;
		}
		throw new InternalError("Fell through to default when all enums covered");
	}

	/**
	 * Returns the X offset of a shape of this type when starting
	 * @return
	 */
	public int starterX() {
		switch(this) {
		case O: return 1;
		case S: return 0;
		case Z: return 0;
		case I: return 0;
		case T: return 0;
		case J: return 0;
		case L: return 0;
		}
		throw new InternalError("Fell through to default when all enums covered");
	}
	
	/**
	 * Returns the inactive block associated with this shape type
	 * @return
	 */
	public Block inactive() {
		return inactive;
	}
	
	public Shape up() {
		switch(this) {
		case O: return O_UP;
		case S: return S_UP;
		case Z: return Z_UP;
		case I: return I_UP;
		case T: return T_UP;
		case J: return J_UP;
		case L: return L_UP;
		}
		throw new InternalError("Fell through to default when all enums covered");
	}
	
	public Shape right() {
		return up().rotateRight();
	}
	
	public Shape left() {
		return up().rotateLeft();
	}
	
	public Shape down() {
		return up().rotateRight().rotateRight();
	}
}
