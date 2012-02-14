package org.tetrevil;

import static org.tetrevil.Shape.I_DOWN;
import static org.tetrevil.Shape.I_LEFT;
import static org.tetrevil.Shape.I_UP;
import static org.tetrevil.Shape.J_DOWN;
import static org.tetrevil.Shape.J_LEFT;
import static org.tetrevil.Shape.J_RIGHT;
import static org.tetrevil.Shape.J_UP;
import static org.tetrevil.Shape.L_DOWN;
import static org.tetrevil.Shape.L_LEFT;
import static org.tetrevil.Shape.L_RIGHT;
import static org.tetrevil.Shape.L_UP;
import static org.tetrevil.Shape.O_UP;
import static org.tetrevil.Shape.S_DOWN;
import static org.tetrevil.Shape.S_LEFT;
import static org.tetrevil.Shape.S_UP;
import static org.tetrevil.Shape.T_DOWN;
import static org.tetrevil.Shape.T_LEFT;
import static org.tetrevil.Shape.T_RIGHT;
import static org.tetrevil.Shape.T_UP;
import static org.tetrevil.Shape.Z_DOWN;
import static org.tetrevil.Shape.Z_LEFT;
import static org.tetrevil.Shape.Z_UP;

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
		case O: return new Shape[] { O_UP };
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
	
	public Block inactive() {
		switch(this) {
		case O: return Block.O;
		case S: return Block.S;
		case Z: return Block.Z;
		case I: return Block.I;
		case T: return Block.T;
		case J: return Block.J;
		case L: return Block.L;
		}
		throw new InternalError("Fell through to default when all enums covered");
	}
}
