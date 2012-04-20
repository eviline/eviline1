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
	
	protected TetrevilBorder border;
	protected Border ghost = BorderFactory.createLineBorder(Block.G.color());
	
	public TetrevilTableCellRenderer(Field field) {
		this.field = field;
		this.border = new TetrevilBorder(field);
		
		super.setBorder(BorderFactory.createEmptyBorder());
	}
	
	protected ColorProvider colors = new DefaultColorProvider();
	
	public Field getField() {
		return field;
	}
	
	public void setField(Field field) {
		this.field = field;
	}
	
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
		
		if(b == null || b == Block.X)
			c.setBackground(Color.WHITE);
		
		if(field.isPaused() && b != null) {
			if(!b.isActive() && b != Block.X)
				c.setBackground(null);
			else
				c.setText("P");
		}
		if(b == null && (field.isGameOver() || field.isPaused())) {
			c.setForeground(Color.BLACK);
			c.setText(String.valueOf(field.getLines()));
		} else if(field.isGameOver()) {
			c.setText(" ");
		}
//		if(b == Block.G) {
//			c.setBorder(ghost);
//			c.setBackground(colors.provideColor(null));
//		} else {
			border.set(colors, column + Field.BUFFER - 1, row + Field.BUFFER);
			c.setBorder(border);
//		}
		if(b != null && !b.isActive()) {
			c.setBackground(c.getBackground().darker().darker());
		}
		return c;
	}
	
}
