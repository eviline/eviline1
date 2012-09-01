package org.tetrevil.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.tetrevil.Field;
import org.tetrevil.event.TetrevilAdapter;
import org.tetrevil.event.TetrevilEvent;

/**
 * A {@link JScrollPane} that holds, and will resize if necessary, a {@link TetrevilTable}
 * @author robin
 *
 */
public class TetrevilComponent extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Field field;
	protected TetrevilTable table;
	protected TetrevilKeyListener tetrevilKeyListener;
	
	protected Timer ticker = new Timer(1000, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			field.clockTick();
		}
	});
	{{
		ticker.setRepeats(true);
		ticker.setInitialDelay(1000);
		ticker.setDelay(1000);
	}}
	
	public TetrevilComponent(Field f) {
//		super(new TetrevilTable(f), JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		super(new BorderLayout());
		
		setFocusable(true);
		
		JScrollPane scroll = new JScrollPane(new TetrevilTable(f), JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		add(scroll, BorderLayout.CENTER);
		
//		setDoubleBuffered(true);
		this.field = f;
		table = (TetrevilTable) scroll.getViewport().getView();
		scroll.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				JScrollPane scroll = (JScrollPane) e.getComponent();
				scroll.getViewport().getView().setSize(scroll.getViewport().getExtentSize());
			}
		});
		
		tetrevilKeyListener = new TetrevilKeyListener(field);
		getTable().addKeyListener(tetrevilKeyListener);
		scroll.addKeyListener(tetrevilKeyListener);
		addKeyListener(tetrevilKeyListener);
		
		field.addTetrevilListener(new TetrevilAdapter() {
			@Override
			public void clockTicked(TetrevilEvent e) {
				if(field.isGrounded())
					ticker.restart();
			}

			@Override
			public void shiftedLeft(TetrevilEvent e) {
				if(field.isGrounded())
					ticker.restart();
			}

			@Override
			public void shiftedRight(TetrevilEvent e) {
				if(field.isGrounded())
					ticker.restart();
			}

			@Override
			public void rotatedLeft(TetrevilEvent e) {
				if(field.isGrounded())
					ticker.restart();
			}

			@Override
			public void rotatedRight(TetrevilEvent e) {
				if(field.isGrounded())
					ticker.restart();
			}
			@Override
			public void linesCleared(TetrevilEvent e) {
				int level = field.getLines() / 10;
				double fss = Math.pow(0.8 - (level - 1) * 0.007, level - 1);
				ticker.setDelay((int)(1000 * fss));
//				ticker.setInitialDelay(ticker.getDelay());
			}
			@Override
			public void gameReset(TetrevilEvent e) {
				ticker.setDelay(1000);
			}
		});
	}
	
	@Override
	public void setSize(Dimension d) {
		setSize(d.width, d.height);
	}
	
	@Override
	public void setSize(int width, int height) {
		if(width * 2 > height)
			width = height / 2;
		else if(height / 2 > width)
			height = width * 2;
		super.setSize(width, height);
	}
	
	public void start() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if(!getTable().isFocusOwner()) {
					getTable().requestFocusInWindow();
					SwingUtilities.invokeLater(this);
				} else {
					ticker.start();
				}
			}
		});
	}
	
	public void stop() {
		ticker.stop();
	}

	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
		table.setField(field);
	}

	public TetrevilTable getTable() {
		return table;
	}
	
	public TetrevilKeyListener getTetrevilKeyListener() {
		return tetrevilKeyListener;
	}

	public Timer getTicker() {
		return ticker;
	}
}

