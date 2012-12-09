package org.eviline;

import java.util.Arrays;

public class PlayerAction {
	public static enum Type {
		ROTATE_LEFT,
		ROTATE_RIGHT,
		SHIFT_LEFT,
		SHIFT_RIGHT,
		DOWN_ONE
	}
	
	private Field startField;
	private Field endField;
	private Type type;
	private Shape startShape;
	private Shape endShape;
	private int startX;
	private int startY;
	private int endX;
	private int endY;
	
	private boolean possible;
	
	public PlayerAction(Field field, Type type, boolean reverse) {
		if(field.getShape() == null)
			throw new IllegalArgumentException("null field shape");
		if(!reverse)
			compute(field, type);
		else
			computeReverse(field, type);
		
		possible = !(startShape == endShape && startX == endX && startY == endY);
	}
	
	@Override
	public int hashCode() {
		return Arrays.<Object>asList(type, startShape, endShape, startX, endX, startY, endY).hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null)
			return false;
		if(obj == this)
			return true;
		if(!(obj instanceof PlayerAction))
			return false;
		PlayerAction opa = (PlayerAction) obj;
		return Arrays.<Object>asList(type, startShape, endShape, startX, endX, startY, endY).equals(
				Arrays.<Object>asList(opa.type, opa.startShape, opa.endShape, opa.startX, opa.endX, opa.startY, opa.endY));
	}
	
	private void compute(Field field, Type type) {
		this.type = type;
		startField = field.copy();
		startX = field.shapeX;
		startY = field.shapeY;
		startShape = field.shape;
		endField = startField.copy();
		
		switch(type) {
		case DOWN_ONE:
			if(!endField.shape.intersects(endField.field, endField.shapeX, endField.shapeY + 1))
				endField.shapeY += 1;
			break;
		case ROTATE_LEFT:
			endField.rotateLeft();
			break;
		case ROTATE_RIGHT:
			endField.rotateRight();
			break;
		case SHIFT_LEFT:
			endField.shiftLeft();
			break;
		case SHIFT_RIGHT:
			endField.shiftRight();
			break;
		}
		
		endShape = endField.shape;
		endX = endField.shapeX;
		endY = endField.shapeY;
	}
	
	private void computeReverse(Field field, Type type) {
		this.type = type;
		endField = field.copy();
		endX = field.shapeX;
		endY = field.shapeY;
		endShape = field.shape;
		startField = endField.copy();
		
		switch(type) {
		case DOWN_ONE:
			if(startField.shapeY == 0)
				break;
			if(!startField.shape.intersects(startField.field, startField.shapeX, startField.shapeY - 1))
				startField.shapeY -= 1;
			break;
		case ROTATE_LEFT:
			startField.reverseRotateLeft();
			break;
		case ROTATE_RIGHT:
			startField.reverseRotateRight();
			break;
		case SHIFT_LEFT:
			startField.shiftRight();
			break;
		case SHIFT_RIGHT:
			startField.shiftLeft();
			break;
		}
		
		startShape = startField.shape;
		startX = startField.shapeX;
		startY = startField.shapeY;
	}
	
	public Field getStartField() {
		return startField;
	}
	public Field getEndField() {
		return endField;
	}
	public Type getType() {
		return type;
	}
	public Shape getStartShape() {
		return startShape;
	}
	public Shape getEndShape() {
		return endShape;
	}
	public int getStartX() {
		return startX;
	}
	public int getStartY() {
		return startY;
	}
	public int getEndX() {
		return endX;
	}
	public int getEndY() {
		return endY;
	}

	public boolean isPossible() {
		return possible;
	}
}
