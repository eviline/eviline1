package org.tetrevil.swing;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.tetrevil.Block;

public class TetrevilTableCellRenderer extends DefaultTableCellRenderer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected ColorProvider colors = new DefaultColorProvider();
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		c.setText(" ");
		c.setBackground(colors.provideColor((Block) value));
		return c;
	}
	
}
