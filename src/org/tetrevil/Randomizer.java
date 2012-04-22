package org.tetrevil;

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
	
	public String getRandomizerName();
	
	public MaliciousRandomizer getMaliciousRandomizer();
}
