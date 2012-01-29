package org.tetrevil.swing;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.tetrevil.Block;
import org.tetrevil.Field;

public class TetrevilTableCellRenderer extends DefaultTableCellRenderer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected Field field;
	
	public TetrevilTableCellRenderer(Field field) {
		this.field = field;
	}
	
	protected ColorProvider colors = new DefaultColorProvider();
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		Block b = (Block) value;
		JLabel c = (JLabel) super.getTableCellRendererComponent(table, b, isSelected, hasFocus, row, column);
		c.setText(value != null && b.isActive() ? String.valueOf(field.getLines()) : " ");
		c.setBackground(colors.provideColor(b));
		if(field.isPaused() && b != null) {
			if(!b.isActive() && b != Block.X)
				c.setBackground(null);
			else
				c.setText("P");
		}
		if(b == null && field.isGameOver()) {
			c.setText(String.valueOf(field.getLines()));
		}
		return c;
	}
	
}
