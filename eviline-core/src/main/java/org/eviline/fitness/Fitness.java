package org.eviline.fitness;

import org.eviline.Field;

public interface Fitness {

	/**
	 * Fitness function used to score a board.  Higher score is worse for the player.
	 * @param field
	 * @return
	 */
	public double score(Field before, Field after);

	public double[] getParameters();
	public void setParameters(double[] parameters);
}