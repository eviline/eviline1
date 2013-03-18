package org.eviline.ai;

import java.util.List;

import org.eviline.Field;
import org.eviline.PlayerAction;
import org.eviline.Shape;
import org.eviline.ShapeType;

/**
 * A decision that can be made by the {@link DefaultAIKernel}.
 * @author robin
 *
 */
public class Decision {
	/**
	 * The score of this decision
	 */
	public double score;
	/**
	 * The shape that was decided on
	 */
	public ShapeType type;
	/**
	 * The field that was decided on
	 */
	public Field field;
	
	public Shape bestShape;
	
	public List<PlayerAction> bestPath;
	
	public int bestShapeX;
	
	public int bestShapeY;
	
	public double worstScore = Double.NEGATIVE_INFINITY;
	/**
	 * One level deeper in the final decision path
	 */
	public volatile Decision deeper;
	
	public Decision() {}
	public Decision(ShapeType type) {
		this.type = type;
	}
	public Decision(ShapeType type, double score) {
		this.type = type;
		this.score = score;
	}
	public Decision(ShapeType type, Field field) {
		this.type = type;
		this.field = field;
	}
	public Decision(ShapeType type, double score, Field field) {
		this.type = type;
		this.score = score;
		this.field = field;
	}
	public Decision(ShapeType type, double score, Field field, Shape bestShape, int bestShapeX, int bestShapeY) {
		this.type = type;
		this.score = score;
		this.field = field;
		this.bestShape = bestShape;
		this.bestShapeX = bestShapeX;
		this.bestShapeY = bestShapeY;
	}
	/**
	 * Copy this {@link Decision}
	 * @return
	 */
	public Decision copy() {
		Decision c = new Decision(type, score, field);
		if(deeper == null)
			c.deeper = null;
		else if(deeper != this)
			c.deeper = deeper.copy();
		else
			c.deeper = c;
		c.bestPath = bestPath;
		c.bestShape = bestShape;
		c.bestShapeX = bestShapeX;
		c.bestShapeY = bestShapeY;
		c.worstScore = worstScore;
		return c;
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("[");
		sb.append(score);
		sb.append(":");
		sb.append(type);
		Decision d = deeper;
		while(d != null) {
			sb.append(" -> ");
			sb.append(d.type);
			if(d == d.deeper)
				break;
			d = d.deeper;
		}
		sb.append("]");
		return sb.toString();
	}
	/**
	 * Generate a string of shapes that represent the decision path
	 * @return
	 */
	public String taunt() {
		if(deeper == this)
			return String.valueOf(type);
		if(deeper != null)
			return type + deeper.taunt();
		return String.valueOf(type);
	}
	/**
	 * Return the deepest decision in the decision path
	 * @return
	 */
	public Decision deepest() {
		return (deeper == null || deeper == this) ? this : deeper.deepest();
	}
}