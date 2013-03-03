package org.eviline.console.gui;

import org.eviline.Field;
import org.eviline.console.engine.ConsoleEngine;

import com.googlecode.lanterna.gui.Border;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.component.Label;
import com.googlecode.lanterna.gui.component.Panel;
import com.googlecode.lanterna.gui.component.Panel.Orientation;
import com.googlecode.lanterna.gui.layout.BorderLayout;

public class EvilineWindow extends Window {
	protected ConsoleEngine engine;
	
	public EvilineWindow(ConsoleEngine engine) {
		super("EVILINE");
		this.engine = engine;
		Field field = engine.getField();
		Panel p = new Panel(new Border.Invisible(), Orientation.HORISONTAL);
		p.setLayoutManager(new BorderLayout());
		p.addComponent(new FieldComponent(field), BorderLayout.LEFT);
		p.addComponent(new Label(" "));
		p.addComponent(new FieldStatisticsPanel(field), BorderLayout.RIGHT);
		addComponent(p);
		
		addWindowListener(new KeyboardInputHandler(engine));
	}
	
	
}
