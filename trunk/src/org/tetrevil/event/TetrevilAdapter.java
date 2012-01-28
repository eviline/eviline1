package org.tetrevil.event;

public abstract class TetrevilAdapter implements TetrevilListener {

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

}
