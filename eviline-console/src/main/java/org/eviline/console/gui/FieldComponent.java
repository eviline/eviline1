package org.eviline.console.gui;

import org.eviline.Block;
import org.eviline.Field;

import com.googlecode.lanterna.gui.TextGraphics;
import com.googlecode.lanterna.gui.component.AbstractComponent;
import com.googlecode.lanterna.terminal.TerminalSize;

public class FieldComponent extends AbstractComponent {

	private Field field;
	private BlockRenderer blockRenderer;
	
	public FieldComponent(Field field) {
		this.field = field;
		this.blockRenderer = new BlockRenderer(field);
	}
	
	@Override
	public void repaint(TextGraphics g) {
		synchronized(field) {
			for(int x = 0; x < Field.WIDTH; x++) {
				for(int y = 0; y < Field.HEIGHT; y++) {
					Block b = field.getFieldBlock(x, y);
					blockRenderer.render(b, g, x, y);
				}
			}
		}
	}

	@Override
	protected TerminalSize calculatePreferredSize() {
		return new TerminalSize(Field.WIDTH * 2, Field.HEIGHT);
	}

}
