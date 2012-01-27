package org.tetrevil;

import static org.tetrevil.Block.*;

public enum Shape {
	O_UP,
	O_RIGHT,
	O_DOWN,
	O_LEFT,
	I_UP,
	I_RIGHT,
	I_DOWN,
	I_LEFT,
	S_UP,
	S_RIGHT,
	S_DOWN,
	S_LEFT,
	Z_UP,
	Z_RIGHT,
	Z_DOWN,
	Z_LEFT,
	T_UP,
	T_RIGHT,
	T_DOWN,
	T_LEFT,
	;

	public Block[][] shape() {
		switch(this) {
		case O_DOWN:
		case O_LEFT:
		case O_UP:
		case O_RIGHT:
			return new Block[][] {
					{OA,	 OA},
					{OA,	 OA}};
		case I_LEFT:
			return new Block[][] {
					{null, IA,	 null,	null},
					{null, IA,	 null,	null},
					{null, IA,	 null,	null},
					{null, IA,	 null,	null}};
		case I_UP:
			return new Block[][] {
					{null,	null,	null,	null},
					{IA,	IA,		IA,		IA	},
					{null,	null,	null,	null},
					{null,	null,	null,	null}};
		case I_RIGHT:
			return new Block[][] {
					{null,	null, IA,	 null},
					{null,	null, IA,	 null},
					{null,	null, IA,	 null},
					{null,	null, IA,	 null}};
		case I_DOWN:
			return new Block[][] {
					{null,	null,	null,	null},
					{null,	null,	null,	null},
					{IA,	IA,		IA,		IA	},
					{null,	null,	null,	null}};
		case T_UP:
			return new Block[][] {
					{null,	TA,		null},
					{TA,	TA,		TA	},
					{null,	null,	null}};
		case T_DOWN:
			return new Block[][] {
					{null,	null,	null},
					{TA,	TA,		TA	},
					{null,	TA,		null}};
		case T_LEFT:
			return new Block[][] {
					{null,	TA,		null},
					{TA,	TA,		null},
					{null,	TA,		null}};
		case T_RIGHT:
			return new Block[][] {
					{null,	TA,		null},
					{null,	TA,		TA	},
					{null,	TA,		null}};
		case S_RIGHT:
			return new Block[][] {
					{null,	SA,		SA	},
					{SA,	SA,		null},
					{null,	null,	null}};
		case S_DOWN:
			return new Block[][] {
					{null,	SA,		null},
					{null,	SA,		SA},
					{null,	null,	SA}};
		case S_LEFT:
			return new Block[][] {
					{null,	null,	null},
					{null,	SA,		SA	},
					{SA,	SA,		null}};
		case S_UP:
			return new Block[][] {
					{SA,	null,	null},
					{SA,	SA,		null},
					{null,	SA,		null}};
		case Z_RIGHT:
			return new Block[][] {
					{null,	null,	null},
					{SA,	SA,		null},
					{null,	SA,		SA	}};
		case Z_LEFT:
			return new Block[][] {
					{SA,	SA,		null},
					{null,	SA,		SA	},
					{null,	null,	null}};
		case Z_UP:
			return new Block[][] {
					{null,	null,	SA},
					{null,	SA,		SA},
					{null,	SA,		null}};
		case Z_DOWN:
			return new Block[][] {
					{null,	SA,		null},
					{SA,	SA,		null},
					{SA,	null,	null}};
		default:
			throw new InternalError("Fell through to default when all enum cases were covered");
		}
	}
	
	public Shape right() {
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
		default:
			throw new InternalError("Fell through to default when all enum cases were covered");
		}
	}
	
	public Shape left() {
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
		default:
			throw new InternalError("Fell through to default when all enum cases were covered");
		}
	}
}
