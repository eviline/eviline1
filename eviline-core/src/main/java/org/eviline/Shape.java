package org.eviline;

import static org.eviline.BlockType.I;
import static org.eviline.BlockType.J;
import static org.eviline.BlockType.L;
import static org.eviline.BlockType.O;
import static org.eviline.BlockType.S;
import static org.eviline.BlockType.T;
import static org.eviline.BlockType.Z;

/**
 * A tetrimino.  Stored as both an array of blocks and as arrays of (x,y) pairs for speed efficiency.
 * @author robin
 *
 */
public enum Shape {
	O_UP(new BlockType[][] {
					{O,	 O},
					{O,	 O}}),
	O_RIGHT(new BlockType[][] {{O,	 O},{O,	 O}}),
	O_DOWN(new BlockType[][] {{O,	 O},{O,	 O}}),
	O_LEFT(new BlockType[][] {{O,	 O},{O,	 O}}),

	I_UP(new BlockType[][] {
					{null,	null,	null,	null},
					{I,	I,		I,		I	},
					{null,	null,	null,	null},
					{null,	null,	null,	null}}),
	I_RIGHT(new BlockType[][] {
					{null,	null, I,	 null},
					{null,	null, I,	 null},
					{null,	null, I,	 null},
					{null,	null, I,	 null}}),
	I_DOWN(new BlockType[][] {
					{null,	null,	null,	null},
					{null,	null,	null,	null},
					{I,	I,		I,		I	},
					{null,	null,	null,	null}}),
	I_LEFT(new BlockType[][] {
					{null, I,	 null,	null},
					{null, I,	 null,	null},
					{null, I,	 null,	null},
					{null, I,	 null,	null}}),
	S_LEFT(new BlockType[][] {
					{S,	null,	null},
					{S,	S,		null},
					{null,	S,		null}}),
	S_UP(new BlockType[][] {
					{null,	S,		S	},
					{S,	S,		null},
					{null,	null,	null}}),
	S_RIGHT(new BlockType[][] {
					{null,	S,		null},
					{null,	S,		S},
					{null,	null,	S}}),
	S_DOWN(new BlockType[][] {
					{null,	null,	null},
					{null,	S,		S	},
					{S,	S,		null}}),
	Z_RIGHT(new BlockType[][] {
					{null,	null,	Z},
					{null,	Z,		Z},
					{null,	Z,		null}}),
	Z_DOWN(new BlockType[][] {
					{null,	null,	null},
					{Z,	Z,		null},
					{null,	Z,		Z	}}),
	Z_LEFT(new BlockType[][] {
					{null,	Z,		null},
					{Z,	Z,		null},
					{Z,	null,	null}}),
	Z_UP(new BlockType[][] {
					{Z,	Z,		null},
					{null,	Z,		Z	},
					{null,	null,	null}}),
	T_UP(new BlockType[][] {
					{null,	T,		null},
					{T,	T,		T	},
					{null,	null,	null}}),
	T_RIGHT(new BlockType[][] {
					{null,	T,		null},
					{null,	T,		T	},
					{null,	T,		null}}),
	T_DOWN(new BlockType[][] {
					{null,	null,	null},
					{T,	T,		T	},
					{null,	T,		null}}),
	T_LEFT(new BlockType[][] {
					{null,	T,		null},
					{T,	T,		null},
					{null,	T,		null}}),
	J_LEFT(new BlockType[][] {
					{null,	J,		null},
					{null,	J,		null},
					{J,	J,		null}}),
	J_UP(new BlockType[][] {
					{J,	null,	null},
					{J,	J,		J},
					{null,	null,	null}}),
	J_RIGHT(new BlockType[][] {
					{null,	J,		J},
					{null,	J,		null},
					{null,	J,		null}}),
	J_DOWN(new BlockType[][] {
					{null,	null,	null},
					{J,	J,		J},
					{null,	null,	J}}),
	L_RIGHT(new BlockType[][] {
					{null,	L,		null},
					{null,	L,		null},
					{null,	L,		L}}),
	L_DOWN(new BlockType[][] {
					{null,	null,	null},
					{L,	L,		L},
					{L,	null,	null}}),
	L_LEFT(new BlockType[][] {
					{L,	L,		null},
					{null,	L,		null},
					{null,	L,		null}}),
	L_UP(new BlockType[][] {
					{null,	null,	L},
					{L,	L,		L},
					{null,	null,	null}}),
	;

	private BlockType[][] shape;
	
	private ShapeType type;
	
	private int[] bx = new int[4];
	private int[] by = new int[4];
	private int width;
	private int height;
	
	private Shape(BlockType[][] shape) {
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
	
	public BlockType[][] shape() {
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
	
	public boolean intersects(Field f, int x, int y) {
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
		
		Block[][] field = f.getField();
		
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
				x0 >= Field.BUFFER + f.getWidth()
				|| x1 >= Field.BUFFER + f.getWidth()
				|| x2 >= Field.BUFFER + f.getWidth()
				|| x3 >= Field.BUFFER + f.getWidth())
			return true;
		
		
		Block b0 = field[y0][x0];
		Block b1 = field[y1][x1];
		Block b2 = field[y2][x2];
		Block b3 = field[y3][x3];
		return !b0.isSolid() && !b1.isSolid() && !b2.isSolid() && !b3.isSolid();
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
