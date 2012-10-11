package org.eviline.randomizer;

import org.eviline.ExtendedPropertySource;
import org.eviline.Field;
import org.eviline.PropertySource;
import org.eviline.runner.MainApplet;

/**
 * Class with factory methods for constructing a {@link MaliciousRandomizer}.
 * Needs to be extended to cover all randomizers, but {@link MainApplet} is too tied
 * to {@link MaliciousRandomizer} right now.
 * @author robin
 *
 */
public class RandomizerFactory {
	public static final String DEPTH = "depth";
	public static final String RFACTOR = "rfactor";
	public static final String FAIR = "fair";
	public static final String DISTRIBUTION = "distribution";
	public static final String CONCURRENT = "concurrent";
	
	public Randomizer newRandomizer(PropertySource props) {
		ExtendedPropertySource eps = new ExtendedPropertySource(props);
		Randomizer ret;
		try {
			String className = props.get("class");
			if(className == null)
				className = MaliciousRandomizer.class.getName();
			Class<? extends Randomizer> clazz = Class.forName(className).asSubclass(Randomizer.class);
			ret = clazz.getConstructor(PropertySource.class).newInstance(props);
		} catch(Exception ex) {
			throw new RuntimeException(ex);
		}
		if(eps.getBoolean(CONCURRENT))
			ret = new ConcurrentDelegatingRandomizer(ret);
		return ret;
	}
}
