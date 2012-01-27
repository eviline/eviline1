package org.tetrevil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EvilShapeProvider implements ShapeProvider {
	public static final int DEFAULT_DEPTH = 5;
	
	protected static double score(Field field) {
		double score = 0;
		for(int y = 0; y < Field.HEIGHT; y++) {
			for(int x = Field.BUFFER; x < Field.WIDTH + Field.BUFFER; x++) {
				score += Field.HEIGHT + Field.BUFFER - y;
			}
		}
		return score;
	}
	
	protected class ScoredPath {
		public List<Shape> path = new ArrayList<Shape>();
		public double score;
		
		public ScoredPath(double score) {
			this.score = score;
		}
		
		public ScoredPath(double score, List<Shape> path) {
			this.path.addAll(path);
			this.score = score;
		}
	}
	
	protected Field[] stack;
	protected ScoredPath[] bestPath;
	protected ScoredPath[] typeBestPath;
	protected ScoredPath[] testPath;
	
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
		return dest.path.get(0);
	}
	
	protected void decide(Field field, int depth, ScoredPath dest) {
		if(depth == stack.length) {
			dest.score = score(field);
			dest.path = Collections.emptyList();
			return;
		}
		bestPath[depth].score = Double.NEGATIVE_INFINITY;
		for(ShapeType type : ShapeType.values()) {
			typeBestPath[depth].score = Double.NEGATIVE_INFINITY;
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
					decide(f, depth+1, testPath[depth]);
					if(testPath[depth].score < typeBestPath[depth].score) {
						typeBestPath[depth].score = testPath[depth].score;
						typeBestPath[depth].path.clear();
						typeBestPath[depth].path.add(shape);
						typeBestPath[depth].path.addAll(testPath[depth].path);
					}
				}
			}
			if(bestPath[depth].score < typeBestPath[depth].score) {
				bestPath[depth].score = typeBestPath[depth].score;
				bestPath[depth].path.clear();
				bestPath[depth].path.addAll(typeBestPath[depth].path);
			}
		}
		dest.score = bestPath[depth].score;
		dest.path = new ArrayList<Shape>(bestPath[depth].path);
	}
}
