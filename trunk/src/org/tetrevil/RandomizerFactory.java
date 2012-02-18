package org.tetrevil;

import java.lang.reflect.Constructor;

import org.tetrevil.runner.MainApplet;

/**
 * Class with factory methods for constructing a {@link MaliciousRandomizer}.
 * Needs to be extended to cover all randomizers, but {@link MainApplet} is too tied
 * to {@link MaliciousRandomizer} right now.
 * @author robin
 *
 */
public class RandomizerFactory {
	private static Class<? extends MaliciousRandomizer> clazz = MaliciousRandomizer.class;
	
	public static MaliciousRandomizer newRandomizer() {
		try {
			return clazz.newInstance();
		} catch(Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public static MaliciousRandomizer newRandomizer(int depth, int dist) {
		try {
			Constructor<? extends MaliciousRandomizer> c = clazz.getConstructor(int.class, int.class);
			return c.newInstance(depth, dist);
		} catch(Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public static Class<? extends MaliciousRandomizer> getClazz() {
		return clazz;
	}
	
	public static void setClazz(Class<? extends MaliciousRandomizer> clazz) {
		RandomizerFactory.clazz = clazz;
	}
}
