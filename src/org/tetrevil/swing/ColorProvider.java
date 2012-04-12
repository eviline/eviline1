package org.tetrevil.swing;

import java.awt.Color;

import org.tetrevil.Block;

/**
 * Interface for objects which can provide block colors
 * @author robin
 *
 */
public interface ColorProvider {
	public Color provideColor(Block block);
}
