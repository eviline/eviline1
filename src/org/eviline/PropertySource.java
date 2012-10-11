package org.eviline;

import java.util.Set;

public interface PropertySource {
	public boolean containsKey(String key);
	public String get(String key);
	public String put(String key, String value);
	public Set<String> keys();
}
