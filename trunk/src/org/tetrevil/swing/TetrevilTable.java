package org.tetrevil.swing;

import java.awt.Color;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JTable;

import org.tetrevil.Block;
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
		setFillsViewportHeight(true);
		
		TetrevilTableCellRenderer ttcr = new TetrevilTableCellRenderer(field);
		for(int i = 0; i < getColumnCount(); i++) {
			getColumnModel().getColumn(i).setCellRenderer(ttcr);
		}
		
		setShowGrid(true);
		setGridColor(Block.X.color());
		setRowSelectionAllowed(false);
		setColumnSelectionAllowed(false);
		setCellSelectionEnabled(false);
		setBackground(Block.X.color());
		
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				TetrevilTable table = (TetrevilTable) e.getComponent();
				int height = table.getHeight() / table.getModel().getRowCount();
				if(height > 0)
					table.setRowHeight(height);
				int width = table.getWidth() / table.getColumnCount();
				if(width > 0) {
					for(int i = 0; i < table.getColumnCount(); i++) {
						table.getColumnModel().getColumn(i).setWidth(width);
					}
				}
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
