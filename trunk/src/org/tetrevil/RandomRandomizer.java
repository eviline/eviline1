package org.tetrevil;

public class RandomRandomizer implements Randomizer {

	@Override
	public Shape provideShape(Field field) {
		Shape[] shapes = Shape.values();
		return shapes[(int)(shapes.length * Math.random())];
	}

}
