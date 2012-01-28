package org.tetrevil.runner;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

import javax.swing.JApplet;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.tetrevil.EvilShapeProvider;
import org.tetrevil.Field;
import org.tetrevil.event.TetrevilAdapter;
import org.tetrevil.event.TetrevilEvent;
import org.tetrevil.swing.TetrevilComponent;
import org.tetrevil.swing.TetrevilTable;

public class MainApplet extends JApplet {
	protected Field field = new Field();
	TetrevilComponent c;
	
	protected Runnable launch = new Runnable() {
		@Override
		public void run() {
			field.setProvider(new EvilShapeProvider(2));
			
			c = new TetrevilComponent(field);
			c.getTable().setFocusable(true);
			KeyAdapter k;
			c.getTable().addKeyListener(k = new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					if(e.isConsumed())
						return;
					Field f = ((TetrevilTable) e.getComponent()).getField();
					if(e.getKeyCode() == KeyEvent.VK_LEFT)
						f.shiftLeft();
					else if(e.getKeyCode() == KeyEvent.VK_RIGHT)
						f.shiftRight();
					else if(e.getKeyCode() == KeyEvent.VK_UP)
						f.rotateLeft();
					else if(e.getKeyCode() == KeyEvent.VK_DOWN)
						f.rotateRight();
					else if(e.getKeyCode() == KeyEvent.VK_ENTER) {
						while(f.getShape() != null) {
							f.clockTick();
						}
					} else if(e.getKeyCode() == KeyEvent.VK_SPACE) {
						f.clockTick();
					} else if(e.getKeyCode() == KeyEvent.VK_R)
						f.reset();
					else if(e.getKeyCode() == KeyEvent.VK_P)
						f.setPaused(!f.isPaused());
					else
						return;
					e.consume();
				}
			});
			
			field.addTetrevilListener(new TetrevilAdapter() {
				public void gameOver(TetrevilEvent e) {
					e.getField().reset();
				}
			});

			addKeyListener(k);
			setLayout(new BorderLayout());
			add(c, BorderLayout.CENTER);
		}
	};
	
	protected Runnable tick = new Runnable() {
		@Override
		public void run() {
			try {
				while(!future.isCancelled() && !future.isDone()) {
					Thread.sleep(1000);
					field.clockTick();
				}
			} catch(InterruptedException ie) {
			}
		}
	};
	
	protected RunnableFuture<?> future = new FutureTask<Object>(tick, null);
	
	@Override
	public void init() {
		try {
			SwingUtilities.invokeAndWait(launch);
		} catch(Exception ex) {
		}
	}
	
	@Override
	public void start() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JOptionPane.showMessageDialog(c, "Controls:\n\n" +
						"LEFT: Shift left 1\n" +
						"RIGHT: Shift right 1\n" +
						"UP: Rotate left\n" +
						"DOWN: Rotate right 1\n" +
						"SPACE: Shift down 1\n" +
						"ENTER: Drop\n" +
						"P: Pause\n" +
						"R: Reset");
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						if(!c.getTable().isFocusOwner()) {
							c.getTable().requestFocusInWindow();
							SwingUtilities.invokeLater(this);
						} else
							new Thread(future).start();
					}
				});
			}
		});
	}
	
	@Override
	public void stop() {
		future.cancel(true);
	}
}
