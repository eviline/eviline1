package org.tetrevil.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.TextLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;

import org.tetrevil.Block;
import org.tetrevil.BlockMetadata;
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
	
	protected Block b;
	protected BlockMetadata m;
	
	public TetrevilTableCellRenderer(Field field) {
		this.field = field;
		this.border = new TetrevilBorder(field);

		setOpaque(false);
		
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
//		field.getProvider().provideShape(field);
		
		b = (Block) value;
		m = field.getMetadata(column + Field.BUFFER - 1, row + Field.BUFFER);
		JLabel c = (JLabel) super.getTableCellRendererComponent(table, b, isSelected, hasFocus, row, column);
		c.setText(" ");

		if(column == 0 && row < "TETREVIL".length())
			c.setText("TETREVIL".substring(row, row+1));
		if(row == Field.HEIGHT && column < "     LINES:".length())
			c.setText("     LINES:".substring(column, column+1));
		if(row == Field.HEIGHT && column == "     LINES:".length())
			c.setText("" + field.getLines());
		
		
		setFont(getFont().deriveFont(getFont().getSize2D() / 1.25f));
		setFont(getFont().deriveFont((b != null && b.isActive()) ? Font.BOLD : Font.PLAIN));
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
		if(b == null && field.isPaused() && !field.isGameOver()) {
			c.setForeground(Color.BLACK);
			c.setText(String.valueOf(field.getLines()));
		} else if(b == null && field.isGameOver()) {
			c.setForeground(Color.BLACK);
			if(field.isMultiplayer())
				c.setText("<html><center>" + (field.isWinner() ? "W" : "L") + "<br>" + String.valueOf(field.getLines()) + "</center></html>");
			else
				c.setText("<html><center>" + field.getLines() + "</center></html>");
		} else if(field.isGameOver() || field.isPaused()) {
			if(b != null && b.isActive())
				c.setText(String.valueOf(field.getLines()));
			else
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
	
	@Override
	protected void paintComponent(Graphics gg) {
		Graphics g = gg.create();
		// TODO Auto-generated method stub
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
		if(b != null && b != Block.X) {
			if(m != null) {
				if(m.shape != null && !m.ghost) {
					g = g.create();
					g.setColor(getBackground().brighter().brighter());
					switch(m.shape.direction()) {
					case UP: break;
					case DOWN: ((Graphics2D) g).rotate(Math.PI, getWidth() / 2, getHeight() / 2); break;
					case RIGHT: ((Graphics2D) g).rotate(Math.PI / 2, getWidth() / 2, getHeight() / 2); break;
					case LEFT: ((Graphics2D) g).rotate(Math.PI / -2, getWidth() / 2, getHeight() / 2); break;
					}
					g.translate(0, -(int)((System.currentTimeMillis() / 100) % getHeight()));
					g.drawLine(getWidth(), 0, 0, getHeight());
					g.drawLine(getWidth(), getHeight(), 0, 2 * getHeight());
					g.drawLine(0, 0, getWidth(), getHeight());
					g.drawLine(0, getHeight(), getWidth(), 2 * getHeight());
				}
			}
		}
		
		g = gg.create();
		super.paintComponent(g);
	}
	
	@Override
	protected void paintBorder(Graphics g) {
		// TODO Auto-generated method stub
		super.paintBorder(g);
	}
	
	@Override
	protected void paintChildren(Graphics g) {
		// TODO Auto-generated method stub
		super.paintChildren(g);
	}
	
}
