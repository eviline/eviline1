package org.eviline;

import java.io.Serializable;
import java.util.Arrays;

import org.eviline.event.EventDispatcher;
import org.eviline.event.EvilineEvent;
import org.eviline.event.EvilineListener;
import org.eviline.fitness.AbstractFitness;
import org.eviline.randomizer.Randomizer;

/**
 * Object which keeps track of the tetris matrix itself.  It keeps an array of {@link BlockType} enums
 * to store the state of the matrix, using <code>null</code> to store an empty area.<p>
 * 
 * This is the "engine" of tetrevil.
 * @author robin
 *
 */
public class Field implements Serializable, Cloneable {
	private static final long serialVersionUID = -207525838052607892L;
	/**
	 * The height of the matrix
	 */
	private static final int DEFAULT_HEIGHT = 20;
	/**
	 * The width of the matrix
	 */
	private static final int DEFAULT_WIDTH = 10;
	/**
	 * The size of the buffer area around the matrix in each direction
	 */
	public static final int BUFFER = 6;
	
	protected int width = DEFAULT_WIDTH;
	
	protected int height = DEFAULT_HEIGHT;
	
	/**
	 * The matrix itself
	 */
	protected Block[][] field;
	/**
	 * The source of shapes
	 */
	protected Randomizer provider;
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
	
	protected int garbage;
	/**
	 * The number of lines scored so far
	 */
	protected int lines;
	
	protected double score;
	
	protected double scoreFactor = 1;
	
	/**
	 * The direction of the currently active DAS
	 */
	protected ShapeDirection autoShift = null;
	
	protected boolean unpausable = false;
	
	protected boolean optimizeCombos;
	
	protected int comboMultiplier = 1;
	
	protected transient boolean winner;
	
	protected transient boolean multiplayer;
	
	protected transient int shapeId;
	
	/**
	 * Event listeners
	 */
	protected transient EvilineListener[] listeners = new EvilineListener[0];
	
	protected transient EventDispatcher dispatcher = null;
	
