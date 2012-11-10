package org.eviline;

import java.io.Serializable;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

/**
 * A basic implementation of {@link PropertySource} backed by a {@link Map}.
 * @author robin
 *
 */
public class BasicPropertySource implements PropertySource, Serializable {
	private static final long serialVersionUID = 0;
	
	/**
	 * The map that backs this {@link BasicPropertySource}.
	 */
	protected Map<String, String> map;
	
	/**
	 * Create a new {@link BasicPropertySource} backed by a {@link TreeMap}
	 */
	public BasicPropertySource() {
		this(new TreeMap<String, String>());
	}
	
	/**
	 * Create a {@link BasicPropertySource} backed by the argument {@link Properties}.
	 * @param p
	 */
	public BasicPropertySource(Properties p) {
		this((Map) p);
	}
	
	/**
	 * Create a {@link BasicPropertySource} backed by the argument {@link Map}.
	 * @param p
	 */
	public BasicPropertySource(Map<String, String> p) {
		this.map = p;
	}

	@Override
	public boolean containsKey(String key) {
		return map.containsKey(key);
	}

	@Override
	public String get(String key) {
		return (String) map.get(key);
	}

	@Override
	public String put(String key, String value) {
		return (String) map.put(key, value);
	}

	@Override
	public Set<String> keys() {
		return map.keySet();
	}
}
