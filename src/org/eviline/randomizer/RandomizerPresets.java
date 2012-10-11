package org.eviline.randomizer;

import java.util.Properties;
import java.util.Set;

import org.eviline.Field;
import org.eviline.PropertySource;

public enum RandomizerPresets implements PropertySource {
	SADISTIC("Sadistic", ThreadedMaliciousRandomizer.class, "depth=5", "rfactor=0", "fair=false", "distribution=0", "adaptive=false", "concurrent=false"),
	EVIL("Evil", ThreadedMaliciousRandomizer.class, "depth=3", "rfactor=0", "fair=false", "distribution=0", "adaptive=false", "concurrent=false"),
	AGGRESSIVE("Aggressive", ThreadedMaliciousRandomizer.class, "depth=3", "rfactor=0.02", "fair=true", "distribution=30", "adaptive=true", "concurrent=true"),
	ANGELIC("Angelic", AngelRandomizer.class, "depth=3", "rfactor=0.01", "fair=true", "distribution=15", "adaptive=false", "concurrent=true"),
	BIPOLAR("Bipolar", BipolarRandomizer.class, "depth=3", "rfactor=0.01", "fair=true", "distribution=15", "adaptive=false", "concurrent=true"),
	;
	
	private String name;
	private Properties props = new Properties();
	
	private RandomizerPresets(String name, Class<? extends Randomizer> clazz, String... properties) {
		this.name = name;
		props.setProperty("class", clazz.getName());
		for(String p : properties) {
			String[] pp = p.split("=", 2);
			props.setProperty(pp[0], pp[1]);
		}
	}
	
	public String getName() {
		return name;
	}
	
	public Properties getProperties() {
		return props;
	}

	@Override
	public boolean containsKey(String key) {
		return getProperties().containsKey(key);
	}

	@Override
	public String get(String key) {
		return getProperties().getProperty(key);
	}
	
	public Randomizer newRandomizer(Field field) {
		return new RandomizerFactory().newRandomizer(this);
	}

	@Override
	public String put(String key, String value) {
		throw new UnsupportedOperationException("presets are read only");
	}

	@Override
	public Set<String> keys() {
		return props.stringPropertyNames();
	}
}
