package org.tetrevil.swing;

import java.awt.Color;

import org.tetrevil.Block;

/**
 * Default implementation of {@link ColorProvider}
 * @author robin
 *
 */
public class DefaultColorProvider implements ColorProvider {
	
	@Override
	public Color provideColor(Block block) {
		if(block == null)
			return Color.GRAY.darker();
		Color ret = block.color();
		return ret;
	}

}
