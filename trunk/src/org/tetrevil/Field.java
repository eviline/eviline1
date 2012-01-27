package org.tetrevil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.tetrevil.event.TetrevilEvent;
import org.tetrevil.event.TetrevilListener;

public class Field {
	public static final int HEIGHT = 20;
	public static final int WIDTH = 10;
	
	protected Block[][] field = new Block[HEIGHT + 3][WIDTH+6];
	protected ShapeProvider provider = new RandomShapeProvider();
	protected Shape shape;
	protected int shapeX;
	protected int shapeY;
	
	protected TetrevilListener[] listeners = new TetrevilListener[0];
	
	public Field() {
		for(int y = 0; y < field.length - 3; y++) {
			Arrays.fill(field[y], 0, 3, Block.X);
			Arrays.fill(field[y], field[y].length - 3, field[y].length, Block.X);
		}
		for(int y = field.length - 3; y < field.length; y++) {
			Arrays.fill(field[y], Block.X);
		}
	}
	
	public void clockTick() {
		if(shape == null) {
			shape = provider.provideShape(field);
			shapeY = 0;
			shapeX = WIDTH / 2 + 1;
		} else if(shape.intersects(field, shapeX, shapeY+1)) {
			Block[][] s = shape.shape();
			for(int y = 0; y < s.length; y++) {
				for(int x = 0; x < s[y].length; x++) {
					if(s[y][x] != null)
						field[y + shapeY][x + shapeX] = s[y][x];
				}
			}
			shape = null;
		} else {
			shapeY++;
		}
		fireClockTicked();
		if(shape != null && shape.intersects(field, shapeX, shapeY))
			fireGameOver();
	}
	
	public void shiftLeft() {
		if(shape == null || shape.intersects(field, shapeX-1, shapeY))
			return;
		shapeX--;
		fireShiftedLeft();
	}
	
	public void shiftRight() {
		if(shape == null || shape.intersects(field, shapeX+1, shapeY))
			return;
		shapeX++;
		fireShiftedRight();
	}
	
	public void rotateLeft() {
		if(shape == null || shape.rotateLeft().intersects(field, shapeX, shapeY))
			return;
		shape = shape.rotateLeft();
		fireRotatedLeft();
	}
	
	public void rotateRight() {
		if(shape == null || shape.rotateRight().intersects(field, shapeX, shapeY))
			return;
		shape = shape.rotateRight();
		fireRotatedRight();
	}
	
	public Block getBlock(int x, int y) {
		if(shape != null && x >= shapeX && y >= shapeY) {
			Block[][] s = shape.shape();
			if(x - shapeX < s[0].length && y - shapeY < s.length) {
				Block b;
				if((b = s[y - shapeY][x - shapeX]) != null)
					return b;
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
}
