package org.tetrevil.event;

import java.util.EventListener;

import org.tetrevil.Field;

/**
 * {@link EventListener} for tetrevil {@link Field} events
 * @author robin
 *
 */
public interface TetrevilListener extends EventListener {
	/**
	 * A clock tick occurred
	 * @param e
	 */
	public void clockTicked(TetrevilEvent e);
	/**
	 * A game over occurred
	 * @param e
	 */
	public void gameOver(TetrevilEvent e);
	/**
	 * The active piece shifted left
	 * @param e
	 */
	public void shiftedLeft(TetrevilEvent e);
	/**
	 * The active piece shifted right
	 * @param e
	 */
	public void shiftedRight(TetrevilEvent e);
	/**
	 * The active piece rotated left
	 * @param e
	 */
	public void rotatedLeft(TetrevilEvent e);
	/**
	 * The active piece rotated right
	 * @param e
	 */
	public void rotatedRight(TetrevilEvent e);
	/**
	 * The game was reset
	 * @param e
	 */
	public void gameReset(TetrevilEvent e);
	/**
	 * The game was paused or unpaused
	 * @param e
	 */
	public void gamePaused(TetrevilEvent e);
	/**
	 * A line was cleared
	 * @param e
	 */
	public void linesCleared(TetrevilEvent e);
}
