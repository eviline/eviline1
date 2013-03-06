package org.eviline.event;

import java.util.EventListener;

import org.eviline.Field;

/**
 * {@link EventListener} for tetrevil {@link Field} events
 * @author robin
 *
 */
public interface EvilineListener extends EventListener {
	
	public void shapeSpawned(EvilineEvent e);
	/**
	 * A clock tick occurred
	 * @param e
	 */
	public void clockTicked(EvilineEvent e);
	
	public void shapeLocked(EvilineEvent e);
	/**
	 * A game over occurred
	 * @param e
	 */
	public void gameOver(EvilineEvent e);
	/**
	 * The active piece shifted left
	 * @param e
	 */
	public void shiftedLeft(EvilineEvent e);
	/**
	 * The active piece shifted right
	 * @param e
	 */
	public void shiftedRight(EvilineEvent e);
	/**
	 * The active piece rotated left
	 * @param e
	 */
	public void rotatedLeft(EvilineEvent e);
	/**
	 * The active piece rotated right
	 * @param e
	 */
	public void rotatedRight(EvilineEvent e);
	/**
	 * The game was reset
	 * @param e
	 */
	public void gameReset(EvilineEvent e);
	/**
	 * The game was paused or unpaused
	 * @param e
	 */
	public void gamePaused(EvilineEvent e);
	/**
	 * A line was cleared
	 * @param e
	 */
	public void linesCleared(EvilineEvent e);
	
	public void garbageReceived(EvilineEvent e);
	
	public void hardDropped(EvilineEvent e);
}
