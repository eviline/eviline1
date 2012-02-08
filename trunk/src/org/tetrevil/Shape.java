package org.tetrevil;

import static org.tetrevil.Block.*;

public enum Shape {
	O_UP(new Block[][] {
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
	S_LEFT(new Block[][] {
					{SA,	null,	null},
					{SA,	SA,		null},
					{null,	SA,		null}}),
	S_UP(new Block[][] {
					{null,	SA,		SA	},
					{SA,	SA,		null},
					{null,	null,	null}}),
	S_RIGHT(new Block[][] {
					{null,	SA,		null},
					{null,	SA,		SA},
					{null,	null,	SA}}),
	S_DOWN(new Block[][] {
					{null,	null,	null},
					{null,	SA,		SA	},
					{SA,	SA,		null}}),
	Z_RIGHT(new Block[][] {
					{null,	null,	ZA},
					{null,	ZA,		ZA},
					{null,	ZA,		null}}),
	Z_DOWN(new Block[][] {
					{null,	null,	null},
					{ZA,	ZA,		null},
					{null,	ZA,		ZA	}}),
	Z_LEFT(new Block[][] {
					{null,	ZA,		null},
					{ZA,	ZA,		null},
					{ZA,	null,	null}}),
	Z_UP(new Block[][] {
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
	J_LEFT(new Block[][] {
					{null,	JA,		null},
					{null,	JA,		null},
					{JA,	JA,		null}}),
	J_UP(new Block[][] {
					{JA,	null,	null},
					{JA,	JA,		JA},
					{null,	null,	null}}),
	J_RIGHT(new Block[][] {
					{null,	JA,		JA},
					{null,	JA,		null},
					{null,	JA,		null}}),
	J_DOWN(new Block[][] {
					{null,	null,	null},
					{JA,	JA,		JA},
					{null,	null,	JA}}),
	L_RIGHT(new Block[][] {
					{null,	LA,		null},
					{null,	LA,		null},
					{null,	LA,		LA}}),
	L_DOWN(new Block[][] {
					{null,	null,	null},
					{LA,	LA,		LA},
					{LA,	null,	null}}),
	L_LEFT(new Block[][] {
					{LA,	LA,		null},
					{null,	LA,		null},
					{null,	LA,		null}}),
	L_UP(new Block[][] {
					{null,	null,	LA},
					{LA,	LA,		LA},
					{null,	null,	null}}),
	;

	private Block[][] shape;
	
	private int[] bx = new int[4];
	private int[] by = new int[4];
	
	private Shape(Block[][] shape) {
		this.shape = shape;
		int b = 0;
		for(int y = 0; y < shape.length; y++) {
			for(int x = 0; x < shape[y].length; x++) {
				if(shape[y][x] != null) {
					bx[b] = x;
					by[b] = y;
					b++;
				}
			}
		}
	}
	
	public Block[][] shape() {
		return shape;
	}
	
	public ShapeType type() {
		switch(this) {
		case O_UP: return ShapeType.O;
		case I_DOWN: case I_LEFT: case I_RIGHT: case I_UP: return ShapeType.I;
		case J_DOWN: case J_LEFT: case J_RIGHT: case J_UP: return ShapeType.J;
		case L_DOWN: case L_LEFT: case L_RIGHT: case L_UP: return ShapeType.L;
		case S_DOWN: case S_LEFT: case S_RIGHT: case S_UP: return ShapeType.S;
		case T_DOWN: case T_LEFT: case T_RIGHT: case T_UP: return ShapeType.T;
		case Z_DOWN: case Z_LEFT: case Z_RIGHT: case Z_UP: return ShapeType.Z;
		}
		throw new InternalError("Fell through to default when all enum cases were covered");
	}
	
	public Shape rotateRight() {
		switch(this) {
		case O_UP: return    O_UP;
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
		case O_UP: return    O_UP;
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
//		for(int iy = 0; iy < shape.length; iy++) {
//			for(int ix = 0; ix < shape[iy].length; ix++) {
//				Block sb = shape[iy][ix];
//				Block fb;
//				if(sb != null && y + iy >= 0 && (fb = field[y + iy][x + ix]) != null && fb != Block.G)
//					return true;
//				if(sb != null && y + iy < 0)
//					return true;
//			}
//		}
		for(int i = 0; i < 4; i++) {
			if(y+by[i] < 0)
				return true;
			Block fb = field[y+by[i]][x+bx[i]];
			if(fb != null && fb != Block.G)
				return true;
		}
		return false;
	}
	
	public ShapeDirection direction() {
		String ts = toString();
		if(ts.endsWith("UP"))
			return ShapeDirection.UP;
		if(ts.endsWith("LEFT"))
			return ShapeDirection.LEFT;
		if(ts.endsWith("RIGHT"))
			return ShapeDirection.RIGHT;
		if(ts.endsWith("DOWN"))
			return ShapeDirection.DOWN;
		throw new InternalError("Somehow enum doesn't end in direction specifier:" + this);
	}
}
