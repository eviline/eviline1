package org.eviline.fitness;

import java.util.Arrays;

import org.eviline.BlockType;
import org.eviline.Field;

public abstract class AbstractFitness implements Fitness {
	protected double[] parameters = new double[0];
	
	@Override
	public double[] getParameters() {
		return Arrays.copyOf(parameters, parameters.length);
	}
	
	public void setParameters(double[] parameters) {
		this.parameters = Arrays.copyOf(parameters, parameters.length);
	}
	
	protected AbstractFitness(double[] parameters) {
		setParameters(parameters);
	}
	
	/**
	 * Fitness function used to score a board.  Higher score is worse for the player.
	 * @param field
	 * @return
	 */
	protected abstract double score(Field field);
	
	@Override
	public double score(Field before, Field after) {
		return score(after) - score(before);
	}
}