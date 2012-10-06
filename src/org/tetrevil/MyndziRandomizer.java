package org.tetrevil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyndziRandomizer extends MaliciousBagRandomizer {
	public MyndziRandomizer() {
		super();
		WEIGHTS.clear();
	}

	public MyndziRandomizer(int depth, int distribution) {
		super(depth, distribution);
		WEIGHTS.clear();
	}
	
	@Override
	public Shape provideShape(Field field) {
		Shape shape = super.provideShape(field);
		
//		taunt = shape.type().toString();
//		for(int d = 0; d < bag.size() + nextBag.size() && d < depth; d++) {
//			if(d < bag.size())
//				taunt += bag.get(d);
//			else
//				taunt += nextBag.get(d - bag.size());
//		}
		
		return shape;
	}

	@Override
	protected Score worstFor(Field field, String taunt, int depth) {
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
			Collections.shuffle(bag);
		}
		
		Score worst = cache.worst[depth];
		worst.score = Double.NEGATIVE_INFINITY;
		
		List<Score> sorted = null;
		if(depth == 0)
			sorted = new ArrayList<Score>();
		
		Fitness.paintImpossibles(field);
		
		Field f = cache.f[depth];
		Field fc = cache.fc[depth];
		for(ShapeType type : ShapeType.values()) {
			if(!bag.contains(type))
				continue;
			Score typeScore = cache.typeScore[depth];
			typeScore.score = Double.POSITIVE_INFINITY;
			typeScore.taunt = taunt + type;

			for(Shape shape : type.orientations()) {
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
					boolean grounded = !shape.intersects(f.getField(), x, 0);
					for(int y = 0; y < Field.HEIGHT + Field.BUFFER; y++) {
						f.setShapeY(y);
						boolean groundedAbove = grounded;
						grounded = f.isGrounded();
						if(!groundedAbove && grounded) {
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
			if(depth < this.depth)
				typeScore = decide(typeScore.field, typeScore.taunt, depth + 1);
//			typeScore.score *= 1 + rfactor - 2 * rfactor * random.nextDouble();
//			if(WEIGHTS.containsKey(type))
//				typeScore.score *= WEIGHTS.get(type);
			permuteScore(typeScore);
			typeScore.shape = type.orientations()[0];
			if(typeScore.score > worst.score && omit != typeScore.shape.type()) {
				worst.score = typeScore.score;
				worst.field = typeScore.field.copyInto(worst.field);
				worst.shape = typeScore.shape;
				worst.taunt = typeScore.taunt;
			}
			if(depth == 0) {
				Score sortable = new Score();
				sortable.shape = typeScore.shape;
				sortable.score = typeScore.score;
				sortable.field = typeScore.field.copyInto(new Field());
				sorted.add(sortable);
			}
		}
		if(depth == 0) {
			Collections.sort(sorted);
			if(sorted.size() >= 3) {
				double delta1 = sorted.get(0).score - sorted.get(1).score;
				double delta2 = sorted.get(1).score - sorted.get(2).score;
				if(delta1 > 2*delta2) { // The worst is significantly the worst, use it
					return worst;
				} else { // Pick a shape at random from the bag
//					Score random = sorted.get((int)(Math.random() * sorted.size()));
					Score next = null;
					for(Score s : sorted) {
						if(this.bag.get(0) == s.shape.type())
							next = s;
					}
					Collections.reverse(sorted);
					if(next == sorted.get(0)) { // We grabbed the best!
						delta1 = sorted.get(1).score - sorted.get(0).score;
						delta2 = sorted.get(2).score - sorted.get(1).score;
						if(delta1 > 3*delta2) { // Was the best significantly the best?
							for(Score s : sorted) {
								if(this.bag.get(1) == s.shape.type())
									next = s;
							}
						}
					}
					return next; // Return the selected shape
				}
			}
		}
		return worst;
	}

	@Override
	public String getRandomizerName() {
		return getClass().getName();
	}
}
