package org.eviline.randomizer;

import static org.eviline.randomizer.RandomizerFactory.CLASS;
import static org.eviline.randomizer.RandomizerFactory.CONCURRENT;
import static org.eviline.randomizer.RandomizerFactory.DEPTH;
import static org.eviline.randomizer.RandomizerFactory.DISTRIBUTION;
import static org.eviline.randomizer.RandomizerFactory.FAIR;
import static org.eviline.randomizer.RandomizerFactory.NEXT;
import static org.eviline.randomizer.RandomizerFactory.RFACTOR;

import java.util.Properties;
import java.util.Set;

import org.eviline.Field;
import org.eviline.PropertySource;

public enum RandomizerPresets implements PropertySource {
	SADISTIC("Sadistic", ThreadedMaliciousRandomizer.class, DEPTH+"=5", RFACTOR+"=0", FAIR+"=false", DISTRIBUTION+"=0", CONCURRENT+"=false", NEXT+"=0"),
	EVIL("Evil", ThreadedMaliciousRandomizer.class, DEPTH+"=3", RFACTOR+"=0", FAIR+"=false", DISTRIBUTION+"=0", CONCURRENT+"=false", NEXT+"=0"),
	AGGRESSIVE("Aggressive", ThreadedMaliciousRandomizer.class, DEPTH+"=2", RFACTOR+"=0.02", FAIR+"=true", DISTRIBUTION+"=30", CONCURRENT+"=true", NEXT+"=0"),
	ANGELIC("Angelic", AngelRandomizer.class, DEPTH+"=2", RFACTOR+"=0.01", FAIR+"=true", DISTRIBUTION+"=15", CONCURRENT+"=true", NEXT+"=1"),
	BIPOLAR("Bipolar", BipolarRandomizer.class, DEPTH+"=2", RFACTOR+"=0.01", FAIR+"=true", DISTRIBUTION+"=15", CONCURRENT+"=true", NEXT+"=0"),
	;
	
	private String name;
	private Properties props = new Properties();
	
	private RandomizerPresets(String name, Class<? extends Randomizer> clazz, String... properties) {
		this.name = name;
		props.setProperty(CLASS, clazz.getName());
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
