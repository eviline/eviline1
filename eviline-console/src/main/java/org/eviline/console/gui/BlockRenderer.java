package org.eviline.console.gui;

import org.eviline.Block;
import org.eviline.Field;

import com.googlecode.lanterna.gui.TextGraphics;
import com.googlecode.lanterna.screen.ScreenCharacterStyle;
import com.googlecode.lanterna.terminal.Terminal.Color;

public class BlockRenderer {
	private Field field;
	
	public BlockRenderer(Field field) {
		this.field = field;
	}
	
	public Color foregroundFor(Block b) {
		if(b == null)
			return Color.BLACK;
		switch(b) {
		case G: return foregroundFor(field.getShape().type().inactive());
		case I: case IA: return Color.CYAN;
		case J: case JA: return Color.BLUE;
		case L: case LA: return Color.GREEN;
		case O: case OA: return Color.YELLOW;
		case S: case SA: return Color.YELLOW;
		case Z: case ZA: return Color.RED;
		case T: case TA: return Color.MAGENTA;
		}
		return Color.BLACK;
	}
	
	public Color backgroundFor(Block b) {
		if(b == null)
			return Color.BLACK;
		switch(b) {
		case G: return Color.BLACK;
		case I: case IA: return Color.CYAN;
		case J: case JA: return Color.BLUE;
		case L: case LA: return Color.GREEN;
		case O: case OA: return Color.YELLOW;
		case S: case SA: return Color.YELLOW;
		case Z: case ZA: return Color.RED;
		case T: case TA: return Color.MAGENTA;
		}
		return Color.BLACK;
	}
	
	public char characterFor(Block b) {
		if(b == null)
			return ' ';
		switch(b) {
		case G: return '#';
		case O: case OA: return '\u2593';
		case S: case SA: return '\u2591';
		}
		return '\u2592';
	}
	
	public void render(Block b, TextGraphics g, int x, int y) {
		g.setForegroundColor(foregroundFor(b));
		g.setBackgroundColor(backgroundFor(b));
		g.drawString(x * 2, y, "" + characterFor(b));
		g.drawString(x * 2 + 1, y, "" + characterFor(b));
	}
}
