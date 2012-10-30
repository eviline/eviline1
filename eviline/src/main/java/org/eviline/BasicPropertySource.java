package org.eviline;

import java.io.Serializable;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

public class BasicPropertySource implements PropertySource, Serializable {
	private static final long serialVersionUID = 0;
	
	protected Map<String, String> map;
	
	public BasicPropertySource() {
		this(new TreeMap<String, String>());
	}
	
	public BasicPropertySource(Properties p) {
		this((Map) p);
	}
	
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
