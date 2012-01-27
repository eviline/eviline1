package org.tetrevil;

public class RandomShapeProvider implements ShapeProvider {

	@Override
	public Shape provideShape(Block[][] field) {
		Shape[] shapes = Shape.values();
		return shapes[(int)(shapes.length * Math.random())];
	}

}
