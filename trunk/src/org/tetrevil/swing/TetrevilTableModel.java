package org.tetrevil.swing;

import java.awt.EventQueue;

import javax.swing.table.AbstractTableModel;

import org.tetrevil.Field;
import org.tetrevil.event.TetrevilEvent;
import org.tetrevil.event.TetrevilListener;

public class TetrevilTableModel extends AbstractTableModel implements TetrevilListener {

	protected Field field;
	
	public TetrevilTableModel(Field field) {
		this.field = field;
		field.addTetrevilListener(this);
	}
	
	@Override
	public int getRowCount() {
		return Field.HEIGHT + 3;
	}

	@Override
	public int getColumnCount() {
		return Field.WIDTH + 6;
	}
	
	@Override
	public String getColumnName(int column) {
		return "";
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return field.getBlock(columnIndex, rowIndex);
	}

	protected void changed() {
		if(EventQueue.isDispatchThread()) {
			fireTableDataChanged();
		} else {
			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					changed();
				}
			});
		}
	}
	
	@Override
	public void clockTicked(TetrevilEvent e) {
		changed();
	}

	@Override
	public void gameOver(TetrevilEvent e) {
		changed();
	}

	@Override
	public void shiftedLeft(TetrevilEvent e) {
		changed();
	}

	@Override
	public void shiftedRight(TetrevilEvent e) {
		changed();
	}

	@Override
	public void rotatedLeft(TetrevilEvent e) {
		changed();
	}

	@Override
	public void rotatedRight(TetrevilEvent e) {
		changed();
	}

}
