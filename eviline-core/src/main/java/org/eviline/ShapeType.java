package org.eviline;

import static org.eviline.Shape.I_DOWN;
import static org.eviline.Shape.I_LEFT;
import static org.eviline.Shape.I_RIGHT;
import static org.eviline.Shape.I_UP;
import static org.eviline.Shape.J_DOWN;
import static org.eviline.Shape.J_LEFT;
import static org.eviline.Shape.J_RIGHT;
import static org.eviline.Shape.J_UP;
import static org.eviline.Shape.L_DOWN;
import static org.eviline.Shape.L_LEFT;
import static org.eviline.Shape.L_RIGHT;
import static org.eviline.Shape.L_UP;
import static org.eviline.Shape.O_UP;
import static org.eviline.Shape.S_DOWN;
import static org.eviline.Shape.S_LEFT;
import static org.eviline.Shape.S_RIGHT;
import static org.eviline.Shape.S_UP;
import static org.eviline.Shape.T_DOWN;
import static org.eviline.Shape.T_LEFT;
import static org.eviline.Shape.T_RIGHT;
import static org.eviline.Shape.T_UP;
import static org.eviline.Shape.Z_DOWN;
import static org.eviline.Shape.Z_LEFT;
import static org.eviline.Shape.Z_RIGHT;
import static org.eviline.Shape.Z_UP;

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
	
	private BlockType block;
	
	private ShapeType() {
		BlockType b = BlockType.valueOf(name());
		block = b;
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

	public Shape[] searchOrientations() {
		switch(this) {
		case O: return new Shape[] { O_UP };
		case S: return new Shape[] { S_UP, S_RIGHT};
		case Z: return new Shape[] { Z_UP, Z_RIGHT};
		case J: return new Shape[] { J_UP, J_RIGHT, J_LEFT, J_DOWN};
		case L: return new Shape[] { L_UP, L_RIGHT, L_LEFT, L_DOWN};
		case I: return new Shape[] { I_UP, I_RIGHT};
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
	public int starterY(Field field) {
		switch(this) {
		case O: return Field.BUFFER - 2;
		case S: return Field.BUFFER - 2;
		case Z: return Field.BUFFER - 2;
		case I: return Field.BUFFER - 2;
		case T: return Field.BUFFER - 2;
		case J: return Field.BUFFER - 2;
		case L: return Field.BUFFER - 2;
		}
		throw new InternalError("Fell through to default when all enums covered");
	}

	/**
	 * Returns the X offset of a shape of this type when starting
	 * @return
	 */
	public int starterX(Field field) {
		switch(this) {
		case O: return (field.getWidth() + 2 * Field.BUFFER - starter().width()) / 2;
		case S: return (field.getWidth() + 2 * Field.BUFFER - starter().width()) / 2;
		case Z: return (field.getWidth() + 2 * Field.BUFFER - starter().width()) / 2;
		case I: return (field.getWidth() + 2 * Field.BUFFER - starter().width()) / 2;
		case T: return (field.getWidth() + 2 * Field.BUFFER - starter().width()) / 2;
		case J: return (field.getWidth() + 2 * Field.BUFFER - starter().width()) / 2;
		case L: return (field.getWidth() + 2 * Field.BUFFER - starter().width()) / 2;
		}
		throw new InternalError("Fell through to default when all enums covered");
	}
	
	/**
	 * Returns the inactive block associated with this shape type
	 * @return
	 */
	public BlockType block() {
		return block;
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
