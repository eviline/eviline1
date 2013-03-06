package org.eviline.fitness;

import org.eviline.Block;
import org.eviline.Field;

public abstract class AbstractFitness implements Fitness {
	private static AbstractFitness defaultInstance;
	
	public static AbstractFitness getDefaultInstance() {
		if(defaultInstance == null)
			defaultInstance = new EvilineFitness();
		return defaultInstance;
	}
	
	public static void setDefaultInstance(AbstractFitness instance) {
		AbstractFitness.defaultInstance = instance;
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

	@Override
	public void prepareField(Field field) {
		paintImpossibles(field);
	}
	
	/**
	 * Fitness function used to score a board.  Higher score is worse for the player.
	 * @param field
	 * @return
	 */
	@Override
	public abstract double score(Field field);

	public abstract void paintImpossibles(Field field);

	public abstract void paintUnlikelies(Field field);

	public abstract void unpaintUnlikelies(Field field);

	public abstract void unpaintImpossibles(Field field);

}