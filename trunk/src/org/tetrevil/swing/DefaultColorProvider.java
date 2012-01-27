package org.tetrevil.swing;

import java.awt.Color;

import org.tetrevil.Block;

public class DefaultColorProvider implements ColorProvider {

	@Override
	public Color provideColor(Block block) {
		if(block == null)
			return null;
		if(block == Block.X)
			return Color.BLACK;
		if(!block.isActive())
			return Color.GRAY;
		else
			return Color.RED.darker();
	}

}
