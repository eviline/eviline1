package org.eviline.clj;

import org.eviline.Field;
import org.eviline.ShapeType;
import org.eviline.ai.AIKernel;
import org.eviline.ai.Context;
import org.eviline.ai.Decision;
import org.eviline.ai.DefaultAIKernel;
import org.eviline.ai.QueueContext;
import org.eviline.fitness.AbstractFitness;
import org.eviline.fitness.Fitness;

public class ClojureAIKernel extends DefaultAIKernel {

	public ClojureAIKernel(Fitness fitness) {
		throw new InternalError("This class should be in clojure");
	}
	
	@Override
	public Decision bestFor(QueueContext context) {
		throw new InternalError("This class should be in clojure");
	}

	@Override
	public Decision bestFor(Context context, ShapeType type) {
		throw new InternalError("This class should be in clojure");
	}

	@Override
	public Decision bestFor(Field inPlayField) {
		throw new InternalError("This class should be in clojure");
	}

	@Override
	public Decision bestFor(Context context) {
		throw new InternalError("This class should be in clojure");
	}

	@Override
	public Decision planBest(Context context, Decision defaultDecision) {
		throw new InternalError("This class should be in clojure");
	}

	@Override
	public Decision worstFor(Context context) {
		throw new InternalError("This class should be in clojure");
	}

	@Override
	public Decision planWorst(Context context, Decision defaultDecision) {
		throw new InternalError("This class should be in clojure");
	}

	@Override
	public AbstractFitness getFitness() {
		throw new InternalError("This class should be in clojure");
	}

	@Override
	public void setFitness(Fitness fitness) {
		throw new InternalError("This class should be in clojure");
	}
}
