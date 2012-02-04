package org.tetrevil;

import java.util.Arrays;
import org.tetrevil.event.TetrevilEvent;
import org.tetrevil.event.TetrevilListener;

public class Field {
	public static final int HEIGHT = 20;
	public static final int WIDTH = 10;
	public static final int BUFFER = 4;
	
	protected Block[][] field = new Block[HEIGHT + 2 * BUFFER][WIDTH + 2 * BUFFER];
	protected ShapeProvider provider = new RandomShapeProvider();
	protected Shape shape;
	protected int shapeX;
	protected int shapeY;
	protected int ghostY;
	protected boolean gameOver;
	protected boolean paused;
	protected int lines;
	
	protected ShapeDirection autoShift = null;
	
	protected TetrevilListener[] listeners = new TetrevilListener[0];
	
	public Field() {
		this(false);
	}
	
	public Field(boolean evil) {
		for(int y = 0; y < BUFFER; y++) {
			Arrays.fill(field[y], 0, BUFFER, Block.X);
			Arrays.fill(field[y], BUFFER, field[y].length - BUFFER, Block.G);
			Arrays.fill(field[y], field[y].length - BUFFER, field[y].length, Block.X);
		}
		for(int y = BUFFER; y < field.length - BUFFER; y++) {
			Arrays.fill(field[y], 0, BUFFER, Block.X);
			Arrays.fill(field[y], field[y].length - BUFFER, field[y].length, Block.X);
		}
		for(int y = field.length - BUFFER; y < field.length; y++) {
			Arrays.fill(field[y], Block.X);
		}
		if(evil)
			provider = new MaliciousShapeProvider();
		else
			provider = new RandomShapeProvider();
	}
	
	public Field copyInto(Field target) {
		for(int y = 0; y < field.length; y++) {
			System.arraycopy(field[y], 0, target.field[y], 0, field[y].length);
		}
		target.provider = provider;
		target.shape = shape;
		target.shapeX = shapeX;
		target.shapeY = shapeY;
		target.gameOver = gameOver;
		target.lines = lines;
		return target;
	}
	
	public void reset() {
		for(int y = 0; y < BUFFER; y++) {
			Arrays.fill(field[y], 0, BUFFER, Block.X);
			Arrays.fill(field[y], BUFFER, field[y].length - BUFFER, Block.G);
			Arrays.fill(field[y], field[y].length - BUFFER, field[y].length, Block.X);
		}
		for(int y = BUFFER; y < field.length - BUFFER; y++) {
			Arrays.fill(field[y], 0, BUFFER, Block.X);
			Arrays.fill(field[y], BUFFER, field[y].length - BUFFER, null);
			Arrays.fill(field[y], field[y].length - BUFFER, field[y].length, Block.X);
		}
		for(int y = field.length - BUFFER; y < field.length; y++) {
			Arrays.fill(field[y], Block.X);
		}
		shape = null;
		gameOver = false;
		lines = 0;
		autoShift = null;
		fireGameReset();
	}
	
	public void clockTick() {
		if(paused)
			return;
		if(gameOver)
			return;
		if(shape == null) {
			shape = provider.provideShape(this).type().starter();
			shapeY = shape.type().starterY();
			shapeX = WIDTH / 2 + 2 + shape.type().starterX();
			autoShift = null;
			reghost();
		} else if(shape.intersects(field, shapeX, shapeY+1)) {
			Block[][] s = shape.shape();
			gameOver = true;
			for(int y = 0; y < s.length; y++) {
				for(int x = 0; x < s[y].length; x++) {
					if(s[y][x] != null) {
						field[y + shapeY][x + shapeX] = s[y][x].inactive();
						if(y + shapeY >= BUFFER)
							gameOver = false;
					}
				}
			}
			shape = null;
		} else {
			shapeY++;
			autoshift();
		}
		fireClockTicked();
		if(shape != null && shape.intersects(field, shapeX, shapeY)) {
			gameOver = true;
		}
		if(gameOver == true)
			fireGameOver();
		if(shape == null) {
			for(int y = field.length - 1 - BUFFER; y >= BUFFER; y--) {
				boolean tetris = true;
				for(int x = BUFFER; x < field[y].length - BUFFER; x++) {
					if(field[y][x] == null)
						tetris = false;
				}
				if(tetris) {
					lines++;
					for(int z = y - 1; z >= BUFFER; z--) {
						System.arraycopy(field[z], 0, field[z+1], 0, field[z].length);
					}
					Arrays.fill(field[BUFFER], BUFFER, field[BUFFER].length - BUFFER, null);
					y = field.length - BUFFER;
				}
			}
		}
	}
	
