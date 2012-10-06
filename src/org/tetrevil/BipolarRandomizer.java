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
		if((level % 3) == 0) {
			Shape ret = angelic.provideShape(field);
			taunt = angelic.getTaunt();
			return ret;
		} else {
			Shape ret = evil.provideShape(field);
			taunt = evil.getTaunt();
			return ret;
		}
	}
	
	@Override
	public void setRfactor(double rfactor) {
		angelic.setRfactor(rfactor);
		evil.setRfactor(rfactor);
	}
	@Override
	public void setDepth(int depth) {
		angelic.setDepth(depth);
		evil.setDepth(depth);
	}
	@Override
	public void setFair(boolean fair) {
		angelic.setFair(fair);
		evil.setFair(fair);
	}
	@Override
	public void setAdaptive(Field field, boolean adaptive) {
		angelic.setAdaptive(field, adaptive);
		evil.setAdaptive(field, adaptive);
	}
	
	@Override
	public String getRandomizerName() {
		return getClass().getName();
	}
	
}
