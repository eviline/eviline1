package org.tetrevil;

import java.util.Arrays;

import org.tetrevil.event.TetrevilEvent;
import org.tetrevil.event.TetrevilListener;
import org.tetrevil.swing.TetrevilTableModel;

/**
 * Object which keeps track of the tetris matrix itself.  It keeps an array of {@link Block} enums
 * to store the state of the matrix, using <code>null</code> to store an empty area.<p>
 * 
 * This is the "engine" of tetrevil.
 * @author robin
 *
 */
public class Field {
	/**
	 * The height of the matrix
	 */
	public static final int HEIGHT = 20;
	/**
	 * The width of the matrix
	 */
	public static final int WIDTH = 10;
	/**
	 * The size of the buffer area around the matrix in each direction
	 */
	public static final int BUFFER = 4;
	
	/**
	 * The matrix itself
	 */
	protected Block[][] field = new Block[HEIGHT + 2 * BUFFER][WIDTH + 2 * BUFFER];
	/**
	 * The source of shapes
	 */
	protected Randomizer provider = new RandomRandomizer();
	/**
	 * The current shape
	 */
	protected Shape shape;
	/**
	 * The horizontal position of the current shape
	 */
	protected int shapeX;
	/**
	 * The vertical position of the current shape
	 */
	protected int shapeY;
	/**
	 * Whether ghosting is enabled
	 */
	protected boolean ghosting = false;
	/**
	 * The vertical position of the current shape's ghost
	 */
	protected int ghostY;
	/**
	 * Whether a game is playing
	 */
	protected boolean playing;
	/**
	 * Whether a game is over
	 */
	protected boolean gameOver;
	/**
	 * Whether a game is paused
	 */
	protected boolean paused;
	/**
	 * The number of lines scored so far
	 */
	protected int lines;
	
	/**
	 * The direction of the currently active DAS
	 */
	protected ShapeDirection autoShift = null;
	
	/**
	 * Event listeners
	 */
	protected TetrevilListener[] listeners = new TetrevilListener[0];
	
	public Field() {
		this(false);
	}
	
