package org.eviline.fitness;

import org.eviline.Block;
import org.eviline.Field;

public class HybridFitness extends AbstractFitness {

	protected AbstractFitness[] functions = new AbstractFitness[] {
			new EvilineFitness(),
			new ElTetrisFitness()
	};
	
	@Override
	protected double normalize(double score) {
		double norm = 0;
		for(AbstractFitness ff : functions) {
			norm += ff.normalize(score);
		}
		return norm / functions.length;
	}
	
	@Override
	public double score(Field field) {
		double score = 0;
		for(AbstractFitness ff : functions) {
			double ffs = ff.scoreWithPaint(field);
			double nffs = ff.normalize(ffs);
//			System.out.println(ff + " -> " + ffs + " -> " + nffs);
			score += nffs;
		}
		return score / functions.length;
	}

	@Override
	public void paintImpossibles(Field field) {
	}

	@Override
	public void paintUnlikelies(Field field) {
	}

	@Override
	public void unpaintUnlikelies(Field field) {
	}

	@Override
	public void unpaintImpossibles(Field field) {
	}

}
