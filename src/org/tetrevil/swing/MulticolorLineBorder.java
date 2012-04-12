package org.tetrevil.swing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;

import javax.swing.border.AbstractBorder;
import javax.swing.border.LineBorder;

public class MulticolorLineBorder extends AbstractBorder {
	protected Color north = null;
	protected Color south = null;
	protected Color east = null;
	protected Color west = null;
	
	protected Stroke nstroke;
	protected Stroke sstroke;
	protected Stroke estroke;
	protected Stroke wstroke;
	
	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		Graphics2D gg = (Graphics2D) g.create();
		
		Stroke s = gg.getStroke();
		
		if(north != null) {
			gg.setStroke(nstroke != null ? nstroke : s);
			gg.setColor(north); gg.drawLine(x, y, x+width-1, y);
		}
		if(east != null) {
			gg.setStroke(estroke != null ? estroke : s);
			gg.setColor(east); gg.drawLine(x+width-1, y, x+width-1, y+height-1);
		}
		if(south != null) {
			gg.setStroke(sstroke != null ? sstroke : s);
			gg.setColor(south); gg.drawLine(x+width-1, y+height-1, x, y+height-1);
		}
		if(west != null) {
			gg.setStroke(wstroke != null ? wstroke : s);
			gg.setColor(west); gg.drawLine(x, y+height-1, x, y);
		}
	}
	
	public Color getNorth() {
		return north;
	}
	public void setNorth(Color north) {
		this.north = north;
	}
	public Color getSouth() {
		return south;
	}
	public void setSouth(Color south) {
		this.south = south;
	}
	public Color getEast() {
		return east;
	}
	public void setEast(Color east) {
		this.east = east;
	}
	public Color getWest() {
		return west;
	}
	public void setWest(Color west) {
		this.west = west;
	}
	
	
}
