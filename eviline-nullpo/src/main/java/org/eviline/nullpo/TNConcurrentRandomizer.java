package org.eviline.nullpo;

import org.eviline.Shape;
import org.eviline.randomizer.ConcurrentRandomizer;
import org.eviline.randomizer.ThreadedMaliciousRandomizer;
import org.eviline.randomizer.MaliciousRandomizer.MaliciousRandomizerProperties;

import mu.nu.nullpo.game.play.GameEngine;

public class TNConcurrentRandomizer extends TNRandomizer {
	@Override
	public void setEngine(GameEngine engine) {
		super.setEngine(engine);
		MaliciousRandomizerProperties mp = new MaliciousRandomizerProperties(3, 0, false, 30);
		ThreadedMaliciousRandomizer r = new ThreadedMaliciousRandomizer(mp);
		r.setRandom(engine.random);
		field.setProvider(new ConcurrentRandomizer(r));
	}
	
	@Override
	public String getName() {
		return "FAST EVIL";
	}
	
	@Override
	public synchronized int next() {
		if(regenerate)
			field.setShape(Shape.O_DOWN);
		return super.next();
	}
}
