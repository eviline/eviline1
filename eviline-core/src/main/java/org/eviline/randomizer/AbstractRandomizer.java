package org.eviline.randomizer;

import java.util.ArrayList;
import java.util.List;

import org.eviline.ShapeType;

public abstract class AbstractRandomizer implements Cloneable, Randomizer {
	@Override
	public List<ShapeType> getNext() {
		String taunt = getTaunt();
		if(taunt == null || taunt.length() < 2)
			return new ArrayList<ShapeType>();
		List<ShapeType> ret = new ArrayList<ShapeType>();
		for(int i = 1; i < taunt.length(); i++) {
			char c = taunt.charAt(i);
			switch(c) {
			case 'O': ret.add(ShapeType.O); break;
			case 'T': ret.add(ShapeType.T); break;
			case 'J': ret.add(ShapeType.J); break;
			case 'L': ret.add(ShapeType.L); break;
			case 'S': ret.add(ShapeType.S); break;
			case 'Z': ret.add(ShapeType.Z); break;
			case 'I': ret.add(ShapeType.I); break;
			}
		}
		return ret;
	}

	@Override
	public Randomizer clone() {
		try {
			return (Randomizer) super.clone();
		} catch(CloneNotSupportedException cnse) {
			throw new InternalError("clone not supported???");
		}
	}
}
