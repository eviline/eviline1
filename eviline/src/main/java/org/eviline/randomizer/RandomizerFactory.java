package org.eviline.randomizer;

import org.eviline.ExtendedPropertySource;
import org.eviline.PropertySource;
import org.eviline.runner.MainApplet;

public class RandomizerFactory {
	public static final String CLASS = "class";
	public static final String DEPTH = "depth";
	public static final String RFACTOR = "rfactor";
	public static final String FAIR = "fair";
	public static final String DISTRIBUTION = "distribution";
	public static final String CONCURRENT = "concurrent";
	public static final String NEXT = "next";
	
	public Randomizer newRandomizer(PropertySource props) {
		ExtendedPropertySource eps = new ExtendedPropertySource(props);
		Randomizer ret;
		try {
			String className = props.get(CLASS);
			if(className == null)
				className = MaliciousRandomizer.class.getName();
			Class<? extends Randomizer> clazz = Class.forName(className).asSubclass(Randomizer.class);
			ret = clazz.getConstructor(PropertySource.class).newInstance(props);
		} catch(Exception ex) {
			throw new RuntimeException(ex);
		}
		boolean concurrency = eps.get(CONCURRENT) == null ? false : eps.getBoolean(CONCURRENT);
		int nextSize = eps.get(NEXT) == null ? 0 : eps.getInt(NEXT);
		if(concurrency || nextSize > 0)
			ret = new QueuedRandomizer(ret, nextSize, concurrency);
		return ret;
	}
}
