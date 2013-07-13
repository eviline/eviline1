package org.eviline.fitness;

import java.util.concurrent.atomic.AtomicInteger;

import org.eviline.Block;
import org.eviline.Field;

public class DefaultFitness extends AbstractFitness {

	public DefaultFitness() {
		super(new double[0]);
	}
	
	public DefaultFitness(double[] parameters) {
		super(parameters);
	}
	
	@Override
	protected double score(Field field) {
		AtomicInteger solidCount = new AtomicInteger(0);
		for(Block[] row : field.getField()) {
			for(Block block : row) {
				if(block.isSolid())
					solidCount.incrementAndGet();
			}
		}
		return solidCount.get();
	}

}
