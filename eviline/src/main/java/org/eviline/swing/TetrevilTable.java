package org.eviline.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.GeneralPath;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.swing.JTable;
import javax.swing.Timer;

import org.eviline.Block;
import org.eviline.Field;
import org.eviline.event.HistoryMovePath;

/**
 * Table for displaying a {@link Field} that resizes itself as appropriate
 * @author robin
 *
 */
public class TetrevilTable extends JTable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected Field field;
	
	protected TetrevilTableCellRenderer ttcr;
	
	protected HistoryMovePath hpath;
	
	public TetrevilTable(Field field) {
		super(new TetrevilTableModel(field));
		this.field = field;
		
		hpath = new HistoryMovePath();
		field.addEvilineListener(hpath);

		setDoubleBuffered(true);
		setTableHeader(null);
		setFillsViewportHeight(true);
		
		ttcr = new TetrevilTableCellRenderer(field);
		for(int i = 0; i < getColumnCount(); i++) {
			getColumnModel().getColumn(i).setCellRenderer(ttcr);
		}
		
		setShowGrid(false);
		setShowHorizontalLines(false);
		setShowVerticalLines(false);
		setRowSelectionAllowed(false);
		setColumnSelectionAllowed(false);
		setCellSelectionEnabled(false);
		setBackground(Block.X.color());
		setIntercellSpacing(new Dimension(0, 0));
		
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
				setShowGrid(false);
			}
		});
		
		Timer repainter = new Timer(150, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				repaint();
			}
		});
		repainter.start();
	}

	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
		ttcr.setField(field);
		((TetrevilTableModel) getModel()).setField(field);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g = g.create();
		if(!(g instanceof Graphics2D))
			return;
		Graphics2D g2 = (Graphics2D) g;
		double xscale = getWidth() / (double)(Field.WIDTH + 2);
		double yscale = getRowHeight();

		g2.translate(xscale, 0);
		
		Color c;
		if(hpath.getLastType() != null) {
			c = ttcr.colors.provideColor(hpath.getLastType().inactive());

			for(List<long[]> path : hpath.getLastPaths()) {
				int remaining = path.size();
				for(long[] p : path) {
					remaining--;
					g2.setColor(new Color(
							c.getRed(), 
							c.getGreen(), 
							c.getBlue(), 
							(int)(96 / Math.pow(1 + (System.currentTimeMillis() - p[2]) / 1000. + remaining / 5., 2))));
					Rectangle r = new Rectangle();
					r.x = (int)(p[0] * xscale);
					r.y = (int)(p[1] * yscale);
					r.width = (int)((p[0] + 1) * xscale) - r.x;
					r.height = (int)((p[1] + 1) * yscale) - r.y;
					g2.fill(r);
				}
			}
		}

		if(hpath.getPathType() != null) {
			c = ttcr.colors.provideColor(hpath.getPathType().inactive());

			for(List<long[]> path : hpath.getIntPaths()) {
				int remaining = path.size();
				for(long[] p : path) {
					remaining--;
					g2.setColor(new Color(
							c.getRed(), 
							c.getGreen(), 
							c.getBlue(), 
							(int)(96 / Math.pow(1 + (System.currentTimeMillis() - p[2]) / 1000. + remaining / 5., 2))));
					g2.fillRect((int)(p[0] * xscale), (int)(p[1] * yscale), (int)xscale, (int)yscale);
				}
			}
		}
	}
	
}
