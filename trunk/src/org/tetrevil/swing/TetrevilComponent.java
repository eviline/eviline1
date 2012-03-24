package org.tetrevil.swing;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.tetrevil.Field;

/**
 * A {@link JScrollPane} that holds, and will resize if necessary, a {@link TetrevilTable}
 * @author robin
 *
 */
public class TetrevilComponent extends JScrollPane {
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
		ticker.setInitialDelay(500);
		ticker.setDelay(1000);
	}}
	
	public TetrevilComponent(Field field) {
		super(new TetrevilTable(field), JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		this.field = field;
		table = (TetrevilTable) getViewport().getView();
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				JScrollPane scroll = (JScrollPane) e.getComponent();
				scroll.getViewport().getView().setSize(scroll.getViewport().getExtentSize());
			}
		});
		
		tetrevilKeyListener = new TetrevilKeyListener(field);
		getTable().addKeyListener(tetrevilKeyListener);
		addKeyListener(tetrevilKeyListener);
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
}

