package org.eviline;

import java.util.Set;

import org.eviline.randomizer.RandomizerFactory;

/**
 * Interface for a readable source of properties.  {@link PropertySource} instances
 * are used as input to a {@link RandomizerFactory}.
 * 
 * @author robin robin
 */
public interface PropertySource {
	/**
	 * Does the specified property key exist?
	 * @param key
	 * @return
	 */
	public boolean containsKey(String key);
	/**
	 * Return the value of the specified property key
	 * @param key
	 * @return
	 */
	public String get(String key);
	/**
	 * Set the value of the specified property key
	 * @param key
	 * @param value
	 * @return
	 */
	public String put(String key, String value);
	/**
	 * Return the set of all property keys
	 * @return
	 */
	public Set<String> keys();
}
