package org.tetrevil.swing;

import javax.swing.JTable;

import org.tetrevil.Field;

public class TetrevilTable extends JTable {
	protected Field field;
	
	public TetrevilTable(Field field) {
		super(new TetrevilTableModel(field));
		this.field = field;
		
		TetrevilTableCellRenderer ttcr = new TetrevilTableCellRenderer();
		for(int i = 0; i < getColumnCount(); i++) {
			getColumnModel().getColumn(i).setCellRenderer(ttcr);
		}
	}
}
