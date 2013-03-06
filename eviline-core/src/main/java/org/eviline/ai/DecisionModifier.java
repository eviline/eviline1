package org.eviline.ai;


/**
 * Interface for objects which wish to modify the decision reached by an iteration
 * of an AI algorithm.  Modifications include but are not limited to adjusting the
 * score attached to a decision based on other factors, such as the recent frequency
 * of shapes.
 * @author robin
 *
 */
public interface DecisionModifier {
	/**
	 * Modify the planned decision.
	 * @param context The context in which the decision was made
	 * @param decision The decision that was made.
	 */
	public void modifyPlannedDecision(Context context, Decision decision);
}