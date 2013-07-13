package org.eviline.ai;

import org.eviline.Field;
import org.eviline.ShapeType;
import org.eviline.fitness.AbstractFitness;
import org.eviline.fitness.FitnessUtils;

/**
 * A decision context.  Contains the original {@link Field}, a copy
 * of the field with impossible areas marked, and some book-keeping info.
 * There is one {@link Context} at each node in the decision tree.
 * @author robin
 *
 */
public class Context {
	public AIKernel ai;
	/**
	 * The decision modifier in use for this context.
	 */
	public DecisionModifier decisionModifier;
	/**
	 * The original {@link Field} based on which a decision is to be made
	 */
	public Field original;
	/**
	 * The original {@link Field} (see {@link #original}) but painted impossible.
	 * (see {@link AbstractFitness#paintImpossibles(Field)})
	 */
	public Field paintedImpossible;
	/**
	 * The remaining decision depth at this context in the decision tree.
	 */
	public int remainingDepth;
	/**
	 * The {@link ShapeType} to omit from the decision.
	 */
	public ShapeType omit;
	
	/**
	 * Create a new {@link Context}.
	 * @param decisionModifier
	 * @param original
	 * @param remainingDepth
	 */
	public Context(AIKernel ai, DecisionModifier decisionModifier, Field original, int remainingDepth) {
		this.ai = ai;
		this.decisionModifier = decisionModifier;
		this.original = original.clone();
		this.paintedImpossible = FitnessUtils.paintedImpossible(original);
		this.remainingDepth = remainingDepth;
	}
	
	/**
	 * Construct a {@link Context} that is a copy of this one, but for use at one level
	 * deeper in the decision tree.
	 * @param deeperOriginal
	 * @return
	 */
	public Context deeper(Field deeperOriginal) {
		return new Context(ai, decisionModifier, deeperOriginal, remainingDepth - 1);
	}
	
	@Override
	public String toString() {
		return String.valueOf(original);
	}
}