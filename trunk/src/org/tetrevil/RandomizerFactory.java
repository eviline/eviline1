package org.tetrevil;

import java.lang.reflect.Constructor;

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
