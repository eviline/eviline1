package org.eviline.ai2;

import org.eviline.Field;
import org.eviline.Shape;

public class Decision2 {
	private Field startingField;
	private Field endingField;
	private Shape endingShape;
	private int endingShapeX;
	private int endingShapeY;
	
	public Decision2() {
	}
	
	public Decision2 withStartingField(Field startingField) {
		setStartingField(startingField);
		return this;
	}
	
	public Decision2 withEndingField(Field endingField) {
		setEndingField(endingField);
		return this;
	}
	
	public Decision2 withEndingShape(Shape endingShape) {
		setEndingShape(endingShape);
		return this;
	}
	
	public Decision2 withEndingShapeX(int endingShapeX) {
		setEndingShapeX(endingShapeX);
		return this;
	}
	
	public Decision2 withEndingShapeY(int endingShapeY) {
		setEndingShapeY(endingShapeY);
		return this;
	}
	
	public Field getStartingField() {
		return startingField;
	}
	public void setStartingField(Field startingField) {
		this.startingField = startingField;
	}
	public Field getEndingField() {
		return endingField;
	}
	public void setEndingField(Field endingField) {
		this.endingField = endingField;
	}
	public Shape getEndingShape() {
		return endingShape;
	}
	public void setEndingShape(Shape endingShape) {
		this.endingShape = endingShape;
	}
	public int getEndingShapeX() {
		return endingShapeX;
	}
	public void setEndingShapeX(int endingShapeX) {
		this.endingShapeX = endingShapeX;
	}
	public int getEndingShapeY() {
		return endingShapeY;
	}
	public void setEndingShapeY(int endingShapeY) {
		this.endingShapeY = endingShapeY;
	}
}
