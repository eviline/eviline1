package org.tetrevil;

import static org.tetrevil.Block.*;

public enum Shape {
	O_UP(new Block[][] {
					{OA,	 OA},
					{OA,	 OA}}),
	O_RIGHT(new Block[][] {
					{OA,	 OA},
					{OA,	 OA}}),
	O_DOWN(new Block[][] {
					{OA,	 OA},
					{OA,	 OA}}),
	O_LEFT(new Block[][] {
					{OA,	 OA},
					{OA,	 OA}}),
	I_UP(new Block[][] {
					{null,	null,	null,	null},
					{IA,	IA,		IA,		IA	},
					{null,	null,	null,	null},
					{null,	null,	null,	null}}),
	I_RIGHT(new Block[][] {
					{null,	null, IA,	 null},
					{null,	null, IA,	 null},
					{null,	null, IA,	 null},
					{null,	null, IA,	 null}}),
	I_DOWN(new Block[][] {
					{null,	null,	null,	null},
					{null,	null,	null,	null},
					{IA,	IA,		IA,		IA	},
					{null,	null,	null,	null}}),
	I_LEFT(new Block[][] {
					{null, IA,	 null,	null},
					{null, IA,	 null,	null},
					{null, IA,	 null,	null},
					{null, IA,	 null,	null}}),
	S_UP(new Block[][] {
					{SA,	null,	null},
					{SA,	SA,		null},
					{null,	SA,		null}}),
	S_RIGHT(new Block[][] {
					{null,	SA,		SA	},
					{SA,	SA,		null},
					{null,	null,	null}}),
	S_DOWN(new Block[][] {
					{null,	SA,		null},
					{null,	SA,		SA},
					{null,	null,	SA}}),
	S_LEFT(new Block[][] {
					{null,	null,	null},
					{null,	SA,		SA	},
					{SA,	SA,		null}}),
	Z_UP(new Block[][] {
					{null,	null,	ZA},
					{null,	ZA,		ZA},
					{null,	ZA,		null}}),
	Z_RIGHT(new Block[][] {
					{null,	null,	null},
					{ZA,	ZA,		null},
					{null,	ZA,		ZA	}}),
	Z_DOWN(new Block[][] {
					{null,	ZA,		null},
					{ZA,	ZA,		null},
					{ZA,	null,	null}}),
	Z_LEFT(new Block[][] {
					{ZA,	ZA,		null},
					{null,	ZA,		ZA	},
					{null,	null,	null}}),
	T_UP(new Block[][] {
					{null,	TA,		null},
					{TA,	TA,		TA	},
					{null,	null,	null}}),
	T_RIGHT(new Block[][] {
					{null,	TA,		null},
					{null,	TA,		TA	},
					{null,	TA,		null}}),
	T_DOWN(new Block[][] {
					{null,	null,	null},
					{TA,	TA,		TA	},
					{null,	TA,		null}}),
	T_LEFT(new Block[][] {
					{null,	TA,		null},
					{TA,	TA,		null},
					{null,	TA,		null}}),
	J_UP(new Block[][] {
					{null,	JA,		null},
					{null,	JA,		null},
					{JA,	JA,		null}}),
	J_RIGHT(new Block[][] {
					{JA,	null,	null},
					{JA,	JA,		JA},
					{null,	null,	null}}),
	J_DOWN(new Block[][] {
					{null,	JA,		JA},
					{null,	JA,		null},
					{null,	JA,		null}}),
	J_LEFT(new Block[][] {
					{null,	null,	null},
					{JA,	JA,		JA},
					{null,	null,	JA}}),
	L_UP(new Block[][] {
					{null,	LA,		null},
					{null,	LA,		null},
					{null,	LA,		LA}}),
	L_RIGHT(new Block[][] {
					{null,	null,	null},
					{LA,	LA,		LA},
					{LA,	null,	null}}),
	L_DOWN(new Block[][] {
					{LA,	LA,		null},
					{null,	LA,		null},
					{null,	LA,		null}}),
	L_LEFT(new Block[][] {
					{null,	null,	LA},
					{LA,	LA,		LA},
					{null,	null,	null}}),
	;

	private Block[][] shape;
	
	private Shape(Block[][] shape) {
		this.shape = shape;
	}
	
	public Block[][] shape() {
		return shape;
	}
	
	public Shape rotateRight() {
		switch(this) {
		case O_UP: return    O_RIGHT;
		case O_RIGHT: return O_DOWN;
		case O_DOWN: return  O_LEFT;
		case O_LEFT: return  O_UP;
		case T_UP: return    T_RIGHT;
		case T_RIGHT: return T_DOWN;
		case T_DOWN: return  T_LEFT;
		case T_LEFT: return  T_UP;
		case I_UP: return    I_RIGHT;
		case I_RIGHT: return I_DOWN;
		case I_DOWN: return  I_LEFT;
		case I_LEFT: return  I_UP;
		case S_UP: return    S_RIGHT;
		case S_RIGHT: return S_DOWN;
		case S_DOWN: return  S_LEFT;
		case S_LEFT: return  S_UP;
		case Z_UP: return    Z_RIGHT;
		case Z_RIGHT: return Z_DOWN;
		case Z_DOWN: return  Z_LEFT;
		case Z_LEFT: return  Z_UP;
		case J_UP: return    J_RIGHT;
		case J_RIGHT: return J_DOWN;
		case J_DOWN: return  J_LEFT;
		case J_LEFT: return  J_UP;
		case L_UP: return    L_RIGHT;
		case L_RIGHT: return L_DOWN;
		case L_DOWN: return  L_LEFT;
		case L_LEFT: return  L_UP;

		}
		throw new InternalError("Fell through to default when all enum cases were covered");
	}
	
	public Shape rotateLeft() {
		switch(this) {
		case O_UP: return    O_LEFT;
		case O_LEFT: return  O_DOWN;
		case O_DOWN: return  O_RIGHT;
		case O_RIGHT: return O_UP;
		case T_UP: return    T_LEFT;
		case T_LEFT: return  T_DOWN;
		case T_DOWN: return  T_RIGHT;
		case T_RIGHT: return T_UP;
		case I_UP: return    I_LEFT;
		case I_LEFT: return  I_DOWN;
		case I_DOWN: return  I_RIGHT;
		case I_RIGHT: return I_UP;
		case S_UP: return    S_LEFT;
		case S_LEFT: return  S_DOWN;
		case S_DOWN: return  S_RIGHT;
		case S_RIGHT: return S_UP;
		case Z_UP: return    Z_LEFT;
		case Z_LEFT: return  Z_DOWN;
		case Z_DOWN: return  Z_RIGHT;
		case Z_RIGHT: return Z_UP;
		case J_UP: return    J_LEFT;
		case J_LEFT: return  J_DOWN;
		case J_DOWN: return  J_RIGHT;
		case J_RIGHT: return J_UP;
		case L_UP: return    L_LEFT;
		case L_LEFT: return  L_DOWN;
		case L_DOWN: return  L_RIGHT;
		case L_RIGHT: return L_UP;
		}
		throw new InternalError("Fell through to default when all enum cases were covered");
	}
	
	public boolean intersects(Block[][] field, int x, int y) {
		Block[][] shape = this.shape();
		for(int iy = 0; iy < shape.length; iy++) {
			for(int ix = 0; ix < shape[iy].length; ix++) {
				if(shape[iy][ix] != null && field[y + iy][x + ix] != null)
					return true;
			}
		}
		return false;
	}
}
