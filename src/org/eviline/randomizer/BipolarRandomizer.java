package org.eviline.randomizer;

import org.eviline.Field;
import org.eviline.PropertySource;
import org.eviline.Shape;

public class BipolarRandomizer implements Randomizer {
	protected AngelRandomizer angelic;
	protected ThreadedMaliciousRandomizer evil;
	
	protected PropertySource config;
	protected String taunt;
	
	public BipolarRandomizer(PropertySource p) {
		this.config = p;
		angelic = new AngelRandomizer(p);
		evil = new ThreadedMaliciousRandomizer(p);
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
	public String getTaunt() {
		return taunt;
	}

	@Override
	public PropertySource config() {
		return config;
	}
	
	@Override
	public String name() {
		return getClass().getName();
	}
}
