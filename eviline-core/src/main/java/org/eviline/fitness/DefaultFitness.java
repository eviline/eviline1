package org.eviline.fitness;

import org.eviline.Field;

public class DefaultFitness extends AbstractFitness {

	@Override
	protected double normalize(double score) {
		return score;
	}

	@Override
	public double score(Field field) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void paintImpossibles(Field field) {
		// TODO Auto-generated method stub

	}

	@Override
	public void paintUnlikelies(Field field) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unpaintUnlikelies(Field field) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unpaintImpossibles(Field field) {
		// TODO Auto-generated method stub

	}

}
