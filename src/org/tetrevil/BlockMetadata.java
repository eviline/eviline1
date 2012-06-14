package org.tetrevil;

import java.io.Serializable;

public class BlockMetadata implements Serializable {
	
	public Shape shape;
	public boolean ghost;
	
	public BlockMetadata() {}
	
	public BlockMetadata(Shape shape, boolean ghost) {
		this.shape = shape;
		this.ghost = ghost;
	}
	
	
}
