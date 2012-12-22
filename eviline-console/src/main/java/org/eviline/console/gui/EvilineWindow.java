package org.eviline.console.gui;

import org.eviline.Field;

import com.googlecode.lanterna.gui.Border;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.component.Panel;
import com.googlecode.lanterna.gui.component.Panel.Orientation;
import com.googlecode.lanterna.gui.layout.BorderLayout;

public class EvilineWindow extends Window {
	protected Field field;
	
	public EvilineWindow(Field field) {
		super("EVILINE");
		this.field = field;
		Panel p = new Panel(new Border.Invisible(), Orientation.HORISONTAL);
		p.setLayoutManager(new BorderLayout());
		p.addComponent(new FieldComponent(field), BorderLayout.LEFT);
		p.addComponent(new FieldStatisticsPanel(field), BorderLayout.CENTER);
		addComponent(p);
		
		addWindowListener(new KeyboardInputHandler(field));
	}
	
	
}
