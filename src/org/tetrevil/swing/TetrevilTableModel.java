package org.tetrevil.swing;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.tetrevil.Field;
import org.tetrevil.Shape;
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
	
	protected Field last = new Field();

	public TetrevilTableModel(Field field) {
		this.field = field;
		last.setGhosting(true);
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
		return last.getBlock(columnIndex + Field.BUFFER - 1, rowIndex + Field.BUFFER);
	}
	
	private static void addCoordinate(List<Integer> xOffsets, List<Integer> yOffsets, int x, int y) {
		if(y < 0)
			return;
		for(int i = 0; i < xOffsets.size(); i++) {
			if(x == xOffsets.get(i) && y == yOffsets.get(i))
				return;
		}
		xOffsets.add(x);
		yOffsets.add(y);
	}

	private static void addShape(List<Integer> xOffsets, List<Integer> yOffsets, int x, int y, Shape shape) {
		for(int i = 0; i < 4; i++) {
			addCoordinate(xOffsets, yOffsets, x + shape.x(i), y + shape.y(i));
			addCoordinate(xOffsets, yOffsets, x + shape.x(i) + 1, y + shape.y(i));
			addCoordinate(xOffsets, yOffsets, x + shape.x(i) - 1, y + shape.y(i));
			addCoordinate(xOffsets, yOffsets, x + shape.x(i), y + shape.y(i) + 1);
			addCoordinate(xOffsets, yOffsets, x + shape.x(i), y + shape.y(i) - 1);
		}
	}
	
	private static void addGhost(List<Integer> xOffsets, List<Integer> yOffsets, List<Integer> yRows, int x, int y, Shape shape) {
		addShape(xOffsets, yOffsets, x, y, shape);
		for(int i = 0; i < 4; i++) {
			if(!yRows.contains(y + shape.y(i)))
				yRows.add(y + shape.y(i));
			if(!yRows.contains(y + shape.y(i) + 1))
				yRows.add(y + shape.y(i) + 1);
			if(!yRows.contains(y + shape.y(i) - 1))
				yRows.add(y + shape.y(i) - 1);
		}
	}
	
	protected void changed(final TetrevilEvent e) {
		if(!EventQueue.isDispatchThread()) {
			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					changed(e);
				}
			});
			return;
		}
		if(e == null || e.getShape() == null) {
			e.getField().copyInto(last);
			last.reghost();
			fireTableDataChanged();
			return;
		}
		
		switch(e.getId()) {
		case TetrevilEvent.GAME_OVER:
		case TetrevilEvent.GAME_PAUSED:
		case TetrevilEvent.GAME_RESET:
		case TetrevilEvent.GARBAGE_RECEIVED:
		case TetrevilEvent.LINES_CLEARED:
			e.getField().copyInto(last);
			last.reghost();
			fireTableDataChanged();
			return;
		}
		
		List<Integer> xOffsets = new ArrayList<Integer>();
		List<Integer> yOffsets = new ArrayList<Integer>();
		List<Integer> yRows = new ArrayList<Integer>();

		int x = e.getX();
		int y = e.getY();
		int gy = e.getGhostY();
		Shape shape = e.getShape();
		
		addShape(xOffsets, yOffsets, x, y, shape);
		addGhost(xOffsets, yOffsets, yRows, x, gy, shape);
		if(last.getShape() != null) {
			addShape(xOffsets, yOffsets, last.getShapeX(), last.getShapeY(), last.getShape());
			addGhost(xOffsets, yOffsets, yRows, last.getShapeX(), last.getGhostY(), last.getShape());
		}
		
		for(int i = 0; i < xOffsets.size(); i++) {
			if(yRows.contains(yOffsets.get(i)))
				continue;
			x = xOffsets.get(i) - Field.BUFFER + 1;
			y = yOffsets.get(i) - Field.BUFFER;
			if(y >= 0 && x >= 0 && x < getColumnCount()) {
				fireTableCellUpdated(y, x);
			}
		}
		for(int i = 0; i < yRows.size(); i++) {
			y = yRows.get(i) - Field.BUFFER;
			if(y >= 0)
				fireTableRowsUpdated(y, y);
		}

		e.getField().copyInto(last);
		last.reghost();
	}
	
	@Override
	public void clockTicked(TetrevilEvent e) {
		changed(e);
	}

	@Override
	public void gameOver(TetrevilEvent e) {
		changed(e);
	}

	@Override
	public void shiftedLeft(TetrevilEvent e) {
		changed(e);
	}

	@Override
	public void shiftedRight(TetrevilEvent e) {
		changed(e);
	}

	@Override
	public void rotatedLeft(TetrevilEvent e) {
		changed(e);
	}

	@Override
	public void rotatedRight(TetrevilEvent e) {
		changed(e);
	}

	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
		changed(null);
	}

	@Override
	public void gameReset(TetrevilEvent e) {
		changed(e);
	}

	@Override
	public void gamePaused(TetrevilEvent e) {
		changed(e);
	}

	@Override
	public void linesCleared(TetrevilEvent e) {
		changed(e);
	}
	
	@Override
	public void garbageReceived(TetrevilEvent e) {
		changed(e);
	}
	
	@Override
	public void shapeSpawned(TetrevilEvent e) {
		changed(e);
	}
	
	@Override
	public void shapeLocked(TetrevilEvent e) {
		changed(e);
	}
}
