package org.eviline.ai2;

import java.util.Arrays;

import org.eviline.Field;
import org.eviline.ShapeType;
import org.eviline.randomizer.Randomizer;

public class Context2 {
	private Field startingField;
	private int searchDepth;
	private ShapeType[] knownNext;
	private Randomizer unknownNext;
	
	public Context2() {
	}
	
	public Context2 deeper(Field deeperField) {
		ShapeType[] deeperKnownNext;
		Randomizer deeperUnknownKnext = unknownNext.clone();
		if(knownNext.length > 1) {
			deeperKnownNext = Arrays.copyOfRange(knownNext, 1, knownNext.length);
		} else {
			deeperKnownNext = new ShapeType[] {deeperUnknownKnext.provideShape(deeperField).type()};
		}
		return new Context2().
				withStartingField(deeperField).
				withSearchDepth(searchDepth - 1).
				withKnownNext(deeperKnownNext).
				withUnknownNext(deeperUnknownKnext);
	}
	
	public Context2 withSearchDepth(int searchDepth) {
		setSearchDepth(searchDepth);
		return this;
	}
	
	public Context2 withKnownNext(ShapeType[] knownNext) {
		setKnownNext(knownNext);
		return this;
	}
	
	public Context2 withUnknownNext(Randomizer unknownNext) {
		setUnknownNext(unknownNext);
		return this;
	}

	public Context2 withStartingField(Field startingField) {
		setStartingField(startingField);
		return this;
	}
	
	public int getSearchDepth() {
		return searchDepth;
	}

	public void setSearchDepth(int searchDepth) {
		this.searchDepth = searchDepth;
	}

	public ShapeType[] getKnownNext() {
		return knownNext;
	}

	public void setKnownNext(ShapeType[] knownNext) {
		this.knownNext = knownNext;
	}

	public Randomizer getUnknownNext() {
		return unknownNext;
	}

	public void setUnknownNext(Randomizer unknownNext) {
		this.unknownNext = unknownNext;
	}

	public Field getStartingField() {
		return startingField;
	}

	public void setStartingField(Field startingField) {
		this.startingField = startingField;
	}
}
