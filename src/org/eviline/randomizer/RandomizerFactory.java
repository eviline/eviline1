package org.eviline.randomizer;

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
	private Field field;
	
	public RandomizerFactory(Field field) {
		this.field = field;
	}
	
	public Randomizer newRandomizer(PropertySource props) {
		Randomizer ret;
		try {
			String className = props.get("class");
			if(className == null)
				className = MaliciousRandomizer.class.getName();
			Class<? extends Randomizer> clazz = Class.forName(className).asSubclass(Randomizer.class);
			if(MaliciousRandomizer.class.isAssignableFrom(clazz) && props.containsKey("distribution")) {
				int dist = Integer.parseInt(props.get("distribution"));
				ret = clazz.getConstructor(int.class, int.class).newInstance(MaliciousRandomizer.DEFAULT_DEPTH, dist);
			} else
				ret = clazz.newInstance();
		} catch(Exception ex) {
			throw new RuntimeException(ex);
		}
		if(ret instanceof MaliciousRandomizer) {
			MaliciousRandomizer mr = (MaliciousRandomizer) ret;
			if(props.containsKey("depth"))
				mr.setDepth(Integer.parseInt(props.get("depth")));
			if(props.containsKey("rfactor"))
				mr.setRfactor(Double.parseDouble(props.get("rfactor")));
			if(props.containsKey("fair"))
				mr.setFair(Boolean.parseBoolean(props.get("fair")));
			if(props.containsKey("adaptive"))
				mr.setAdaptive(field, Boolean.parseBoolean(props.get("adaptive")));
		}
		if(Boolean.parseBoolean(props.get("concurrent")))
			ret = new ConcurrentDelegatingRandomizer(ret);
		return ret;
	}
}
