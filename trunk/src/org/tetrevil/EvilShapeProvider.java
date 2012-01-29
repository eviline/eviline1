package org.tetrevil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EvilShapeProvider implements ShapeProvider {
	public static final int DEFAULT_DEPTH = 2;
	public static final int HISTORY_SIZE = 3;
	
	public static double score(Field field) {
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
	
	public static class ScoredPath {
		public List<Shape> path = new ArrayList<Shape>();
		public double score;
		
		public ScoredPath(double score) {
			this.score = score;
		}
		
		public ScoredPath(double score, List<Shape> path) {
			this.path.addAll(path);
			this.score = score;
		}
		
		@Override
		public String toString() {
			return String.valueOf(score) + String.valueOf(path);
		}
	}
	
	protected Field[] stack;
	protected ScoredPath[] bestPath;
	protected ScoredPath[] typeBestPath;
	protected ScoredPath[] testPath;
	
	protected List<ShapeType> recent = new ArrayList<ShapeType>();
	
	public EvilShapeProvider() {
		this(DEFAULT_DEPTH);
	}
	
	public EvilShapeProvider(int depth) {
		stack = new Field[depth];
		bestPath = new ScoredPath[depth];
		typeBestPath = new ScoredPath[depth];
		testPath = new ScoredPath[depth];
		for(int i = 0; i < depth; i++) {
			stack[i] = new Field();
			bestPath[i] = new ScoredPath(0);
			typeBestPath[i] = new ScoredPath(0);
			testPath[i] = new ScoredPath(0);
		}
	}
	
	@Override
	public Shape provideShape(Field field) {
		ScoredPath dest = new ScoredPath(0);
		decide(field, 0, dest);
		recent.add(dest.path.get(0).type());
		while(recent.size() > HISTORY_SIZE)
			recent.remove(0);
		return dest.path.get(0);
	}
	
	protected void decide(Field field, int depth, ScoredPath dest) {
		ShapeType omit = null;
		if(recent.size() > 0) {
			omit = recent.get(0);
			for(ShapeType t : recent) {
				if(omit != t)
					omit = null;
			}
		}
		if(depth == stack.length) {
			dest.score = score(field) * (1.25 - Math.random() / 2);
			dest.path = Collections.emptyList();
			return;
		}
		bestPath[depth].score = Double.NEGATIVE_INFINITY;
		for(ShapeType type : ShapeType.values()) {
			typeBestPath[depth].score = Double.POSITIVE_INFINITY;
			for(Shape shape : type.shapes()) {
				for(int x = 0; x < Field.WIDTH; x++) {
					Field f = field.copyInto(stack[depth]);
					f.setShape(shape);
					f.setShapeX(Field.WIDTH / 2 + 1);
					f.setShapeY(0);
					for(int i = 0; i < Field.WIDTH; i++)
						f.shiftLeft();
					for(int i = 0; i < x; i++)
						f.shiftRight();
					while(f.getShape() != null && !f.isGameOver())
						f.clockTick();
					decideNice(f, depth+1, testPath[depth]);
					if(testPath[depth].score < typeBestPath[depth].score) {
						typeBestPath[depth].score = testPath[depth].score;
						typeBestPath[depth].path.clear();
						typeBestPath[depth].path.add(shape);
						typeBestPath[depth].path.addAll(testPath[depth].path);
					}
				}
			}
			double score = typeBestPath[depth].score * (1.25 - Math.random() / 2);
			if(bestPath[depth].score < score && omit != type) {
				bestPath[depth].score = typeBestPath[depth].score;
				bestPath[depth].path.clear();
				bestPath[depth].path.addAll(typeBestPath[depth].path);
			}
		}
		dest.score = bestPath[depth].score;
		dest.path = new ArrayList<Shape>(bestPath[depth].path);
	}

	protected void decideNice(Field field, int depth, ScoredPath dest) {
		if(depth == stack.length) {
			dest.score = score(field);
			dest.path = Collections.emptyList();
			return;
		}
		
		int tallest = 0;
		for(;;tallest++) {
			boolean found = false;
			for(int x = Field.BUFFER; x < Field.WIDTH + Field.BUFFER; x++) {
				if(field.getBlock(x, tallest) != null)
					found = true;
			}
			if(found)
				break;
		}
		
		double parentScore = score(field);
		bestPath[depth].score = Double.POSITIVE_INFINITY;
		for(ShapeType type : ShapeType.values()) {
			typeBestPath[depth].score = Double.POSITIVE_INFINITY;
			for(Shape shape : type.shapes()) {
				for(int x = 0; x < Field.WIDTH; x++) {
					Field f = field.copyInto(stack[depth]);
					f.setShape(shape);
					f.setShapeX(Field.WIDTH / 2 + 1);
					f.setShapeY(Math.max(0, tallest - 4));
					for(int i = 0; i < Field.WIDTH; i++)
						f.shiftLeft();
					for(int i = 0; i < x; i++)
						f.shiftRight();
					while(f.getShape() != null && !f.isGameOver())
						f.clockTick();
					double fscore = score(f);
					if(fscore < parentScore) {
						dest.score = fscore;
						dest.path.clear();
						dest.path.add(shape);
						return;
					}
					decideNice(f, depth+1, testPath[depth]);
					if(testPath[depth].score < typeBestPath[depth].score) {
						typeBestPath[depth].score = testPath[depth].score;
						typeBestPath[depth].path.clear();
						typeBestPath[depth].path.add(shape);
						typeBestPath[depth].path.addAll(testPath[depth].path);
					}
				}
			}
			if(bestPath[depth].score > typeBestPath[depth].score) {
				bestPath[depth].score = typeBestPath[depth].score;
				bestPath[depth].path.clear();
				bestPath[depth].path.addAll(typeBestPath[depth].path);
			}
		}
		dest.score = bestPath[depth].score;
		dest.path.clear();
		dest.path.addAll(bestPath[depth].path);
	}

}
