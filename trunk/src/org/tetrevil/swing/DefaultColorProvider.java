package org.tetrevil.swing;

import java.awt.Color;

import org.tetrevil.Block;

public class DefaultColorProvider implements ColorProvider {

	@Override
	public Color provideColor(Block block) {
		if(block == null)
			return Color.BLACK;
		Color ret = Color.GRAY;
		switch(block) {
		case I: case IA:
			ret = new Color(0, 159, 218);
			break;
		case J: case JA:
			ret = new Color(0, 101, 189);
			break;
		case L: case LA:
			ret = new Color(255, 121, 0);
			break;
		case O: case OA:
			ret = new Color(254, 203, 0);
			break;
		case S: case SA:
			ret = new Color(105, 190, 40);
			break;
		case T: case TA:
			ret = new Color(149, 45, 152);
			break;
		case Z: case ZA:
			ret = new Color(237, 41, 57);
			break;
		}
		if(!block.isActive())
			ret = ret.darker();
		return ret;
	}

}
