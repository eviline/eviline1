package org.tetrevil;

import static org.tetrevil.Shape.*;

public enum ShapeType {
	O,
	S,
	Z,
	J,
	L,
	I,
	T,
	;
	
	public Shape[] shapes() {
		switch(this) {
		case O: return new Shape[] { O_DOWN };
		case S: return new Shape[] { S_DOWN, S_LEFT};
		case Z: return new Shape[] { Z_DOWN, Z_LEFT};
		case J: return new Shape[] { J_DOWN, J_LEFT, J_RIGHT, J_UP };
		case L: return new Shape[] { L_DOWN, L_LEFT, L_RIGHT, L_UP };
		case I: return new Shape[] { I_DOWN, I_LEFT};
		case T: return new Shape[] { T_DOWN, T_LEFT, T_RIGHT, T_UP };
		}
		throw new InternalError("Fell through to default when all enums covered");
	}
	
	public Shape starter() {
		switch(this) {
		case O: return O_DOWN;
		case S: return S_RIGHT;
		case Z: return Z_LEFT;
		case T: return T_UP;
		case I: return I_UP;
		case J: return J_RIGHT;
		case L: return L_LEFT;
		}
		throw new InternalError("Fell through to default when all enums covered");
	}
	
	public int starterY() {
		switch(this) {
		case O: return 0;
		case S: return 0;
		case Z: return 0;
		case I: return -1;
		case T: return 0;
		case J: return 0;
		case L: return 0;
		}
		throw new InternalError("Fell through to default when all enums covered");
	}

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
}
