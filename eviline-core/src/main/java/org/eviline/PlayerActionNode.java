package org.eviline;

public class PlayerActionNode {
	private int[] xform;
	private Shape shape;
	private int x;
	private int y;
	
	public PlayerActionNode(Shape shape, int x, int y) {
//		xform = shape.symmetryTranslation(shape.rotateLeft().rotateLeft());
		if(xform != null && (shape.direction() == ShapeDirection.RIGHT || shape.direction() == ShapeDirection.DOWN)) {
			x -= xform[0];
			y -= xform[1];
			shape = shape.rotateLeft().rotateLeft();
		} else
			xform = null;
		this.shape = shape;
		this.x = x;
		this.y = y;
	}
	
	@Override
	public int hashCode() {
		return (1 + shape.ordinal()) * x * y;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null)
			return false;
		if(obj == this)
			return true;
		if(!(obj instanceof PlayerActionNode))
			return false;
		PlayerActionNode n = (PlayerActionNode) obj;
		return shape == n.shape && x == n.x && y == n.y;
	}

	public Shape getShape() {
		if(xform != null)
			return shape.rotateLeft().rotateLeft();
		return shape;
	}
	
	@Override
	public String toString() {
		return "[" + getShape() + " at (" + getX() + "," + getY() + ")]";
	}

	public int getX() {
		if(xform != null)
			return x + xform[0];
		return x;
	}

	public int getY() {
		if(xform != null)
			return y + xform[1];
		return y;
	}
}