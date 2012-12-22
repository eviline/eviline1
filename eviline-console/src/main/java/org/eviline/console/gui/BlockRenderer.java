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
		case S: case SA: return Color.RED;
		case Z: case ZA: return Color.RED;
		case T: case TA: return Color.MAGENTA;
		}
		return Color.BLACK;
	}
	
	public String stringFor(Block b) {
		if(b == null)
			return "  ";
		switch(b) {
		case G: return "[]";
//		case O: case OA: return "\u25a0\u25a0";
		}
		return "\u2592\u2592";
	}
	
	public void render(Block b, TextGraphics g, int x, int y) {
		ScreenCharacterStyle[] s = new ScreenCharacterStyle[0];
//		if(b == Block.O || b == Block.OA)
//			s = new ScreenCharacterStyle[] {ScreenCharacterStyle.Bold, ScreenCharacterStyle.Reverse};
		g.setForegroundColor(foregroundFor(b));
		g.setBackgroundColor(backgroundFor(b));
		g.drawString(x * 2, y, stringFor(b), s);
	}
}
