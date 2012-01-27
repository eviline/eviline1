package org.tetrevil;

public class Field {
	public static final int HEIGHT = 20;
	public static final int WIDTH = 10;
	
	protected Block[][] field = new Block[HEIGHT + 3][WIDTH+6];
	protected Shape shape;
	protected int shapeX;
	protected int shapeY;
	
	public void tick() {
		
	}
	
	public void shiftLeft() {
		if(shape == null || shape.intersects(field, shapeX-1, shapeY))
			return;
		shapeX--;
	}
	
	public void shiftRight() {
		if(shape == null || shape.intersects(field, shapeX+1, shapeY))
			return;
		shapeX++;
	}
	
	public void rotateLeft() {
		if(shape == null || shape.rotateLeft().intersects(field, shapeX, shapeY))
			return;
		shape = shape.rotateLeft();
	}
	
	public void rotateRight() {
		if(shape == null || shape.rotateRight().intersects(field, shapeX, shapeY))
			return;
		shape = shape.rotateRight();
	}
	
}
