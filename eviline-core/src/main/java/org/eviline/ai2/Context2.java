package org.eviline.ai2;

import org.eviline.ShapeType;

public class Context2 {
	private int searchDepth;
	private ShapeType[] knownNext;
	
	public Context2() {
	}
	
	public Context2 withSearchDepth(int searchDepth) {
		setSearchDepth(searchDepth);
		return this;
	}
	
	public Context2 withKnownNext(ShapeType[] knownNext) {
		setKnownNext(knownNext);
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
}
