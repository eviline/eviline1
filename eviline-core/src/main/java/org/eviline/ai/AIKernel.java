package org.eviline.ai;

import java.util.Map;

import org.eviline.Field;
import org.eviline.PlayerActionNode;
import org.eviline.ShapeType;
import org.eviline.fitness.Fitness;

public interface AIKernel {
	/**
	 * Determine the best way for a player to play a particular queue of shapes.
	 * @param context
	 * @return
	 */
	public Decision bestFor(final QueueContext context);

	/**
	 * Determine the best way for a player to play a single shape.
	 * @param context
	 * @param type
	 * @return
	 */
	public Decision bestFor(Context context, ShapeType type);

	public Decision bestFor(Field inPlayField);

	/**
	 * Determine the shape that would be best for the player
	 * @param context
	 * @return
	 */
	public Decision bestFor(Context context);

	/**
	 * Determine the shape that would be best for the player
	 * @param context
	 * @param defaultDecision
	 * @return
	 */
	public Decision planBest(Context context, Decision defaultDecision);

	/**
	 * Determine the shape that would be worst for the player
	 * @param context
	 * @return
	 */
	public Decision worstFor(Context context);

	/**
	 * Determine the shape that would be worst for the player
	 * @param context
	 * @param defaultDecision
	 * @return
	 */
	public Decision planWorst(Context context, Decision defaultDecision);

	public boolean isHighGravity();

	public void setHighGravity(boolean highGravity);

	public boolean isHardDropOnly();

	public void setHardDropOnly(boolean hardDropOnly);

	public Fitness getFitness();

	public void setFitness(Fitness fitness);

}