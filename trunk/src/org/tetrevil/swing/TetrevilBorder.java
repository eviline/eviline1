package org.tetrevil.swing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Stroke;

import org.tetrevil.Block;
import org.tetrevil.Field;

public class TetrevilBorder extends MulticolorLineBorder {
	protected Field field;
	protected Color bg = Color.BLACK;
	
	public static Color blend(Color c1, Color c2) {
		int r = (c1.getRed() + c2.getRed()) / 2;
		int g = (c1.getGreen() + c2.getGreen()) / 2;
		int b = (c1.getBlue() + c2.getBlue()) / 2;
		int a = (c1.getAlpha() + c2.getAlpha()) / 2;
		return new Color(r, g, b, a);
	}
	
	protected static Stroke normalStroke = new BasicStroke(2f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER);
	protected static Stroke topStroke = new BasicStroke(10f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER);
	
	public TetrevilBorder(Field field) {
		this.field = field;
		nstroke = sstroke = estroke = wstroke = normalStroke;
	}
	
	public void set(ColorProvider colors, int x, int y) {

		Block cb = field.getBlock(x, y);
		boolean ca = cb != null && cb.isActive();
		Color c = colors.provideColor(cb);
		Block n = field.getBlock(x, y-1);
		Block s = field.getBlock(x, y+1);
		Block e = field.getBlock(x+1, y);
		Block w = field.getBlock(x-1, y);
		
		boolean top = (y == Field.BUFFER);
		
		north = cb != n ? blend(c, colors.provideColor(n)) : null;
		south = cb != s ? blend(c, colors.provideColor(s)) : null;
		east = cb != e ? blend(c, colors.provideColor(e)) : null;
		west = cb != w ? blend(c, colors.provideColor(w)) : null;
		
		if(top && n != null && cb == null && (w == null || w == Block.X) && (e == null || e == Block.X)) {
			north = colors.provideColor(n);
			nstroke = topStroke;
		} else {
			nstroke = normalStroke;
		}
	}

	
	public Color getBg() {
		return bg;
	}

	public void setBg(Color bg) {
		this.bg = bg;
	}
	
}
