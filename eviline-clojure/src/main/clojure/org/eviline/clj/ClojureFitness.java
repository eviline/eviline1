package org.eviline.clj;

import org.eviline.Field;
import org.eviline.fitness.DefaultFitness;
import org.eviline.fitness.Fitness;

public class ClojureFitness extends DefaultFitness {

	@Override
	public double score(Field field) {
		throw new InternalError("This class should be in clojure");
	}

	@Override
	public Field prepareField(Field field) {
		throw new InternalError("This class should be in clojure");
	}
}
