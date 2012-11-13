package org.eviline.nullpo;

import mu.nu.nullpo.game.play.GameEngine;

import org.eviline.Shape;
import org.eviline.randomizer.AngelRandomizer;
import org.eviline.randomizer.ConcurrentRandomizer;
import org.eviline.randomizer.ThreadedMaliciousRandomizer;
import org.eviline.randomizer.MaliciousRandomizer.MaliciousRandomizerProperties;

public class TNConcurrentAngelRandomizer extends TNRandomizer {
	@Override
	public void setEngine(GameEngine engine) {
		super.setEngine(engine);
		MaliciousRandomizerProperties mp = new MaliciousRandomizerProperties(3, .01, true, 15);
		AngelRandomizer t = new AngelRandomizer(mp);
		t.setRandom(engine.random);
		field.setProvider(new ConcurrentRandomizer(t));
	}
	
	@Override
	public String getName() {
		return "FAST ANGELIC";
	}
	
	@Override
	public synchronized int next() {
		if(regenerate)
			field.setShape(Shape.O_DOWN);
		return super.next();
	}

}
