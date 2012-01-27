package org.tetrevil.event;

public interface TetrevilListener {
	public void clockTicked(TetrevilEvent e);
	public void gameOver(TetrevilEvent e);
	public void shiftedLeft(TetrevilEvent e);
	public void shiftedRight(TetrevilEvent e);
	public void rotatedLeft(TetrevilEvent e);
	public void rotatedRight(TetrevilEvent e);
}
