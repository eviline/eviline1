package org.tetrevil;

public interface PropertySource {
	public boolean containsKey(String key);
	public String get(String key);
}
