package org.eviline.console.gui;

import java.util.List;

import org.eviline.Field;
import org.eviline.Shape;
import org.eviline.ShapeType;

import com.googlecode.lanterna.gui.TextGraphics;
import com.googlecode.lanterna.gui.component.AbstractComponent;
import com.googlecode.lanterna.terminal.TerminalPosition;
import com.googlecode.lanterna.terminal.TerminalSize;
import com.googlecode.lanterna.terminal.Terminal.Color;

public class NextShapeComponent extends AbstractComponent {
	protected Field field;
	
	protected BlockRenderer br;
	
	public NextShapeComponent(Field field) {
		this.field = field;
		br = new BlockRenderer(field);
	}
	
	@Override
	public void repaint(TextGraphics g) {
		g.setBackgroundColor(Color.BLACK);
		g.setForegroundColor(Color.BLACK);
		g.fillRectangle(' ', new TerminalPosition(0, 0), new TerminalSize(8, 4));
		
		List<ShapeType> nextList = field.getProvider().getNext();
		if(nextList.size() == 0)
			return;
		
		Shape next = nextList.get(0).starter();
		for(int i = 0; i < 4; i++) {
			br.render(next.type().inactive(), g, next.x(i), next.y(i));
		}
	}

	@Override
	protected TerminalSize calculatePreferredSize() {
		return new TerminalSize(8, 4);
	}

}
