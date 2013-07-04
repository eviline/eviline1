package org.eviline;

import java.io.Serializable;
import java.util.Arrays;

import org.eviline.event.EventDispatcher;
import org.eviline.event.EvilineEvent;
import org.eviline.event.EvilineListener;
import org.eviline.fitness.AbstractFitness;
import org.eviline.randomizer.Randomizer;

/**
 * Object which keeps track of the tetris matrix itself.  It keeps an array of {@link Block} enums
 * to store the state of the matrix, using <code>null</code> to store an empty area.<p>
 * 
 * This is the "engine" of tetrevil.
 * @author robin
 *
 */
public class Field implements Serializable {
	private static final long serialVersionUID = -207525838052607892L;
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
	public static final int BUFFER = 6;
	
	/**
	 * The matrix itself
	 */
	protected Block[][] field = new Block[HEIGHT + 2 * BUFFER][WIDTH + 2 * BUFFER];
	protected transient BlockMetadata[][] metadata = new BlockMetadata[HEIGHT + 2 * BUFFER][WIDTH + 2 * BUFFER];
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
				System.arraycopy(field[y], 0, target.field[y], 0, field[y].length);
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
	
	public Field copy() {
		return copyInto(newInstance());
	}
	
	/**
	 * Reset the field
	 */
	public synchronized void reset() {
		if(unpausable)
			return;
		fireGameReset();
		for(int y = 0; y < BUFFER; y++) {
			Arrays.fill(field[y], 0, BUFFER, Block.X);
			Arrays.fill(field[y], BUFFER, field[y].length - BUFFER, null);
			Arrays.fill(metadata[y], BUFFER, metadata[y].length - BUFFER, null);
			Arrays.fill(field[y], field[y].length - BUFFER, field[y].length, Block.X);
		}
		for(int y = BUFFER; y < field.length - BUFFER; y++) {
			Arrays.fill(field[y], 0, BUFFER, Block.X);
			Arrays.fill(field[y], BUFFER, field[y].length - BUFFER, null);
			Arrays.fill(metadata[y], BUFFER, metadata[y].length - BUFFER, null);
			Arrays.fill(field[y], field[y].length - BUFFER, field[y].length, Block.X);
		}
		for(int y = field.length - BUFFER; y < field.length; y++) {
			Arrays.fill(field[y], Block.X);
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
			shapeY = shape.type().starterY();
//			shapeX = WIDTH / 2 + Field.BUFFER - 2 + shape.type().starterX();
//			shapeX = (WIDTH + 2 * BUFFER - shape.width()) / 2;
			shapeX = shape.type().starterX();
			if(!shape.intersects(field, shapeX, shapeY+1)) // Move the shape down one row if possible
				shapeY++;
			reghost();
			fireShapeSpawned();
		} else if(shape.intersects(field, shapeX, shapeY+1)) { // If the shape can't be moved down a row...
			/*
			 * Copy this shape to the field, assuming a game over.  If any of the shape
			 * is within the playable field then un-game-over.
			 */
			gameOver = true;
			for(int i = 0; i < 4; i++) {
				int y = shape.y(i);
				int x = shape.x(i);
				field[y + shapeY][x + shapeX] = shape.type().inactive();
				metadata[y + shapeY][x + shapeX] = new BlockMetadata(shape, false, shapeId);
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
		if(shape != null && shape.intersects(field, shapeX, shapeY)) {
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
					lines += comboMultiplier;
					multilines++;
					// Shift down the field
					for(int z = y - 1; z >= 0; z--) {
//						System.arraycopy(field[z], 0, field[z+1], 0, field[z].length);
//						System.arraycopy(metadata[z], 0, metadata[z+1], 0, metadata[z].length);
						field[z+1] = field[z];
						metadata[z+1] = metadata[z];
					}
					field[0] = new Block[WIDTH + 2 * BUFFER];
					metadata[0] = new BlockMetadata[WIDTH + 2 * BUFFER];
					// Fill in the top row with nulls
					Arrays.fill(field[0], 0, BUFFER, Block.X);
					Arrays.fill(field[0], WIDTH + BUFFER, WIDTH + 2*BUFFER, Block.X);
					y = field.length - BUFFER;
				}
			}
			
			if(multilines > 0) {
				if(optimizeCombos)
					comboMultiplier++;
				else
					comboMultiplier = 1;
				score += scoreFactor * 1000 * multilines * multilines;
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
		return shape.intersects(field, shapeX, shapeY+1);
	}
	
	/**
	 * Shift the current shape one to the left
	 */
	public synchronized void shiftLeft() {
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
	public synchronized void shiftRight() {
		if(paused)
			return;
		if(shape == null || shape.intersects(field, shapeX+1, shapeY))
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
		while(!shape.intersects(field, shapeX, shapeY + 1)) {
			score += 2 * scoreFactor;
			clockTick();
		}
	}
	
	public synchronized void hardDrop() {
		if(paused)
			return;
		if(shape == null)
			return;
		while(!shape.intersects(field, shapeX, shapeY + 1)) {
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
	public synchronized void reghost() {
		if(!ghosting)
			return;
		for(int y = 0; y < HEIGHT + 2 * BUFFER; y++) {
			for(int x = 0; x < WIDTH + 2 * BUFFER; x++) {
				if(metadata[y][x] == null)
					continue;
				metadata[y][x].ghostClearable = false;
			}
		}
		ghostY = shapeY;
		if(shape == null)
			return;
		while(!shape.intersects(field, shapeX, ghostY + 1))
			ghostY++;
		for(int i = 0; i < 4; i++) {
			int by = ghostY + shape.y(i);
			boolean ghostClear = true;
			for(int x = BUFFER; x < WIDTH + BUFFER; x++)
				if(getBlock(x, by) == null)
					ghostClear = false;
			if(ghostClear) {
				for(int x = BUFFER; x < WIDTH + BUFFER; x++) {
					if(metadata[by][x] == null)
						continue;
					metadata[by][x].ghostClearable = true;
				}
			}
		}
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
			if(!rotated.intersects(field, x, y)) {
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
			if(!rotated.intersects(field, x, y)) {
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
	
	protected void applyGarbage() {
		int lines = this.garbage;
		if(lines == 0)
			return;
		for(int i = lines; i < field.length; i++) {
			System.arraycopy(field[i], 0, field[i - lines], 0, field[i].length);
			System.arraycopy(metadata[i], 0, metadata[i-lines], 0, metadata[i].length);
		}
		for(int i = HEIGHT + BUFFER - lines; i < HEIGHT + BUFFER; i++) {
			int x = (int)(WIDTH * Math.random());
			field[i][x] = null;
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
	
	public synchronized Block getFieldBlock(int x, int y) {
		return getBlock(x + Field.BUFFER, y + Field.BUFFER);
	}
	
	public synchronized BlockMetadata getMetadata(int x, int y) {
		if(shape != null && x >= shapeX && y >= shapeY) {
			Block[][] s = shape.shape();
			if(x - shapeX < s[0].length && y - shapeY < s.length) {
				Block b;
				if((b = s[y - shapeY][x - shapeX]) != null)
					return new BlockMetadata(shape, false, -1);
			}
			if(x - shapeX < s[0].length && y >= ghostY && y - ghostY < s.length) {
				if(s[y - ghostY][x - shapeX] != null)
					return new BlockMetadata(shape, true, -1);
			}
		}
//		if(x >= BUFFER && x < WIDTH + BUFFER && y < BUFFER)
//			return Block.G;
		return metadata[y][x];
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
}
