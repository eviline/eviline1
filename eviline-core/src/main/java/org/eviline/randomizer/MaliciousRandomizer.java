package org.eviline.randomizer;

import static org.eviline.randomizer.RandomizerFactory.DEPTH;
import static org.eviline.randomizer.RandomizerFactory.DISTRIBUTION;
import static org.eviline.randomizer.RandomizerFactory.FAIR;
import static org.eviline.randomizer.RandomizerFactory.RFACTOR;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.eviline.ExtendedPropertySource;
import org.eviline.Field;
import org.eviline.PropertySource;
import org.eviline.Shape;
import org.eviline.ShapeType;
import org.eviline.ai.AI;
import org.eviline.ai.Context;
import org.eviline.ai.Decision;
import org.eviline.ai.DecisionModifier;

/**
 * {@link Randomizer} that looks a few moves ahead in the future to come up with the worst
 * {@link Shape} to return.
 * @author robin
 *
 */
public class MaliciousRandomizer implements Randomizer, Serializable {
	public static final int HISTORY_SIZE = 3;

	public static class MaliciousRandomizerProperties extends ExtendedPropertySource {
		public MaliciousRandomizerProperties() {
		}
		
		public MaliciousRandomizerProperties(PropertySource p) {
			super(p);
		}
		
		public MaliciousRandomizerProperties(int depth, double rfactor, boolean fair, int distribution) {
			depth(depth);
			rfactor(rfactor);
			fair(fair);
			distribution(distribution);
		}
		
		public int depth() {
			return get(DEPTH) == null ? 3 : getInt(DEPTH);
		}
		
		public void depth(int depth) {
			putInt(DEPTH, depth);
		}
		
		public double rfactor() {
			return get(RFACTOR) == null ? 0.05 : getDouble(RFACTOR);
		}
		
		public void rfactor(double rfactor) {
			putDouble(RFACTOR, rfactor);
		}
		
		public boolean fair() {
			return get(FAIR) == null ? true : getBoolean(FAIR);
		}
		
		public void fair(boolean fair) {
			putBoolean(FAIR, fair);
		}
		
		public int distribution() {
			return get(DISTRIBUTION) == null ? 30 : getInt(DISTRIBUTION);
		}
		
		public void distribution(int distribution) {
			putInt(DISTRIBUTION, distribution);
		}
	}
	
	protected MaliciousRandomizerProperties config;
	
	public int depth() { return config.depth(); }
	public double rfactor() { return config.rfactor(); }
	public boolean fair() { return config.fair(); }
	public int distribution() { return config.distribution(); }
	
	protected boolean randomFirst = true;
	
	protected List<ShapeType> recent = new ArrayList<ShapeType>();
	protected int[] typeCounts = new int[ShapeType.values().length];
	protected List<ShapeType> history = new ArrayList<ShapeType>();
	protected int distAdjustment = 0;
	
	protected Random random = new Random();
	
	protected transient String taunt = "";
	
	public MaliciousRandomizer(PropertySource p) {
		config = new MaliciousRandomizerProperties(p);
		for(int i = 0; i < typeCounts.length; i++) {
			typeCounts[i] = distribution();
		}
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
		history.add(decision.type);
		while(recent.size() > HISTORY_SIZE)
			recent.remove(0);
		typeCounts[decision.type.ordinal()]++;
//		typeCounts[(int)(typeCounts.length * random.nextDouble())]--;
		if(history.size() > config.distribution() * typeCounts.length) {
			ShapeType hdrop = history.remove(0);
			typeCounts[hdrop.ordinal()]--;
		}
		return shape;
	}
	
	@Override
	public String getTaunt() {
		return taunt;
	}
	
	@Override
	public List<ShapeType> getNext() {
		String taunt = getTaunt();
		if(taunt == null || taunt.length() < 2)
			return new ArrayList<ShapeType>();
		List<ShapeType> ret = new ArrayList<ShapeType>();
		for(int i = 1; i < taunt.length(); i++) {
			char c = taunt.charAt(i);
			switch(c) {
			case 'O': ret.add(ShapeType.O); break;
			case 'T': ret.add(ShapeType.T); break;
			case 'J': ret.add(ShapeType.J); break;
			case 'L': ret.add(ShapeType.L); break;
			case 'S': ret.add(ShapeType.S); break;
			case 'Z': ret.add(ShapeType.Z); break;
			case 'I': ret.add(ShapeType.I); break;
			}
		}
		return ret;
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
		Context context = new Context(AI.getInstance(), decisionModifier, field, this.depth() - depth);
		context.omit = omit;
		Decision defaultDecision = new Decision();
		defaultDecision.field = field.clone();
		defaultDecision.score = AI.getInstance().getFitness().score(field, defaultDecision.field);
		Decision decision = AI.getInstance().planWorst(context, defaultDecision);
		
		return decision;
	}
	
	protected void permuteDecision(Decision typeDecision) {
		if(typeDecision.score == Double.POSITIVE_INFINITY || typeDecision.score == Double.NEGATIVE_INFINITY)
			return;
		typeDecision.score *= 1 + rfactor() - 2 * rfactor() * random.nextDouble();
		if(fair()) {
			double overuse = typeCounts[typeDecision.type.ordinal()] / (double) distribution() - 1;
			overuse /= 3;
			typeDecision.score -= Math.abs(typeDecision.score) * overuse;
		}
		if(typeDecision.type == ShapeType.O) {
			typeDecision.score -= 0.2 * Math.abs(typeDecision.score);
		}
	}
	

	public Random getRandom() {
		return random;
	}

	public void setRandom(Random random) {
		this.random = random;
	}
	
	@Override
	public PropertySource config() {
		return config;
	}
	
	@Override
	public String name() {
		return getClass().getName();
	}

	@Override
	public Randomizer clone() {
		try {
			return (Randomizer) super.clone();
		} catch(CloneNotSupportedException cnse) {
			throw new InternalError("clone not supported???");
		}
	}
}
