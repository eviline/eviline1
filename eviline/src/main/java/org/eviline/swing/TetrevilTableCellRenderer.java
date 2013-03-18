package org.eviline.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;

import org.eviline.Block;
import org.eviline.BlockMetadata;
import org.eviline.Field;
import org.eviline.ShapeType;
import org.eviline.ShapeTypeIcon;

/**
 * Cell renderer for a {@link TetrevilTable}
 * @author robin
 *
 */
public class TetrevilTableCellRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;
	
	private static final Color TRANSPARENT = new Color(0, 0, 0, 0);

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
	public Component getTableCellRendererComponent(
			JTable table, 
			Object value, 
			boolean isSelected, 
			boolean hasFocus, 
			int row, 
			int column) {

		b = (Block) value;
		m = field.getMetadata(column + Field.BUFFER - 1, row + Field.BUFFER);
		JLabel c = (JLabel) super.getTableCellRendererComponent(table, b, isSelected, hasFocus, row, column);
		c.setText(" ");

		c.setIcon(null);
		
//		String taunt = field.getProvider().getTaunt();
		String taunt;
		if(column == 0 && row < "EVILINE".length())
			c.setText("EVILINE".substring(row, row+1));
		else if(row == Field.HEIGHT && column < "     LINES:".length())
			c.setText("     LINES:".substring(column, column+1));
		else if(row == Field.HEIGHT && column == "     LINES:".length())
			c.setText("" + field.getLines());
		else if(field.getProvider() != null && (taunt = field.getProvider().getTaunt()) != null) {
			if(taunt.length() > 0)
				taunt = taunt.substring(1);
			if(column == Field.WIDTH + 1 && row < taunt.length()) {
//				if(row > 0) {
					String shape = taunt.substring(row, row+1);
					ShapeType type = ShapeType.valueOf(shape);
					c.setText(null);
					Icon icon = ShapeTypeIcon.forType(type).icon();
					c.setIcon(icon);
//				} else
//					c.setText("?");
			}
		}

		setFont(getFont().deriveFont(getFont().getSize2D() / 1.25f));
		setFont(getFont().deriveFont((b != null && b.isActive()) ? Font.BOLD : Font.PLAIN));
		c.setHorizontalTextPosition(SwingConstants.CENTER);
		c.setHorizontalAlignment(SwingConstants.CENTER);
		c.setForeground(Color.WHITE);
		c.setBackground(colors.provideColor(b));

		if(b == null)
			c.setBackground(TRANSPARENT);
		else if(b == Block.X) {
			Color xc = colors.provideColor(b);
			c.setBackground(new Color(xc.getRed(), xc.getGreen(), xc.getBlue(), 128));
		} else if(b == Block.G) {
			Color gb = c.getBackground();
			c.setBackground(new Color(gb.getRed(), gb.getGreen(), gb.getBlue(), 168));
		}

		if(field.isPaused() && b != null) {
			if(!b.isActive() && b != Block.X)
				c.setBackground(null);
			else
				c.setText("P");
		}
		if(b == null && field.isPaused() && !field.isGameOver()) {
//			c.setForeground(Color.BLACK);
//			c.setText(String.valueOf(field.getLines()));
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
		if(b != null && !b.isActive() && c != null) {
			c.setBackground(c.getBackground().darker().darker());
		}
		return c;
	}

	@Override
	protected void paintComponent(Graphics gg) {
		Graphics2D g = (Graphics2D) gg.create();

		Color bg = getBackground();
		
		if(m != null && m.ghostClearable) {
			bg = b.color();
			if(b != null && b != Block.G)
				setBackground(Color.WHITE);
			bg = new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), 64);
		}
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(bg);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		if(b != null && b != Block.X && m != null && m.shape != null && !m.ghost && !field.isPaused()) {
			g = (Graphics2D) gg.create();
			switch(m.shape.direction()) {
			case UP: break;
			case DOWN: g.rotate(Math.PI, getWidth() / 2, getHeight() / 2); break;
			case RIGHT: g.rotate(Math.PI / 2, getWidth() / 2, getHeight() / 2); break;
			case LEFT: g.rotate(Math.PI / -2, getWidth() / 2, getHeight() / 2); break;
			}
			Color c = getBackground().brighter().brighter();
			g.setColor(c);
			g.drawLine(getWidth() / 2, getHeight() / 4, getWidth() / 2, getHeight() * 3 / 4);
			g.drawLine(getWidth() / 2, getHeight() / 4, getWidth() * 3 / 8, getHeight() * 3 / 8);
			g.drawLine(getWidth() / 2, getHeight() / 4, getWidth() * 5 / 8, getHeight() * 3 / 8);
		}
//		if(b != null && b != Block.X) {
//			if(m != null) {
//				if(m.shape != null && !m.ghost) {
//					g = (Graphics2D) gg.create();
////					g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//					switch(m.shape.direction()) {
//					case UP: break;
//					case DOWN: g.rotate(Math.PI, getWidth() / 2, getHeight() / 2); break;
//					case RIGHT: g.rotate(Math.PI / 2, getWidth() / 2, getHeight() / 2); break;
//					case LEFT: g.rotate(Math.PI / -2, getWidth() / 2, getHeight() / 2); break;
//					}
//					g.translate(0, -(int)((System.currentTimeMillis() / 150) % getHeight()));
//					Color c = getBackground();
//					int STEP = 3;
//					for(int j = 0; j < STEP * 3; j += STEP) {
//						g.setColor(c = brighter(c));
//						for(int i = j; i < j+STEP; i++) {
//							g.drawLine(getWidth(), i, 0, getHeight() + i);
//							g.drawLine(0, i, getWidth(), getHeight() + i);
//							g.drawLine(getWidth(), getHeight() + i, 0, 2 * getHeight() + i);
//							g.drawLine(0, getHeight() + i, getWidth(), 2 * getHeight() + i);
//							g.drawLine(getWidth(), -getHeight() + i, 0, i);
//							g.drawLine(0, -getHeight() + i, getWidth(), i);
//						}
//					}
//				}
//			}
//		}

		g = (Graphics2D) gg.create();
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
