package org.tetrevil;

import java.util.ArrayList;
import java.util.List;

public class MaliciousRandomizer implements Randomizer {
	public static final int DEFAULT_DEPTH = 3;
	public static final int DEFAULT_DIST = 30;
	public static final int HISTORY_SIZE = 3;

	public static class Score {
		public double score;
		public Shape shape;
		public Field field = new Field();
	}

	protected class Cache {
		public Score depestDecide = new Score();
		public Score[] worst = new Score[depth + 1];
		public Field[] f = new Field[depth + 1];
		public Field[] fc = new Field[depth + 1];
		public Score[] typeScore = new Score[depth + 1];
		
		public Cache() {
			for(int i = 0; i < depth + 1; i++) {
				worst[i] = new Score();
				f[i] = new Field();
				fc[i] = new Field();
				typeScore[i] = new Score();
			}
		}
	}
	
	protected Cache cache;
	
	protected int depth = 3;
	protected double rfactor = 0.05;
	protected boolean fair = true;
	protected int distribution = 100;
	
	protected boolean randomFirst = true;
	
	protected List<ShapeType> recent = new ArrayList<ShapeType>();
	protected int[] typeCounts = new int[ShapeType.values().length];
	protected int totalCount = 0;

	public MaliciousRandomizer() {
		this(DEFAULT_DEPTH, DEFAULT_DIST);
	}
	
	public MaliciousRandomizer(int depth, int distribution) {
		this.depth = depth;
		this.distribution = distribution;
		this.cache = new Cache();
		for(int i = 0; i < typeCounts.length; i++) {
			totalCount += (typeCounts[i] = distribution);
		}
	}
	
	@Override
	public String toString() {
		return "d=" + depth +", rf=" + (int)(100 * rfactor) + "%, f=" + fair + ", ds=" + distribution;
	}
	
	@Override
	public Shape provideShape(Field field) {
		if(randomFirst) {
			randomFirst = false;
			return ShapeType.values()[(int)(Math.random() * ShapeType.values().length)].starter();
		}
		field = field.copyInto(new Field());
		Shape shape = decide(field, 0).shape;
		recent.add(shape.type());
		while(recent.size() > HISTORY_SIZE)
			recent.remove(0);
		typeCounts[shape.type().ordinal()]++;
		typeCounts[(int)(typeCounts.length * Math.random())]--;
		return shape;
	}
	
	protected Score decide(Field field, int depth) {
		if(depth > this.depth) {
			Score score = cache.depestDecide;
			score.field = field;
			score.score = Fitness.score(field);
			return score;
		}
		
		return worstFor(field, depth);
	}
	
	protected Score worstFor(Field field, int depth) {
		ShapeType omit = null;
		if(depth == 0 && recent.size() > 0) {
			omit = recent.get(0);
			for(ShapeType t : recent) {
				if(omit != t)
					omit = null;
			}
		}

		Score worst = cache.worst[depth];
		worst.score = Double.NEGATIVE_INFINITY;
		
		paintImpossibles(field);
		
		Field f = cache.f[depth];
		Field fc = cache.fc[depth];
		for(ShapeType type : ShapeType.values()) {
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
			typeScore = decide(typeScore.field, depth + 1);
			typeScore.score *= 1 + rfactor - 2 * rfactor * Math.random();
			if(fair)
				typeScore.score *= distribution / (double) typeCounts[type.ordinal()];
			typeScore.shape = type.shapes()[0];
			if(typeScore.score > worst.score && omit != typeScore.shape.type()) {
				worst.score = typeScore.score;
				worst.field = typeScore.field.copyInto(worst.field);
				worst.shape = typeScore.shape;
			}
		}
		return worst;
	}
	
	protected void paintImpossibles(Field field) {
		Block[][] f = field.getField();
		for(int y = 1; y < Field.BUFFER + Field.HEIGHT; y++) {
			for(int x = Field.BUFFER; x < Field.BUFFER + Field.WIDTH; x++) {
				if(f[y][x] == null)
					f[y][x] = Block.X;
			}
		}
		for(int y = 1; y < Field.BUFFER + Field.HEIGHT; y++) {
			for(int x = Field.BUFFER; x < Field.BUFFER + Field.WIDTH; x++) {
				if(f[y-1][x] == null || f[y][x-1] == null || f[y][x+1] == null && f[y][x] == Block.X)
					f[y][x] = null;
			}
			for(int x = Field.BUFFER + Field.WIDTH - 1; x >= Field.BUFFER; x--) {
				if(f[y-1][x] == null || f[y][x-1] == null || f[y][x+1] == null && f[y][x] == Block.X)
					f[y][x] = null;
			}
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
		cache = new Cache();
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
	
}
