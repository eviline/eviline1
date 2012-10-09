package org.eviline.swing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

import org.eviline.Block;
import org.eviline.BlockMetadata;
import org.eviline.Field;

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
		BlockMetadata m;
		int id = ((m = field.getMetadata(x, y)) != null) ? m.shapeId : 0;
		boolean ca = cb != null && cb.isActive();
		Color c = colors.provideColor(cb);
		
		Block n = field.getBlock(x, y-1);
		Block s = field.getBlock(x, y+1);
		Block e = field.getBlock(x+1, y);
		Block w = field.getBlock(x-1, y);
		
		int nid = ((m = field.getMetadata(x, y-1)) != null) ? m.shapeId : 0;
		int sid = ((m = field.getMetadata(x, y+1)) != null) ? m.shapeId : 0;
		int eid = ((m = field.getMetadata(x+1, y)) != null) ? m.shapeId : 0;
		int wid = ((m = field.getMetadata(x-1, y)) != null) ? m.shapeId : 0;
		
//		System.out.println("id:" + id + ", nid:" + nid + ", m:" + m);
		
		boolean top = (y == Field.BUFFER);
		
		Color idc = field.isPaused() ? Color.BLACK : Color.WHITE;
		
		north = cb != n ? blend(c, colors.provideColor(n)) : id != nid ? blend(c, idc) : null;
		south = cb != s ? blend(c, colors.provideColor(s)) : id != sid ? blend(c, idc) : null;
		east = cb != e ? blend(c, colors.provideColor(e)) : id != eid ? blend(c, idc) : null;
		west = cb != w ? blend(c, colors.provideColor(w)) : id != wid ? blend(c, idc) : null;

		if(cb == Block.G) {
			c = field.getShape().type().inactive().active().color();
			if(north != null) north = c;
			if(south != null) south = c;
			if(east != null) east = c;
			if(west != null) west = c;
		}
		
		if(top && n != null && cb == null && (w == null || w == Block.X) && (e == null || e == Block.X)) {
			north = colors.provideColor(n);
			nstroke = topStroke;
		} else if(top && n != null && n.isActive() && (cb == null || !cb.isActive())) {
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
