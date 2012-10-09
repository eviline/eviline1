package org.eviline;

public interface PropertySource {
	public boolean containsKey(String key);
	public String get(String key);
}
