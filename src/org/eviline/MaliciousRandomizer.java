package org.eviline;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.eviline.AIKernel.Context;
import org.eviline.AIKernel.Decision;
import org.eviline.AIKernel.DecisionModifier;
import org.eviline.event.TetrevilAdapter;
import org.eviline.event.TetrevilEvent;
import org.eviline.event.TetrevilListener;

/**
 * {@link Randomizer} that looks a few moves ahead in the future to come up with the worst
 * {@link Shape} to return.
 * @author robin
 *
 */
public class MaliciousRandomizer implements Randomizer, Serializable {
	public static final int DEFAULT_DEPTH = 3;
	public static final int DEFAULT_DIST = 30;
	public static final int HISTORY_SIZE = 3;

	protected int depth = 3;
	protected double rfactor = 0.05;
	protected boolean fair = false;
	protected int distribution = 100;
	protected boolean adaptive = false;
	
	protected boolean randomFirst = true;
	
	protected List<ShapeType> recent = new ArrayList<ShapeType>();
	protected int[] typeCounts = new int[ShapeType.values().length];
	protected int distAdjustment = 0;
	
	protected Random random = new Random();
	
	protected transient String taunt = "";
	
	protected TetrevilListener adaptiveListener = new TetrevilAdapter() {
		@Override
		public void linesCleared(TetrevilEvent e) {
			adjustDistribution(e.getLines());
		}
	};

	public MaliciousRandomizer() {
		this(DEFAULT_DEPTH, DEFAULT_DIST);
	}
	
	public MaliciousRandomizer(int depth, int distribution) {
		this.depth = depth;
		this.distribution = distribution;
		for(int i = 0; i < typeCounts.length; i++) {
			typeCounts[i] = distribution;
		}
	}
	
	@Override
	public String getRandomizerName() {
		return getClass().getName();
	}
	
	@Override
	public String toString() {
		return "mal, d=" + depth +", rf=" + (int)(100 * rfactor) + "%, f=" + fair + ", ds=" + distribution;
	}
	
	@Override
	public Shape provideShape(Field field) {
		if(randomFirst) {
			randomFirst = false;
			ShapeType type;
			do {
				type = ShapeType.values()[(int)(random.nextDouble() * ShapeType.values().length)];
			} while(type == ShapeType.S || type == ShapeType.Z);
			return type.starter();
		}
		field = field.copyInto(new Field());
		Decision decision = decide(field, "", 0);
		Shape shape = decision.type.starter();
//		taunt = score.taunt;
		recent.add(decision.type);
		while(recent.size() > HISTORY_SIZE)
			recent.remove(0);
		typeCounts[decision.type.ordinal()]++;
		typeCounts[(int)(typeCounts.length * random.nextDouble())]--;
		return shape;
	}
	
	@Override
	public String getTaunt() {
		return taunt;
	}
	
	protected Decision decide(Field field, String taunt, int depth) {
		return worstFor(field, taunt, depth);
	}
	
	protected Decision worstFor(Field field, String taunt, int depth) {
		ShapeType omit = null;
		if(depth == 0 && recent.size() > 0) {
			omit = recent.get(0);
			for(ShapeType t : recent) {
				if(omit != t)
					omit = null;
			}
		}
		final ShapeType fomit = omit;
		
		DecisionModifier decisionModifier = new DecisionModifier() {
			@Override
			public void modifyPlannedDecision(Context context, Decision decision) {
				permuteDecision(decision);
			}
		};
		Context context = new Context(decisionModifier, field, this.depth - depth);
		context.omit = omit;
		Decision defaultDecision = new Decision();
		defaultDecision.field = field.copy();
		defaultDecision.score = Fitness.scoreWithPaint(defaultDecision.field);
		Decision decision = AIKernel.getInstance().planWorst(context, defaultDecision);
		
		return decision;
	}
	
	protected void permuteDecision(Decision typeDecision) {
		if(typeDecision.score == Double.POSITIVE_INFINITY || typeDecision.score == Double.NEGATIVE_INFINITY)
			return;
		typeDecision.score *= 1 + rfactor - 2 * rfactor * random.nextDouble();
		if(fair)
			typeDecision.score *= (distribution + distAdjustment) / (double) typeCounts[typeDecision.type.ordinal()];
		if(typeDecision.type == ShapeType.O) {
			typeDecision.score -= 0.2 * Math.abs(typeDecision.score);
		}
	}
	

	public double getRfactor() {
		return rfactor;
	}

	public void setRfactor(double rfactor) {
		this.rfactor = rfactor;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
//		cache = new Cache();
	}

	public boolean isFair() {
		return fair;
	}

	public void setFair(boolean fair) {
		this.fair = fair;
	}

	public int getDistribution() {
		return distribution;
	}

	public boolean isAdaptive() {
		return adaptive;
	}

	public void setAdaptive(Field field, boolean adaptive) {
		if(this.adaptive != adaptive) {
			if(adaptive)
				field.addTetrevilListener(adaptiveListener);
			else
				field.removeTetrevilListener(adaptiveListener);
		}
		this.adaptive = adaptive;
	}
	
	public void adjustDistribution(int adjustment) {
		for(int i = 0; i < typeCounts.length; i++) {
			typeCounts[i] += adjustment;
		}
		distAdjustment += adjustment;
	}
	
	@Override
	public MaliciousRandomizer getMaliciousRandomizer() {
		return this;
	}

	public Random getRandom() {
		return random;
	}

	public void setRandom(Random random) {
		this.random = random;
	}
}
