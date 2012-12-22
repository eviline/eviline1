package org.eviline.fitness;

import org.eviline.Block;
import org.eviline.Field;

public abstract class Fitness {
	private static Fitness defaultInstance;
	
	public static Fitness getDefaultInstance() {
		if(defaultInstance == null)
			defaultInstance = new EvilineFitness();
		return defaultInstance;
	}
	
	public static void setDefaultInstance(Fitness instance) {
		Fitness.defaultInstance = instance;
	}
	
	protected double[] params = new double[0];
	
	public double[] getParams() {
		return params;
	}

	protected abstract double normalize(double score);

	public double scoreWithPaint(Field field) {
		paintImpossibles(field);
		double ret = score(field);
		unpaintImpossibles(field);
		return ret;
	}

	/**
	 * Fitness function used to score a board.  Higher score is worse for the player.
	 * @param field
	 * @return
	 */
	public abstract double score(Field field);

	public abstract void paintImpossibles(Field field);

	public abstract void paintImpossibles(Block[][] f);

	public abstract void paintUnlikelies(Field field);

	public abstract void paintUnlikelies(Block[][] f);

	public abstract void unpaintUnlikelies(Field field);

	public abstract void unpaintImpossibles(Field field);

}