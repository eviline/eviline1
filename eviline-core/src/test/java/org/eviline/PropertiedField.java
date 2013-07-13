package org.eviline;

import java.util.Set;

public class PropertiedField extends Field implements PropertySource {
	private static final long serialVersionUID = 0;
	
	protected PropertySource props = new BasicPropertySource();

	@Override
	public PropertiedField newInstance() {
		return new PropertiedField();
	}
	
	@Override
	public Field copyInto(Field target) {
		super.copyInto(target);
		if(target instanceof PropertiedField) {
			for(String key : keys())
				((PropertiedField) target).put(key, get(key));
		}
		return target;
	}
	
	@Override
	public PropertiedField clone() {
		return (PropertiedField) super.clone();
	}
	
	@Override
	public boolean containsKey(String key) {
		return props.containsKey(key);
	}

	@Override
	public String get(String key) {
		return props.get(key);
	}

	@Override
	public String put(String key, String value) {
		return props.put(key, value);
	}

	@Override
	public Set<String> keys() {
		return props.keys();
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
