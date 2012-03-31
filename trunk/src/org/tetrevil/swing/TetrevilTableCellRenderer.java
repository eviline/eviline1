package org.tetrevil.swing;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;

import org.tetrevil.Block;
import org.tetrevil.Field;

/**
 * Cell renderer for a {@link TetrevilTable}
 * @author robin
 *
 */
public class TetrevilTableCellRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;
	
	protected Field field;
	
	protected Border ghost = BorderFactory.createLineBorder(Block.G.color());
	
	public TetrevilTableCellRenderer(Field field) {
		this.field = field;
		
	}
	
	protected ColorProvider colors = new DefaultColorProvider();
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		Block b = (Block) value;
		JLabel c = (JLabel) super.getTableCellRendererComponent(table, b, isSelected, hasFocus, row, column);
		c.setText(b != null && b.isActive() && b != Block.G? String.valueOf(field.getLines()) : " ");
		
		setFont(getFont().deriveFont(getFont().getSize2D() / 1.25f));
		c.setHorizontalTextPosition(SwingConstants.CENTER);
		c.setHorizontalAlignment(SwingConstants.CENTER);
		c.setForeground(Color.WHITE);
		c.setBackground(colors.provideColor(b));
		
		if(field.isPaused() && b != null) {
			if(!b.isActive() && b != Block.X)
				c.setBackground(null);
			else
				c.setText("P");
		}
		if(b == null && field.isGameOver()) {
//			c.setForeground(Color.WHITE);
			c.setText(String.valueOf(field.getLines()));
		} else if(field.isGameOver()) {
			c.setText(" ");
		}
		if(b == Block.G) {
			c.setBorder(ghost);
			c.setBackground(colors.provideColor(null));
		} else
			c.setBorder(null);
		return c;
	}
	
}
