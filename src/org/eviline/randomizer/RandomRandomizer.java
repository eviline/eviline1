package org.eviline.randomizer;

import java.io.Serializable;

import org.eviline.Field;
import org.eviline.PropertySource;
import org.eviline.Shape;

/**
 * {@link Randomizer} that chooses at random, not using a bag.
 * @author robin
 *
 */
public class RandomRandomizer implements Randomizer, Serializable {

	protected PropertySource config;
	
	public RandomRandomizer(PropertySource p) {
		this.config = p;
	}
	
	@Override
	public Shape provideShape(Field field) {
		Shape[] shapes = Shape.values();
		return shapes[(int)(shapes.length * Math.random())];
	}

	@Override
	public String getTaunt() {
		return "";
	}

	@Override
	public PropertySource config() {
		return config;
	}
	
	@Override
	public String name() {
		return getClass().getName();
	}
}
