package org.tetrevil;

public class BipolarRandomizer extends MaliciousRandomizer {
	protected AngelRandomizer angelic;
	protected ThreadedMaliciousRandomizer evil;
	
	public BipolarRandomizer() {
		super();
		angelic = new AngelRandomizer();
		evil = new ThreadedMaliciousRandomizer();
	}
	public BipolarRandomizer(int depth, int distribution) {
		super(depth, distribution);
		angelic = new AngelRandomizer(depth, distribution);
		evil = new ThreadedMaliciousRandomizer(depth, distribution);
	}
	
	@Override
	public Shape provideShape(Field field) {
		int level = field.getLines() / 10;
		if((level % 3) == 0)
			return angelic.provideShape(field);
		else
			return evil.provideShape(field);
	}
	
	public void setRfactor(double rfactor) {
		angelic.setRfactor(rfactor);
		evil.setRfactor(rfactor);
	}
	public void setDepth(int depth) {
		angelic.setDepth(depth);
		evil.setDepth(depth);
	}
	public void setFair(boolean fair) {
		angelic.setFair(fair);
		evil.setFair(fair);
	}
	public void setAdaptive(Field field, boolean adaptive) {
		angelic.setAdaptive(field, adaptive);
		evil.setAdaptive(field, adaptive);
	}
	
	@Override
	public String getRandomizerName() {
		return getClass().getName();
	}
	
}
