package org.eviline;

import java.util.Set;

public class ExtendedPropertySource implements PropertySource {
	protected PropertySource p;
	
	public ExtendedPropertySource(PropertySource p) {
		this.p = p;
	}
	
	public ExtendedPropertySource() {
		this(new BasicPropertySource());
	}

	@Override
	public boolean containsKey(String key) {
		return p.containsKey(key);
	}

	@Override
	public String get(String key) {
		return p.get(key);
	}

	@Override
	public String put(String key, String value) {
		return p.put(key, value);
	}

	@Override
	public Set<String> keys() {
		return p.keys();
	}
	
	public Boolean getBoolean(String key) {
		return get(key) == null ? null : Boolean.parseBoolean(get(key));
	}
	
	public Boolean putBoolean(String key, Boolean value) {
		Boolean ret = getBoolean(key);
		put(key, value == null ? null : value.toString());
		return ret;
	}
	
	public Integer getInt(String key) {
		return get(key) == null ? null : Integer.parseInt(get(key));
	}
	
	public Integer putInt(String key, Integer value) {
		Integer ret = getInt(key);
		put(key, value == null ? null : value.toString());
		return ret;
	}
	
	public Double getDouble(String key) {
		return get(key) == null ? null : Double.parseDouble(get(key));
	}
	
	public Double putDouble(String key, Double value) {
		Double ret = getDouble(key);
		put(key, value == null ? null : value.toString());
		return ret;
	}
}
