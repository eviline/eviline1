package org.tetrevil.swing;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JScrollPane;

import org.tetrevil.Field;

/**
 * A {@link JScrollPane} that holds, and will resize if necessary, a {@link TetrevilTable}
 * @author robin
 *
 */
public class TetrevilComponent extends JScrollPane {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Field field;
	protected TetrevilTable table;
	
	public TetrevilComponent(Field field) {
		super(new TetrevilTable(field), JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		this.field = field;
		table = (TetrevilTable) getViewport().getView();
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				JScrollPane scroll = (JScrollPane) e.getComponent();
				scroll.getViewport().getView().setSize(scroll.getViewport().getExtentSize());
			}
		});
	}

	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
		table.setField(field);
	}

	public TetrevilTable getTable() {
		return table;
	}
}
