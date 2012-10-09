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

	public static class Score implements Serializable, Comparable<Score> {
		public double score;
		public Shape shape;
		public String taunt;
		public Field field = new Field();
		
		public Score() {}
		
		public Score(Decision d) {
			score = d.score;
			shape = d.type.starter();
			field = d.field;
			taunt = d.taunt();
		}
		
		@Override
		public int compareTo(Score o) {
			return -((Double) score).compareTo(o.score);
		}
	}

//	protected class Cache implements Serializable {
//		public Score depestDecide = new Score();
//		public Score[] worst = new Score[depth + 1];
//		public Field[] f = new Field[depth + 1];
//		public Field[] fc = new Field[depth + 1];
//		public Score[] typeScore = new Score[depth + 1];
//		
//		public Cache() {
//			for(int i = 0; i < depth + 1; i++) {
//				worst[i] = new Score();
//				f[i] = new Field();
//				fc[i] = new Field();
//				typeScore[i] = new Score();
//			}
//		}
//	}
	
//	protected Cache cache;
	
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
//		this.cache = new Cache();
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
		Score score = decide(field, "", 0);
		Shape shape = score.shape;
//		taunt = score.taunt;
		recent.add(shape.type());
		while(recent.size() > HISTORY_SIZE)
			recent.remove(0);
		typeCounts[shape.type().ordinal()]++;
		typeCounts[(int)(typeCounts.length * random.nextDouble())]--;
		return shape;
	}
	
	@Override
	public String getTaunt() {
		return taunt;
	}
	
	protected Score decide(Field field, String taunt, int depth) {
		return worstFor(field, taunt, depth);
	}
	
	protected Score worstFor(Field field, String taunt, int depth) {
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
				if(decision.type == fomit) {
					decision.score = Double.NEGATIVE_INFINITY;
					return;
				}
				Score s = new Score(decision);
				permuteScore(s);
				decision.score = s.score;
			}
		};
		Context context = new Context(decisionModifier, field, this.depth - depth);
		context.omit = omit;
		Decision defaultDecision = new Decision();
		defaultDecision.field = field.copy();
		defaultDecision.score = Fitness.scoreWithPaint(defaultDecision.field);
		Decision decision = AIKernel.getInstance().planWorst(context, defaultDecision);
		
		return new Score(decision);
	}
	
	protected void permuteScore(Score typeScore) {
		if(typeScore.score == Double.POSITIVE_INFINITY || typeScore.score == Double.NEGATIVE_INFINITY)
			return;
		typeScore.score *= 1 + rfactor - 2 * rfactor * random.nextDouble();
		if(fair)
			typeScore.score *= (distribution + distAdjustment) / (double) typeCounts[typeScore.shape.type().ordinal()];
		if(typeScore.shape.type() == ShapeType.O) {
			typeScore.score -= 0.2 * Math.abs(typeScore.score);
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
