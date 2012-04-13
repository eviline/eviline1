package org.tetrevil.event;

import java.io.Serializable;

/**
 * Abstract implementation of {@link TetrevilListener}
 * @author robin
 *
 */
public abstract class TetrevilAdapter implements TetrevilListener, Serializable {

	@Override
	public void clockTicked(TetrevilEvent e) {
	}

	@Override
	public void gameOver(TetrevilEvent e) {
	}

	@Override
	public void shiftedLeft(TetrevilEvent e) {
	}

	@Override
	public void shiftedRight(TetrevilEvent e) {
	}

	@Override
	public void rotatedLeft(TetrevilEvent e) {
	}

	@Override
	public void rotatedRight(TetrevilEvent e) {
	}

	@Override
	public void gameReset(TetrevilEvent e) {
	}
	
	@Override
	public void gamePaused(TetrevilEvent e) {
	}
	
	@Override
	public void linesCleared(TetrevilEvent e) {
	}
}
