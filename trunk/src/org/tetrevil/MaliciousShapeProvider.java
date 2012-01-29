package org.tetrevil;

import java.util.ArrayList;
import java.util.List;

public class MaliciousShapeProvider implements ShapeProvider {
	public static final int DEFAULT_DEPTH = 4;
	public static final int HISTORY_SIZE = 3;

	public static double score(Field field) {
		if(field.isGameOver())
			return Double.POSITIVE_INFINITY;
		double score = 0;
		for(int x = Field.BUFFER; x < Field.WIDTH + Field.BUFFER; x++) {
			int bonus = 0;
			for(int y = Field.HEIGHT - 1; y >= 0; y--) {
				if(field.getBlock(x, y) != null)
					score += Field.HEIGHT + Field.BUFFER - y + bonus * 10;
				else
					bonus++;
			}
		}
		return score;
	}

	public static class Score {
		public double score;
		public Shape shape;
		public Field field;
	}

	protected int depth;
	
	protected List<ShapeType> recent = new ArrayList<ShapeType>();

	public MaliciousShapeProvider() {
		this(DEFAULT_DEPTH);
	}
	
	public MaliciousShapeProvider(int depth) {
		this.depth = depth;
	}
	
	@Override
	public Shape provideShape(Field field) {
		Shape shape = decide(field, 0).shape;
		recent.add(shape.type());
		while(recent.size() > HISTORY_SIZE)
			recent.remove(0);
		return shape;
	}
	
	protected Score decide(Field field, int depth) {
		if(depth > this.depth) {
			Score score = new Score();
			score.field = field;
			score.score = score(field);
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

		Score worst = new Score();
		worst.score = Double.NEGATIVE_INFINITY;
		Field f = new Field();
		for(ShapeType type : ShapeType.values()) {
			Score typeScore = new Score();
			typeScore.score = Double.POSITIVE_INFINITY;
			for(Shape shape : type.shapes()) {
				for(int x = 0; x < Field.WIDTH; x++) {
					field.copyInto(f);
					f.setShape(shape);
					f.setShapeY(0);
					f.setShapeX(Field.WIDTH / 2 + 1);
					for(int i = 0; i < Field.WIDTH / 2 + 1; i++)
						f.shiftLeft();
					for(int i = 0; i < x; i++)
						f.shiftRight();
					while(f.getShape() != null && !f.isGameOver())
						f.clockTick();
					double fscore = score(f);
					if(fscore < typeScore.score) {
						typeScore.score = fscore;
						typeScore.field = f.copyInto(new Field());
						typeScore.shape = shape;
					}
				}
			}
			typeScore = decide(typeScore.field, depth + 1);
			typeScore.score *= 1.25 - Math.random() / 2;
			typeScore.shape = type.shapes()[0];
			if(typeScore.score > worst.score && omit != typeScore.shape.type()) {
				worst.score = typeScore.score;
				worst.field = typeScore.field;
				worst.shape = typeScore.shape;
			}
		}
		return worst;
	}
	
}
