package org.eviline.randomizer;

import org.eviline.Field;
import org.eviline.PropertySource;
import org.eviline.Shape;

/**
 * Interface for objects which can provide {@link Shape}s to a {@link Field}.
 * @author robin
 *
 */
public interface Randomizer {
	/**
	 * Return the {@link Shape} to be next played on the argument {@link Field}
	 * @param field
	 * @return
	 */
	public Shape provideShape(Field field);
	
	public String getTaunt();
	
	public PropertySource config();
	
	public String name();
}
