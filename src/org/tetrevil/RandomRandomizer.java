package org.tetrevil;

/**
 * {@link Randomizer} that chooses at random, not using a bag.
 * @author robin
 *
 */
public class RandomRandomizer implements Randomizer {

	@Override
	public Shape provideShape(Field field) {
		Shape[] shapes = Shape.values();
		return shapes[(int)(shapes.length * Math.random())];
	}

	@Override
	public String getRandomizerName() {
		return getClass().getName();
	}
}
