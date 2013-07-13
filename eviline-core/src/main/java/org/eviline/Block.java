package org.eviline;

import java.awt.Color;

public class Block implements Cloneable {
	private static final Block empty = new Block().withTypeAndColor(BlockType.EMPTY);
	private static final Block border = new Block().withTypeAndColor(BlockType.BORDER);
	private static final Block garbage = new Block().withTypeAndColor(BlockType.GARBAGE);
	private static final Block ghostInstance = new Block().withTypeAndColor(BlockType.GHOST);
	
	public static Block getEmpty() {
		return empty;
	}
	
	public static Block getBorder() {
		return border;
	}
	
	public static Block getGarbage() {
		return garbage;
	}
	
	public static Block getGhost() {
		return ghostInstance;
	}
	
	private BlockType type;
	private Shape shape;
	private boolean ghost;
	private boolean active;
	private int shapeId;
	private Color color;
	
	public Block() {
	}
	
	@Override
	public Block clone() {
		try {
			return (Block) super.clone();
		} catch(CloneNotSupportedException cnse) {
			throw new InternalError("Clone not supported?");
		}
	}
	
	public boolean isSolid() {
		return type.isSolid();
	}
	
	public Block withType(BlockType type) {
		setType(type);
		return this;
	}
	
	public Block withTypeAndColor(BlockType type) {
		setType(type);
		setColor(type.color());
		return this;
	}
	
	public Block withShape(Shape shape) {
		setShape(shape);
		return this;
	}
	
	public Block withGhost(boolean ghost) {
		setGhost(ghost);
		return this;
	}
	
	public Block withActive(boolean active) {
		setActive(active);
		return this;
	}
	
	public Block withShapeId(int shapeId) {
		setShapeId(shapeId);
		return this;
	}
	
	public Block withColor(Color color) {
		setColor(color);
		return this;
	}

	public BlockType getType() {
		return type;
	}

	public Shape getShape() {
		return shape;
	}

	public boolean isGhost() {
		return ghost;
	}

	public boolean isActive() {
		return active;
	}

	public int getShapeId() {
		return shapeId;
	}

	public Color getColor() {
		return color;
	}

	public void setType(BlockType type) {
		this.type = type;
	}

	public void setShape(Shape shape) {
		this.shape = shape;
	}

	public void setGhost(boolean ghost) {
		this.ghost = ghost;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void setShapeId(int shapeId) {
		this.shapeId = shapeId;
	}

	public void setColor(Color color) {
		this.color = color;
	}
}
