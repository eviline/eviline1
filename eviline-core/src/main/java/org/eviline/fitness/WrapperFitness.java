package org.eviline.fitness;

import org.eviline.Block;
import org.eviline.Field;

public class WrapperFitness extends AbstractFitness {
	
	protected Fitness delegate;
	
	public WrapperFitness(Fitness delegate) {
		this.delegate = delegate;
	}

	@Override
	protected double normalize(double score) {
		return score;
	}

	@Override
	public double score(Field field) {
		return delegate.score(field);
	}

	@Override
	public void paintImpossibles(Field field) {
		delegate.prepareField(field);
	}
	
	@Override
	public Field prepareField(Field field) {
		return delegate.prepareField(field);
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