	public Field() {
		this(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}
	
	public Field(int width, int height) {
		this.width = width;
		this.height = height;
		field = new Block[height + 2 * BUFFER][width + 2 * BUFFER];
		reset();
	}
	
	public Field newInstance() {
		return new Field();
	}
	
	/**
	 * Copy this {@link Field} into a target and return the target.
	 * @param target The destination and return {@link Field}
	 * @return The target
	 */
	public Field copyInto(Field target) {
		if(field != null) {
			for(int y = 0; y < field.length; y++) {
				for(int x = 0; x < field[y].length; x++) {
					target.field[y][x] = field[y][x].clone();
				}
			}
		}
		target.provider = provider;
		target.shape = shape;
		target.shapeX = shapeX;
		target.shapeY = shapeY;
		target.gameOver = gameOver;
		target.lines = lines;
		target.score = score;
		target.scoreFactor = scoreFactor;
		return target;
	}
	
	public Field clone() {
		try {
			return copyInto((Field) super.clone());
		} catch(CloneNotSupportedException cnse) {
			throw new InternalError("Clone not supported?");
		}
	}
	
	/**
	 * Reset the field
	 */
	public synchronized void reset() {
		if(unpausable)
			return;
		fireGameReset();
		for(int y = 0; y < field.length - BUFFER; y++) {
			Arrays.fill(field[y], 0, BUFFER, Block.getBorder());
			Arrays.fill(field[y], BUFFER, field[y].length - BUFFER, Block.getEmpty());
			Arrays.fill(field[y], field[y].length - BUFFER, field[y].length, Block.getBorder());
		}
		for(int y = field.length - BUFFER; y < field.length; y++) {
			Arrays.fill(field[y], Block.getBorder());
		}
		shape = null;
		gameOver = false;
		lines = 0;
		score = 0;
		scoreFactor = 1;
		autoShift = null;
		playing = false;
	}
	
	/**
	 * Cause one clock tick.  One clock tick means one movement of gravity downwards.
	 * Tetrevil does not keep a separate 60Hz clock, or some other such independent clock.
	 */
	public synchronized void clockTick() {
		if(paused)
			return;
		if(gameOver)
			return;
		playing = true;
		if(shape == null) { // Create a new shape if there is no active one
			shapeId++;
			shape = provider.provideShape(this);
			if(shape == null)
				return;
			shape = shape.type().starter();
			shapeY = shape.type().starterY(this);
//			shapeX = WIDTH / 2 + Field.BUFFER - 2 + shape.type().starterX();
//			shapeX = (WIDTH + 2 * BUFFER - shape.width()) / 2;
			shapeX = shape.type().starterX(this);
			if(!shape.intersects(this, shapeX, shapeY+1)) // Move the shape down one row if possible
				shapeY++;
			reghost();
			fireShapeSpawned();
		} else if(shape.intersects(this, shapeX, shapeY+1)) { // If the shape can't be moved down a row...
			/*
			 * Copy this shape to the field, assuming a game over.  If any of the shape
			 * is within the playable field then un-game-over.
			 */
			gameOver = true;
			for(int i = 0; i < 4; i++) {
				int y = shape.y(i);
				int x = shape.x(i);
				field[y + shapeY][x + shapeX].withType(shape.type().block()).withActive(false);
				if(y + shapeY >= BUFFER)
					gameOver = false;
			}
			shape = null; // No active shape
			reghost();
			fireShapeLocked();
			applyGarbage();
		} else {
			// Move the shape down one row and autoshift
			shapeY++;
			autoshift();
		}
		fireClockTicked(); // Fire a clock tick event
		if(shape != null && shape.intersects(this, shapeX, shapeY)) {
			// If we have an active shape and the shape intersects the field then game over
			gameOver = true;
		}
		if(gameOver == true)
			fireGameOver(); // Fire game over event
		if(shape == null) { // If there is no active shape, check for cleared lines
			int multilines = 0;
			for(int y = field.length - 1 - BUFFER; y >= BUFFER; y--) {
				boolean cleared = true;
				for(int x = BUFFER; x < field[y].length - BUFFER; x++) {
					if(field[y][x] == null)
						cleared = false;
				}
				if(cleared) {
					lines++;
					multilines++;
					// Shift down the field
					for(int z = y - 1; z >= 0; z--) {
//						System.arraycopy(field[z], 0, field[z+1], 0, field[z].length);
//						System.arraycopy(metadata[z], 0, metadata[z+1], 0, metadata[z].length);
						field[z+1] = field[z];
					}
					field[0] = new Block[width + 2 * BUFFER];
					// Fill in the top row with nulls
					Arrays.fill(field[0], 0, BUFFER, Block.getBorder());
					Arrays.fill(field[0], BUFFER, width + BUFFER, Block.getEmpty());
					Arrays.fill(field[0], width + BUFFER, width + 2*BUFFER, Block.getBorder());
					y = field.length - BUFFER;
				}
			}
			
			if(multilines > 0) {
				if(optimizeCombos && multilines > 1)
					comboMultiplier++;
				score += Math.pow(scoreFactor * Math.pow(multilines, 4), comboMultiplier);
				fireLinesCleared(multilines);
			} else
				comboMultiplier = 1;
		}
	}
	
	/**
	 * Returns whether the current shape is "grounded", e.g. can be locked but isn't
	 * @return
	 */
	public synchronized boolean isGrounded() {
		if(shape == null)
			return false;
		return shape.intersects(this, shapeX, shapeY+1);
	}
	
	/**
	 * Shift the current shape one to the left
	 */
	public synchronized void shiftLeft() {
		if(paused)
			return;
		if(shape == null || shape.intersects(this, shapeX-1, shapeY))
			return;
		shapeX--;
		reghost();
		fireShiftedLeft();
	}
	
	/**
	 * Shift the current shape one to the right
	 */
	public synchronized void shiftRight() {
		if(paused)
			return;
		if(shape == null || shape.intersects(this, shapeX+1, shapeY))
			return;
		shapeX++;
		reghost();
		fireShiftedRight();
	}
	
	public synchronized void shiftDown() {
		if(paused)
			return;
		score += scoreFactor;
		clockTick();
	}
	
	public synchronized void softDrop() {
		if(paused)
			return;
		if(shape == null)
			return;
		while(!shape.intersects(this, shapeX, shapeY + 1)) {
			score += 2 * scoreFactor;
			clockTick();
		}
	}
	
	public synchronized void hardDrop() {
		if(paused)
			return;
		if(shape == null)
			return;
		while(!shape.intersects(this, shapeX, shapeY + 1)) {
			score += 3 * scoreFactor;
			clockTick();
		}
		clockTick();
		fireHardDropped();
	}
	
	/**
	 * Auto-shift the current shape all the way to the left or right.
	 */
	public synchronized void autoshift() {
		if(paused || autoShift == null)
			return;
		switch(autoShift) {
		case LEFT:
			for(int i = 0; i < width; i++)
				shiftLeft();
			reghost();
			break;
		case RIGHT:
			for(int i = 0; i < width; i++)
				shiftRight();
			reghost();
			break;
		}
	}
	
	/**
	 * Recalculate the ghost location
	 */
	public synchronized void reghost() {
		if(!ghosting)
			return;
		ghostY = shapeY;
		if(shape == null)
			return;
		while(!shape.intersects(this, shapeX, ghostY + 1))
			ghostY++;
	}
	
	/**
	 * Counter-clockwise shape rotation
	 */
	public synchronized void rotateLeft() {
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
			if(!rotated.intersects(this, x, y)) {
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
	
	public synchronized void reverseRotateLeft() {
		if(paused || shape == null)
			return;
		Shape rotated = shape.rotateRight();
		int[][] table = KickTable.forShape(shape.type(), rotated.direction(), shape.direction()).table();
		
		int kickI = -1;
		for(int i = table.length - 1; i >= 0; i--) {
			int[] kick = table[i];
			int x = shapeX - kick[0];
			int y = shapeY - kick[1];
			if(!rotated.intersects(this, x, y)) {
				kickI = i;
			}
		}
		if(kickI == -1)
			return;
		shapeX -= table[kickI][0];
		shapeY -= table[kickI][1];
		shape = rotated;
		reghost();
		autoshift();
	}
	
	/**
	 * Clockwise shape rotation
	 */
	public synchronized void rotateRight() {
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
			if(!rotated.intersects(this, x, y)) {
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
	
	public synchronized void reverseRotateRight() {
		if(paused || shape == null)
			return;
		Shape rotated = shape.rotateLeft();
		int[][] table = KickTable.forShape(shape.type(), rotated.direction(), shape.direction()).table();
		
		int kickI = -1;
		for(int i = table.length - 1; i >= 0; i--) {
			int[] kick = table[i];
			int x = shapeX - kick[0];
			int y = shapeY - kick[1];
			if(!rotated.intersects(this, x, y)) {
				kickI = i;
			}
		}
		if(kickI == -1)
			return;
		shapeX -= table[kickI][0];
		shapeY -= table[kickI][1];
		shape = rotated;
		reghost();
		autoshift();
	}
	
	public synchronized void garbage(int lines) {
		this.garbage += lines;
	}
	
	public void applyGarbage() {
		int lines = this.garbage;
		if(lines == 0)
			return;
		for(int i = lines; i < height + BUFFER; i++) {
			System.arraycopy(field[i], 0, field[i - lines], 0, field[i].length);
		}
		for(int i = height + BUFFER - lines; i < height + BUFFER; i++) {
			Arrays.fill(field[i], BUFFER, BUFFER + width, Block.getGarbage());
			int x = (int)(width * Math.random());
			field[i][x] = Block.getEmpty();
		}
		fireGarbageReceived(lines);
		this.garbage = 0;
	}
	
	/**
	 * Return the block at the specified x,y pair, with the current shape and current ghost applied.
	 * Used by field renderers such as {@link TetrevilTableModel}
	 * @param x
	 * @param y
	 * @return
	 */
	public synchronized Block getBlock(int x, int y) {
		if(shape != null && x >= shapeX && y >= shapeY) {
			BlockType[][] s = shape.shape();
			if(x - shapeX < s[0].length && y - shapeY < s.length) {
				BlockType b;
				if((b = s[y - shapeY][x - shapeX]) != null)
					return field[y][x];
			}
			if(x - shapeX < s[0].length && y >= ghostY && y - ghostY < s.length) {
				if(s[y - ghostY][x - shapeX] != null)
					return Block.getGhost();
			}
		}
//		if(x >= BUFFER && x < WIDTH + BUFFER && y < BUFFER)
//			return Block.G;
		return field[y][x];
	}
	
	public synchronized Block getFieldBlock(int x, int y) {
		return getBlock(x + Field.BUFFER, y + Field.BUFFER);
	}
	
	/**
	 * Add a tetrevil listener to this field
	 * @param l
	 */
	public synchronized void addEvilineListener(EvilineListener l) {
		if(listeners == null)
			listeners = new EvilineListener[0];
		EvilineListener[] ll = Arrays.copyOf(listeners, listeners.length + 1);
		ll[ll.length - 1] = l;
		listeners = ll;
	}
	
	/**
	 * Remove a tetrevil listener from this field
	 * @param l
	 */
	public synchronized void removeEvilineListener(EvilineListener l) {
		if(listeners == null)
			listeners = new EvilineListener[0];
		EvilineListener[] listeners = this.listeners;
		for(int i = listeners.length - 1; i >= 0; i--) {
			if(l == listeners[i]) {
				EvilineListener[] ll = Arrays.copyOf(listeners, listeners.length - 1);
				System.arraycopy(listeners, i+1, ll, i, listeners.length - i - 1);
				this.listeners = ll;
				break;
			}
		}
	}
	
	protected void fireClockTicked() {
		if(listeners == null)
			return;
		EvilineEvent e = null;
		EvilineListener[] ll = listeners;
		for(int i = ll.length - 1; i >= 0; i--) {
			if(dispatcher == null)
				dispatcher = new EventDispatcher();
			if(e == null)
				e = new EvilineEvent(this, EvilineEvent.CLOCK_TICKED, this);
			dispatcher.clockTicked(ll[i], e);
		}
	}
	
	protected void fireGameOver() {
		if(listeners == null)
			return;
		EvilineEvent e = null;
		EvilineListener[] ll = listeners;
		for(int i = ll.length - 1; i >= 0; i--) {
			if(dispatcher == null)
				dispatcher = new EventDispatcher();
			if(e == null)
				e = new EvilineEvent(this, EvilineEvent.GAME_OVER, this);
			dispatcher.gameOver(ll[i], e);
		}
	}

	protected void fireGamePaused() {
		if(listeners == null)
			return;
		EvilineEvent e = null;
		EvilineListener[] ll = listeners;
		for(int i = ll.length - 1; i >= 0; i--) {
			if(dispatcher == null)
				dispatcher = new EventDispatcher();
			if(e == null)
				e = new EvilineEvent(this, EvilineEvent.GAME_PAUSED, this);
			dispatcher.gamePaused(ll[i], e);
		}
	}

	protected void fireGameReset() {
		if(listeners == null)
			return;
		EvilineEvent e = null;
		EvilineListener[] ll = listeners;
		for(int i = ll.length - 1; i >= 0; i--) {
			if(dispatcher == null)
				dispatcher = new EventDispatcher();
			if(e == null)
				e = new EvilineEvent(this, EvilineEvent.GAME_RESET, this);
			dispatcher.gameReset(ll[i], e);
		}
	}

	protected void fireShiftedLeft() {
		if(listeners == null)
			return;
		EvilineEvent e = null;
		EvilineListener[] ll = listeners;
		for(int i = ll.length - 1; i >= 0; i--) {
			if(dispatcher == null)
				dispatcher = new EventDispatcher();
			if(e == null)
				e = new EvilineEvent(this, EvilineEvent.SHIFTED_LEFT, this);
			dispatcher.shiftedLeft(ll[i], e);
		}
	}

	protected void fireShiftedRight() {
		if(listeners == null)
			return;
		EvilineEvent e = null;
		EvilineListener[] ll = listeners;
		for(int i = ll.length - 1; i >= 0; i--) {
			if(dispatcher == null)
				dispatcher = new EventDispatcher();
			if(e == null)
				e = new EvilineEvent(this, EvilineEvent.SHIFTED_RIGHT, this);
			dispatcher.shiftedRight(ll[i], e);
		}
	}

	protected void fireRotatedLeft() {
		if(listeners == null)
			return;
		EvilineEvent e = null;
		EvilineListener[] ll = listeners;
		for(int i = ll.length - 1; i >= 0; i--) {
			if(dispatcher == null)
				dispatcher = new EventDispatcher();
			if(e == null)
				e = new EvilineEvent(this, EvilineEvent.ROTATED_LEFT, this);
			dispatcher.rotatedLeft(ll[i], e);
		}
	}

	protected void fireRotatedRight() {
		if(listeners == null)
			return;
		EvilineEvent e = null;
		EvilineListener[] ll = listeners;
		for(int i = ll.length - 1; i >= 0; i--) {
			if(dispatcher == null)
				dispatcher = new EventDispatcher();
			if(e == null)
				e = new EvilineEvent(this, EvilineEvent.ROTATED_RIGHT, this);
			dispatcher.rotatedRight(ll[i], e);
		}
	}

	protected void fireLinesCleared(int lines) {
		if(listeners == null)
			return;
		EvilineEvent e = null;
		EvilineListener[] ll = listeners;
		for(int i = ll.length - 1; i >= 0; i--) {
			if(dispatcher == null)
				dispatcher = new EventDispatcher();
			if(e == null)
				e = new EvilineEvent(this, EvilineEvent.LINES_CLEARED, this, lines);
			dispatcher.linesCleared(ll[i], e);
		}
	}
	
	protected void fireGarbageReceived(int lines) {
		if(listeners == null)
			return;
		EvilineEvent e = null;
		EvilineListener[] ll = listeners;
		for(int i = ll.length - 1; i >= 0; i--) {
			if(dispatcher == null)
				dispatcher = new EventDispatcher();
			if(e == null)
				e = new EvilineEvent(this, EvilineEvent.GARBAGE_RECEIVED, this, lines);
			dispatcher.garbageReceived(ll[i], e);
		}
	}
	
	protected void fireShapeSpawned() {
		if(listeners == null)
			return;
		EvilineEvent e = null;
		EvilineListener[] ll = listeners;
		for(int i = ll.length - 1; i >= 0; i--) {
			if(dispatcher == null)
				dispatcher = new EventDispatcher();
			if(e == null)
				e = new EvilineEvent(this, EvilineEvent.SHAPE_SPAWNED, this, lines);
			dispatcher.shapeSpawned(ll[i], e);
		}
	}
	
	protected void fireShapeLocked() {
		if(listeners == null)
			return;
		EvilineEvent e = null;
		EvilineListener[] ll = listeners;
		for(int i = ll.length - 1; i >= 0; i--) {
			if(dispatcher == null)
				dispatcher = new EventDispatcher();
			if(e == null)
				e = new EvilineEvent(this, EvilineEvent.SHAPE_LOCKED, this, lines);
			dispatcher.shapeLocked(ll[i], e);
		}
	}

	protected void fireHardDropped() {
		if(listeners == null)
			return;
		EvilineEvent e = null;
		EvilineListener[] ll = listeners;
		for(int i = ll.length - 1; i >= 0; i--) {
			if(dispatcher == null)
				dispatcher = new EventDispatcher();
			if(e == null)
				e = new EvilineEvent(this, EvilineEvent.HARD_DROP, this);
			dispatcher.hardDropped(ll[i], e);
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

	public int getGhostY() {
		return ghostY;
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
		if(unpausable)
			return;
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

	public void setField(Block[][] field) {
		this.field = field;
	}
	
	public boolean isGhosting() {
		return ghosting;
	}

	public void setGhosting(boolean ghosting) {
		this.ghosting = ghosting;
	}
	
	public boolean isUnpausable() {
		return unpausable;
	}
	
	public void setUnpausable(boolean unpausable) {
		this.unpausable = unpausable;
	}

	public boolean isPlaying() {
		return playing;
	}

	public boolean isWinner() {
		return winner;
	}

	public void setWinner(boolean winner) {
		this.winner = winner;
	}

	public boolean isMultiplayer() {
		return multiplayer;
	}
	
	public void setMultiplayer(boolean multiplayer) {
		this.multiplayer = multiplayer;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(220);
		for(int y = 0; y < field.length - BUFFER + 1; y++) {
			for(int x = BUFFER - 1; x < field[y].length - BUFFER + 1; x++) {
				sb.append(field[y][x] == null ? " " : field[y][x]);
			}
			sb.append("\n");
		}
		sb.append("Score " + AbstractFitness.getDefaultInstance().score(this));
		return sb.toString();
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public double getScoreFactor() {
		return scoreFactor;
	}

	public void setScoreFactor(double scoreFactor) {
		this.scoreFactor = scoreFactor;
	}

	public boolean isOptimizeCombos() {
		return optimizeCombos;
	}

	public void setOptimizeCombos(boolean optimizeCombos) {
		this.optimizeCombos = optimizeCombos;
	}

	public int getShapeId() {
		return shapeId;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
