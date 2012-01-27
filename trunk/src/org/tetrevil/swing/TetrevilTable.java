package org.tetrevil.swing;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JTable;

import org.tetrevil.Field;

public class TetrevilTable extends JTable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Field field;
	
	public TetrevilTable(Field field) {
		super(new TetrevilTableModel(field));
		this.field = field;
		
		setTableHeader(null);
		
		TetrevilTableCellRenderer ttcr = new TetrevilTableCellRenderer();
		for(int i = 0; i < getColumnCount(); i++) {
			getColumnModel().getColumn(i).setCellRenderer(ttcr);
		}
		
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				TetrevilTable table = (TetrevilTable) e.getComponent();
				table.setRowHeight(table.getHeight() / table.getModel().getRowCount());
			}
		});
	}

	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
		((TetrevilTableModel) getModel()).setField(field);
	}
}
