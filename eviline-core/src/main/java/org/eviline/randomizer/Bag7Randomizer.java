package org.eviline.randomizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eviline.BasicPropertySource;
import org.eviline.Field;
import org.eviline.PropertySource;
import org.eviline.Shape;
import org.eviline.ShapeType;

public class Bag7Randomizer extends AbstractRandomizer implements Randomizer {

	protected PropertySource config = new BasicPropertySource();
	
	protected List<ShapeType> bag = new ArrayList<ShapeType>();
	
	@Override
	public Shape provideShape(Field field) {
		if(bag.size() == 0) {
			bag.addAll(Arrays.asList(ShapeType.values()));
			Collections.shuffle(bag);
		}
		return bag.remove(0).starter();
	}

	@Override
	public String getTaunt() {
		return null;
	}

	@Override
	public PropertySource config() {
		return config;
	}

	@Override
	public String name() {
		return "Bag-7";
	}

}
