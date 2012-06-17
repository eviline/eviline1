package org.tetrevil;

import java.io.Serializable;

public class BlockMetadata implements Serializable {
	
	public Shape shape;
	public boolean ghost;
	public boolean ghostClearable;
	
	public BlockMetadata() {}
	
	public BlockMetadata(Shape shape, boolean ghost) {
		this.shape = shape;
		this.ghost = ghost;
	}
	
	public BlockMetadata(BlockMetadata source) {
		this.shape = source.shape;
		this.ghost = source.ghost;
		this.ghostClearable = source.ghostClearable;
	}
	
}
