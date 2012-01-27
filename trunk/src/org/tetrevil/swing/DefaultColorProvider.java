package org.tetrevil.swing;

import java.awt.Color;

import org.tetrevil.Block;

public class DefaultColorProvider implements ColorProvider {

	@Override
	public Color provideColor(Block block) {
		if(block == null)
			return null;
		Color ret = Color.BLACK;
		switch(block) {
		case I: case IA:
			ret = Color.RED;
			break;
		case J: case JA:
			ret = Color.GREEN;
			break;
		case L: case LA:
			ret = Color.BLUE;
			break;
		case O: case OA:
			ret = Color.ORANGE;
			break;
		case S: case SA:
			ret = Color.CYAN;
			break;
		case T: case TA:
			ret = Color.MAGENTA;
			break;
		case Z: case ZA:
			ret = Color.YELLOW;
			break;
		}
		if(!block.isActive())
			ret = ret.darker();
		return ret;
	}

}
