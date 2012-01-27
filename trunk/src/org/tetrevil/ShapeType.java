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
		case O: return new Shape[] { O_DOWN, O_LEFT, O_RIGHT, O_UP };
		case S: return new Shape[] { S_DOWN, S_LEFT, S_RIGHT, S_UP };
		case Z: return new Shape[] { Z_DOWN, Z_LEFT, Z_RIGHT, Z_UP };
		case J: return new Shape[] { J_DOWN, J_LEFT, J_RIGHT, J_UP };
		case L: return new Shape[] { L_DOWN, L_LEFT, L_RIGHT, L_UP };
		case I: return new Shape[] { I_DOWN, I_LEFT, I_RIGHT, I_UP };
		case T: return new Shape[] { T_DOWN, T_LEFT, T_RIGHT, T_UP };
		}
		throw new InternalError("Fell through to default when all enums covered");
	}
}