	public void shiftLeft() {
		if(paused)
			return;
		if(shape == null || shape.intersects(field, shapeX-1, shapeY))
			return;
		shapeX--;
		reghost();
		fireShiftedLeft();
	}
	
	public void shiftRight() {
		if(paused)
			return;
		if(shape == null || shape.intersects(field, shapeX+1, shapeY))
			return;
		shapeX++;
		reghost();
		fireShiftedRight();
	}
	
	public void autoshift() {
		if(paused || autoShift == null)
			return;
		switch(autoShift) {
		case LEFT:
			for(int i = 0; i < Field.WIDTH; i++)
				shiftLeft();
			reghost();
			break;
		case RIGHT:
			for(int i = 0; i < Field.WIDTH; i++)
				shiftRight();
			reghost();
			break;
		}
	}
	
	protected void reghost() {
		ghostY = shapeY;
		while(!shape.intersects(field, shapeX, ghostY + 1))
			ghostY++;
	}
	
	public void rotateLeft() {
		if(paused || shape == null)
			return;
//		if(shape == null || shape.rotateLeft().intersects(field, shapeX, shapeY))
//			return;
//		shape = shape.rotateLeft();
		Shape rotated = shape.rotateLeft();
		int[][] table = KickTable.forShape(shape.type(), shape.direction(), rotated.direction()).table();
		
		for(int[] kick : table) {
			int x = shapeX + kick[0];
			int y = shapeY + kick[1];
			if(!rotated.intersects(field, x, y)) {
				shapeX = x;
				shapeY = y;
				shape = rotated;
				reghost();
				fireRotatedLeft();
				return;
			}
		}
	}
	
	public void rotateRight() {
		if(paused || shape == null)
			return;
//		if(shape == null || shape.rotateRight().intersects(field, shapeX, shapeY))
//			return;
//		shape = shape.rotateRight();
		Shape rotated = shape.rotateRight();
		int[][] table = KickTable.forShape(shape.type(), shape.direction(), rotated.direction()).table();
		
		for(int[] kick : table) {
			int x = shapeX + kick[0];
			int y = shapeY + kick[1];
			if(!rotated.intersects(field, x, y)) {
				shapeX = x;
				shapeY = y;
				shape = rotated;
				reghost();
				fireRotatedRight();
				return;
			}
		}
	}
	
	public Block getBlock(int x, int y) {
		if(shape != null && x >= shapeX && y >= shapeY) {
			Block[][] s = shape.shape();
			if(x - shapeX < s[0].length && y - shapeY < s.length) {
				Block b;
				if((b = s[y - shapeY][x - shapeX]) != null)
					return b;
			}
			if(x - shapeX < s[0].length && y >= ghostY && y - ghostY < s.length) {
				if(s[y - ghostY][x - shapeX] != null)
					return Block.G;
			}
		}
		return field[y][x];
	}
	
	public void addTetrevilListener(TetrevilListener l) {
		TetrevilListener[] ll = Arrays.copyOf(listeners, listeners.length + 1);
		ll[ll.length - 1] = l;
		listeners = ll;
	}
	
