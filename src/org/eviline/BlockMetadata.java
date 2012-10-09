package org.eviline;

import java.io.Serializable;

public class BlockMetadata implements Serializable {
	
	public Shape shape;
	public boolean ghost;
	public boolean ghostClearable;
	public int shapeId;
	
	public BlockMetadata() {}
	
	public BlockMetadata(Shape shape, boolean ghost, int shapeId) {
		this.shape = shape;
		this.ghost = ghost;
		this.shapeId = shapeId;
	}
	
	public BlockMetadata(BlockMetadata source) {
		this.shape = source.shape;
		this.ghost = source.ghost;
		this.ghostClearable = source.ghostClearable;
		this.shapeId = source.shapeId;
	}
	
}
