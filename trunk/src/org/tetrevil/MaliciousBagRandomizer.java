package org.tetrevil;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.tetrevil.MaliciousRandomizer.Cache;
import org.tetrevil.MaliciousRandomizer.Score;

public class MaliciousBagRandomizer extends MaliciousRandomizer implements Randomizer {
	public static final int DEFAULT_DEPTH = 4;
	public static final int DEFAULT_DIST = 3;
	public static final int HISTORY_SIZE = 3;

//	public static class Score {
//		public double score;
//		public Shape shape;
//		public Field field = new Field();
//	}
	
	protected static Map<ShapeType, Double> WEIGHTS = new EnumMap<ShapeType, Double>(ShapeType.class);
	static {
		WEIGHTS.put(ShapeType.I, 1.1);
	}

	protected class Cache {
		public Score depestDecide = new Score();
		public Score[] worst = new Score[depth + 2];
		public Field[] f = new Field[depth + 2];
		public Field[] fc = new Field[depth + 2];
		public Score[] typeScore = new Score[depth + 2];
		public List<ShapeType>[] bag = new List[depth + 2];
		
		public Cache() {
			for(int i = 0; i < depth + 2; i++) {
				worst[i] = new Score();
				f[i] = new Field();
				fc[i] = new Field();
				typeScore[i] = new Score();
				bag[i] = new ArrayList<ShapeType>();
			}
		}
	}
	
	protected Cache cache;
	
	protected List<ShapeType> bag = new ArrayList<ShapeType>();

	public MaliciousBagRandomizer() {
		this(DEFAULT_DEPTH, DEFAULT_DIST);
	}
	
	public MaliciousBagRandomizer(int depth, int distribution) {
		this.depth = depth;
		this.distribution = distribution;
		this.cache = new Cache();
		for(ShapeType type : ShapeType.values()) {
			bag.add(type);
			for(int i = 1; i < distribution; i++)
				bag.add(type);
		}
	}
	
	@Override
	public String toString() {
		return "bag, d=" + depth +", rf=" + (int)(100 * rfactor) + "%, f=" + fair + ", ds=" + distribution;
	}
	
	@Override
	public Shape provideShape(Field field) {
		if(randomFirst) {
			randomFirst = false;
			Shape s = ShapeType.values()[(int)(Math.random() * ShapeType.values().length)].starter();
			bag.remove(s.type());
			return s;
		}
		if(this.bag.size() == 0) {
			for(ShapeType type : ShapeType.values()) {
				bag.add(type);
				for(int i = 1; i < distribution; i++)
					bag.add(type);
			}
		}
		cache.bag[0].clear(); cache.bag[0].addAll(this.bag);
		field = field.copyInto(new Field());
		Shape shape = decide(field, 0).shape;
		recent.add(shape.type());
		while(recent.size() > HISTORY_SIZE)
			recent.remove(0);
		this.bag.remove(shape.type());
		return shape;
	}

	@Override
	protected Score decide(Field field, int depth) {
		if(depth > this.depth) {
			Score score = cache.depestDecide;
			score.field = field;
			score.score = Fitness.score(field);
			return score;
		}
		
		return worstFor(field, depth);
	}

	@Override
	protected Score worstFor(Field field, int depth) {
		ShapeType omit = null;
		if(depth == 0 && recent.size() > 0) {
			omit = recent.get(0);
			for(ShapeType t : recent) {
				if(omit != t)
					omit = null;
			}
		}
		
		List<ShapeType> bag = cache.bag[depth];
		if(bag.size() == 0) {
			for(ShapeType type : ShapeType.values()) {
				bag.add(type);
				for(int i = 1; i < distribution; i++)
					bag.add(type);
			}
		}
		
		Score worst = cache.worst[depth];
		worst.score = Double.NEGATIVE_INFINITY;
		
		paintImpossibles(field);
		
		Field f = cache.f[depth];
		Field fc = cache.fc[depth];
		for(ShapeType type : ShapeType.values()) {
			if(!bag.contains(type))
				continue;
			Score typeScore = cache.typeScore[depth];
			typeScore.score = Double.POSITIVE_INFINITY;

			for(Shape shape : type.shapes()) {
				for(int x = 0; x < Field.WIDTH; x++) {
					field.copyInto(f);
					f.setShape(shape);
//					f.setShapeY(0);
					f.setShapeX(x);
//					for(int i = 0; i < Field.WIDTH / 2 + 1; i++)
//						f.shiftLeft();
//					for(int i = 0; i < x; i++)
//						f.shiftRight();
//					while(f.getShape() != null && !f.isGameOver())
//						f.clockTick();
//					double fscore = score(f);
//					if(fscore < typeScore.score) {
//						typeScore.score = fscore;
//						typeScore.field = f.copyInto(typeScore.field);
//						typeScore.shape = shape;
//					}
					for(int y = 0; y < Field.HEIGHT + Field.BUFFER; y++) {
						f.setShapeY(y);
						if(!shape.intersects(f.getField(), x, y) && f.isGrounded()) {
							f.copyInto(fc);
							fc.clockTick();
							double fscore = Fitness.score(fc);
							if(fscore < typeScore.score) {
								typeScore.score = fscore;
								typeScore.field = fc.copyInto(typeScore.field);
								typeScore.shape = shape;
							}
						}
					}
				}
			}
			cache.bag[depth+1].clear(); cache.bag[depth+1].addAll(bag); cache.bag[depth+1].remove(type);
			typeScore = decide(typeScore.field, depth + 1);
			typeScore.score *= 1 + rfactor - 2 * rfactor * Math.random();
			if(WEIGHTS.containsKey(type))
				typeScore.score *= WEIGHTS.get(type);
			typeScore.shape = type.shapes()[0];
			if(typeScore.score > worst.score && omit != typeScore.shape.type()) {
				worst.score = typeScore.score;
				worst.field = typeScore.field.copyInto(worst.field);
				worst.shape = typeScore.shape;
			}
		}
		return worst;
	}

	@Override
	public void setDepth(int depth) {
		super.setDepth(depth);
		cache = new Cache();
	}
}
