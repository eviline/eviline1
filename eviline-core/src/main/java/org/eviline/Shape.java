package org.eviline;

import static org.eviline.Block.IA;
import static org.eviline.Block.JA;
import static org.eviline.Block.LA;
import static org.eviline.Block.OA;
import static org.eviline.Block.SA;
import static org.eviline.Block.TA;
import static org.eviline.Block.ZA;

/**
 * A tetrimino.  Stored as both an array of blocks and as arrays of (x,y) pairs for speed efficiency.
 * @author robin
 *
 */
public enum Shape {
	O_UP(new Block[][] {
					{OA,	 OA},
					{OA,	 OA}}),
	O_RIGHT(new Block[][] {{OA,	 OA},{OA,	 OA}}),
	O_DOWN(new Block[][] {{OA,	 OA},{OA,	 OA}}),
	O_LEFT(new Block[][] {{OA,	 OA},{OA,	 OA}}),

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
	
	private ShapeType type;
	
	private int[] bx = new int[4];
	private int[] by = new int[4];
	private int width;
	private int height;
	
	private Shape(Block[][] shape) {
		this.shape = shape;
		int b = 0;
		int xmin = 4, xmax = -1, ymin = 4, ymax = -1;
		for(int y = 0; y < shape.length; y++) {
			for(int x = 0; x < shape[y].length; x++) {
				if(shape[y][x] != null) {
					bx[b] = x;
					by[b] = y;
					b++;
					xmin = Math.min(xmin, x);
					xmax = Math.max(xmax, x);
					ymin = Math.min(ymin, y);
					ymax = Math.max(ymax, y);
				}
			}
		}
		type = ShapeType.valueOf(name().substring(0, 1));
		width = xmax - xmin + 1;
		height = ymax - ymin + 1;
	}
	
	public Block[][] shape() {
		return shape;
	}
	
	public int width() { return width; }
	
	public int height() { return height; }
	
	public ShapeType type() {
		return type;
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
		/*
		for(int i = 0; i < 4; i++) {
			if(y+by[i] < 0)
				return true;
			Block fb = field[y+by[i]][x+bx[i]];
			if(fb != null && fb != Block.G)
				return true;
		}
		return false;
		*/
		int y0 = y + by[0];
		int y1 = y + by[1];
		int y2 = y + by[2];
		int y3 = y + by[3];
		if((y0 | y1 | y2 | y3) < 0)
			return true;
		
		int x0 = x + bx[0];
		int x1 = x + bx[1];
		int x2 = x + bx[2];
		int x3 = x + bx[3];
		
		if(x0 < Field.BUFFER || x1 < Field.BUFFER || x2 < Field.BUFFER || x3 < Field.BUFFER)
			return true;
		if(
				x0 >= Field.BUFFER + Field.WIDTH
				|| x1 >= Field.BUFFER + Field.WIDTH
				|| x2 >= Field.BUFFER + Field.WIDTH
				|| x3 >= Field.BUFFER + Field.WIDTH)
			return true;
		
		
		Block b0 = field[y0][x0];
		Block b1 = field[y1][x1];
		Block b2 = field[y2][x2];
		Block b3 = field[y3][x3];
		if(b0 == null && b1 == null && b2 == null && b3 == null)
			return false;
		if((b0 == null || b0 == Block.G) && (b1 == null || b1 == Block.G) && (b2 == null || b2 == Block.G) && (b3 == null || b3 == Block.G))
			return false;
		else
			return true;
	}
	
	public int x(int i) {
		return bx[i];
	}
	
	public int y(int i) {
		return by[i];
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
	
	public int[] symmetryTranslation(Shape s) {
		if(type() != s.type())
			return null;
		if(type() == ShapeType.O)
			return new int[] {0, 0};
		if(direction() == ShapeDirection.UP && s.direction() != ShapeDirection.DOWN)
			return null;
		if(direction() == ShapeDirection.DOWN && s.direction() != ShapeDirection.UP)
			return null;
		if(direction() == ShapeDirection.LEFT && s.direction() != ShapeDirection.RIGHT)
			return null;
		if(direction() == ShapeDirection.RIGHT && s.direction() != ShapeDirection.LEFT)
			return null;
		if(type() != ShapeType.I && type() != ShapeType.S && type != ShapeType.Z)
			return null;
		switch(direction()) {
		case DOWN: return new int[] {0, 1};
		case LEFT: return new int[] {-1, 0};
		case RIGHT: return new int[] {1, 0};
		case UP: return new int[] {0, -1};
		}
		return null;
	}
}
