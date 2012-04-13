package org.tetrevil.swing;

import java.awt.EventQueue;

import javax.swing.table.AbstractTableModel;

import org.tetrevil.Field;
import org.tetrevil.event.TetrevilEvent;
import org.tetrevil.event.TetrevilListener;

/**
 * Table model for a {@link TetrevilTable}.  Mostly just a passthrough to {@link Field#getBlock(int, int)}
 * @author robin
 *
 */
public class TetrevilTableModel extends AbstractTableModel implements TetrevilListener {
	private static final long serialVersionUID = 1L;
	protected Field field;
	
	public TetrevilTableModel(Field field) {
		this.field = field;
		field.addTetrevilListener(this);
	}
	
	@Override
	public int getRowCount() {
		return Field.HEIGHT + 1;
	}

	@Override
	public int getColumnCount() {
		return Field.WIDTH + 2;
	}
	
	@Override
	public String getColumnName(int column) {
		return "";
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return field.getBlock(columnIndex + Field.BUFFER - 1, rowIndex + Field.BUFFER);
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

	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
	}

	@Override
	public void gameReset(TetrevilEvent e) {
		changed();
	}

	@Override
	public void gamePaused(TetrevilEvent e) {
		changed();
	}

	@Override
	public void linesCleared(TetrevilEvent e) {
		changed();
	}
}