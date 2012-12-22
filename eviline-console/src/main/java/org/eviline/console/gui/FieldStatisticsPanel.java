package org.eviline.console.gui;

import org.eviline.Field;
import org.eviline.ShapeType;

import com.googlecode.lanterna.gui.TextGraphics;
import com.googlecode.lanterna.gui.component.Label;
import com.googlecode.lanterna.gui.component.Panel;
import com.googlecode.lanterna.gui.layout.BorderLayout;
import com.googlecode.lanterna.gui.layout.LayoutManager;
import com.googlecode.lanterna.gui.layout.VerticalLayout;
import com.googlecode.lanterna.terminal.TerminalSize;

public class FieldStatisticsPanel extends Panel {
	private Field field;
	
	private Panel next = new Panel(Orientation.VERTICAL);
	private Label lines = new Label();
	
	public FieldStatisticsPanel(Field field) {
		super(Orientation.VERTICAL);
		this.field = field;
		setLayoutManager(new BorderLayout());
		next.addComponent(new Label("Next:"));
		next.addComponent(new NextShapeComponent(field));
		addComponent(next, BorderLayout.TOP);
		Panel p = new Panel(Orientation.VERTICAL);
		p.addComponent(new Label("Lines:"));
		p.addComponent(lines);
		addComponent(p, BorderLayout.BOTTOM);
		addComponent(new Label(" "), BorderLayout.CENTER);
		
	}
	
	@Override
	public void repaint(TextGraphics graphics) {
		lines.setText("" + field.getLines());
		super.repaint(graphics);
	}
}
