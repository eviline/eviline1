package org.tetrevil;

public class RandomShapeProvider implements ShapeProvider {

	@Override
	public Shape provideShape(Field field) {
		Shape[] shapes = Shape.values();
		return shapes[(int)(shapes.length * Math.random())];
	}

}