	public Field(boolean evil) {
		for(int y = 0; y < BUFFER; y++) {
			Arrays.fill(field[y], 0, BUFFER, Block.X);
//			Arrays.fill(field[y], BUFFER, field[y].length - BUFFER, Block.G);
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
			provider = new MaliciousRandomizer();
		else
			provider = new RandomRandomizer();
	}
	
	/**
	 * Copy this {@link Field} into a target and return the target.
	 * @param target The destination and return {@link Field}
	 * @return The target
	 */
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
	
	/**
	 * Reset the field
	 */
	public void reset() {
		for(int y = 0; y < BUFFER; y++) {
			Arrays.fill(field[y], 0, BUFFER, Block.X);
			Arrays.fill(field[y], BUFFER, field[y].length - BUFFER, null);
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
		playing = false;
		fireGameReset();
	}
	
	/**
	 * Cause one clock tick.  One clock tick means one movement of gravity downwards.
	 * Tetrevil does not keep a separate 60Hz clock, or some other such independent clock.
	 */
	public void clockTick() {
		if(paused)
			return;
		if(gameOver)
			return;
		playing = true;
		if(shape == null) { // Create a new shape if there is no active one
			shape = provider.provideShape(this).type().starter();
			shapeY = shape.type().starterY();
			shapeX = WIDTH / 2 + 2 + shape.type().starterX();
			if(!shape.intersects(field, shapeX, shapeY+1)) // Move the shape down one row if possible
				shapeY++;
			reghost();
		} else if(shape.intersects(field, shapeX, shapeY+1)) { // If the shape can't be moved down a row...
			/*
			 * Copy this shape to the field, assuming a game over.  If any of the shape
			 * is within the playable field then un-game-over.
			 */
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
			shape = null; // No active shape
		} else {
			// Move the shape down one row and autoshift
			shapeY++;
			autoshift();
		}
		fireClockTicked(); // Fire a clock tick event
		if(shape != null && shape.intersects(field, shapeX, shapeY)) {
			// If we have an active shape and the shape intersects the field then game over
			gameOver = true;
		}
		if(gameOver == true)
			fireGameOver(); // Fire game over event
		if(shape == null) { // If there is no active shape, check for cleared lines
			for(int y = field.length - 1 - BUFFER; y >= BUFFER; y--) {
				boolean cleared = true;
				for(int x = BUFFER; x < field[y].length - BUFFER; x++) {
					if(field[y][x] == null)
						cleared = false;
				}
				if(cleared) {
					lines++;
					// Shift down the field
					for(int z = y - 1; z >= 0; z--) {
						System.arraycopy(field[z], 0, field[z+1], 0, field[z].length);
					}
					// Fill in the top row with nulls
					Arrays.fill(field[0], BUFFER, field[0].length - BUFFER, null);
					y = field.length - BUFFER;
					fireLinesCleared(); // Fire a line cleared event
				}
			}
		}
	}
	
	/**
	 * Returns whether the current shape is "grounded", e.g. can be locked but isn't
	 * @return
	 */
	public boolean isGrounded() {
		if(shape == null)
			return false;
		return shape.intersects(field, shapeX, shapeY+1);
	}
	
	/**
	 * Shift the current shape one to the left
	 */
	public void shiftLeft() {
		if(paused)
			return;
		if(shape == null || shape.intersects(field, shapeX-1, shapeY))
			return;
		shapeX--;
		reghost();
		fireShiftedLeft();
	}
	
	/**
	 * Shift the current shape one to the right
	 */
	public void shiftRight() {
		if(paused)
			return;
		if(shape == null || shape.intersects(field, shapeX+1, shapeY))
			return;
		shapeX++;
		reghost();
		fireShiftedRight();
	}
	
	/**
	 * Auto-shift the current shape all the way to the left or right.
	 */
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
	
	/**
	 * Recalculate the ghost location
	 */
	protected void reghost() {
		ghostY = shapeY;
		if(shape == null || !ghosting)
			return;
		while(!shape.intersects(field, shapeX, ghostY + 1))
			ghostY++;
	}
	
	/**
	 * Counter-clockwise shape rotation
	 */
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
				autoshift();
				fireRotatedLeft();
				return;
			}
		}
	}
	
	/**
	 * Clockwise shape rotation
	 */
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
				autoshift();
				fireRotatedRight();
				return;
			}
		}
	}
	
	/**
	 * Return the block at the specified x,y pair, with the current shape and current ghost applied.
	 * Used by field renderers such as {@link TetrevilTableModel}
	 * @param x
	 * @param y
	 * @return
	 */
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
//		if(x >= BUFFER && x < WIDTH + BUFFER && y < BUFFER)
//			return Block.G;
		return field[y][x];
	}
	
	/**
	 * Add a tetrevil listener to this field
	 * @param l
	 */
	public void addTetrevilListener(TetrevilListener l) {
		TetrevilListener[] ll = Arrays.copyOf(listeners, listeners.length + 1);
		ll[ll.length - 1] = l;
		listeners = ll;
	}
	
	/**
	 * Remove a tetrevil listener from this field
	 * @param l
	 */
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

	protected void fireLinesCleared() {
		TetrevilEvent e = null;
		TetrevilListener[] ll = listeners;
		for(int i = ll.length - 1; i >= 0; i--) {
			if(e == null)
				e = new TetrevilEvent(this, this);
			ll[i].linesCleared(e);
		}
	}

	public Randomizer getProvider() {
		return provider;
	}

	public void setProvider(Randomizer provider) {
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

	public Block[][] getField() {
		return field;
	}

	public boolean isGhosting() {
		return ghosting;
	}

	public void setGhosting(boolean ghosting) {
		this.ghosting = ghosting;
	}

	public boolean isPlaying() {
		return playing;
	}
}
