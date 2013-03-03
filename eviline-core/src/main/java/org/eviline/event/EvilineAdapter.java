package org.eviline.event;

import java.io.Serializable;

/**
 * Abstract implementation of {@link EvilineListener}
 * @author robin
 *
 */
public abstract class EvilineAdapter implements EvilineListener, Serializable {

	@Override
	public void shapeSpawned(EvilineEvent e) {
	}
	
	@Override
	public void clockTicked(EvilineEvent e) {
	}

	@Override
	public void shapeLocked(EvilineEvent e) {
	}
	
	@Override
	public void gameOver(EvilineEvent e) {
	}

	@Override
	public void shiftedLeft(EvilineEvent e) {
	}

	@Override
	public void shiftedRight(EvilineEvent e) {
	}

	@Override
	public void rotatedLeft(EvilineEvent e) {
	}

	@Override
	public void rotatedRight(EvilineEvent e) {
	}

	@Override
	public void gameReset(EvilineEvent e) {
	}
	
	@Override
	public void gamePaused(EvilineEvent e) {
	}
	
	@Override
	public void linesCleared(EvilineEvent e) {
	}
	
	@Override
	public void garbageReceived(EvilineEvent e) {
	}
}