	public void removeTetrevilListener(TetrevilListener l) {
		TetrevilListener[] listeners = this.listeners;
		for(int i = listeners.length - 1; i >= 0; i--) {
			if(l == listeners[i]) {
				TetrevilListener[] ll = Arrays.copyOf(listeners, listeners.length - 1);
				System.arraycopy(listeners, i+1, ll, i, listeners.length - i - 1);
				this.listeners = ll;
				break;
			}
		}
	}
	
	protected void fireClockTicked() {
		TetrevilEvent e = null;
		TetrevilListener[] ll = listeners;
		for(int i = ll.length - 1; i >= 0; i--) {
			if(e == null)
				e = new TetrevilEvent(this, this);
			ll[i].clockTicked(e);
		}
	}
	
	protected void fireGameOver() {
		TetrevilEvent e = null;
		TetrevilListener[] ll = listeners;
		for(int i = ll.length - 1; i >= 0; i--) {
			if(e == null)
				e = new TetrevilEvent(this, this);
			ll[i].gameOver(e);
		}
	}

	protected void fireGamePaused() {
		TetrevilEvent e = null;
		TetrevilListener[] ll = listeners;
		for(int i = ll.length - 1; i >= 0; i--) {
			if(e == null)
				e = new TetrevilEvent(this, this);
			ll[i].gamePaused(e);
		}
	}

	protected void fireGameReset() {
		TetrevilEvent e = null;
		TetrevilListener[] ll = listeners;
		for(int i = ll.length - 1; i >= 0; i--) {
			if(e == null)
				e = new TetrevilEvent(this, this);
			ll[i].gameReset(e);
		}
	}

	protected void fireShiftedLeft() {
		TetrevilEvent e = null;
		TetrevilListener[] ll = listeners;
		for(int i = ll.length - 1; i >= 0; i--) {
			if(e == null)
				e = new TetrevilEvent(this, this);
			ll[i].shiftedLeft(e);
		}
	}

	protected void fireShiftedRight() {
		TetrevilEvent e = null;
		TetrevilListener[] ll = listeners;
		for(int i = ll.length - 1; i >= 0; i--) {
			if(e == null)
				e = new TetrevilEvent(this, this);
			ll[i].shiftedRight(e);
		}
	}

	protected void fireRotatedLeft() {
		TetrevilEvent e = null;
		TetrevilListener[] ll = listeners;
		for(int i = ll.length - 1; i >= 0; i--) {
			if(e == null)
				e = new TetrevilEvent(this, this);
			ll[i].rotatedLeft(e);
		}
	}

	protected void fireRotatedRight() {
		TetrevilEvent e = null;
		TetrevilListener[] ll = listeners;
		for(int i = ll.length - 1; i >= 0; i--) {
			if(e == null)
				e = new TetrevilEvent(this, this);
			ll[i].rotatedRight(e);
		}
	}

	public ShapeProvider getProvider() {
		return provider;
	}

	public void setProvider(ShapeProvider provider) {
		this.provider = provider;
	}

	public Shape getShape() {
		return shape;
	}

	public int getShapeX() {
		return shapeX;
	}

	public int getShapeY() {
		return shapeY;
	}

	public boolean isGameOver() {
		return gameOver;
	}

	public void setShape(Shape shape) {
		this.shape = shape;
	}

	public void setShapeX(int shapeX) {
		this.shapeX = shapeX;
	}

	public void setShapeY(int shapeY) {
		this.shapeY = shapeY;
	}

	public void setGameOver(boolean gameOver) {
		this.gameOver = gameOver;
	}

	public int getLines() {
		return lines;
	}

	public void setLines(int lines) {
		this.lines = lines;
	}

	public boolean isPaused() {
		return paused;
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
		fireGamePaused();
	}

	public ShapeDirection getAutoShift() {
		return autoShift;
	}

	public void setAutoShift(ShapeDirection autoShift) {
		this.autoShift = autoShift;
	}
}
