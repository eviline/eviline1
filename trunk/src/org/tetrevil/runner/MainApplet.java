package org.tetrevil.runner;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.tetrevil.ConcurrentShapeProvider;
import org.tetrevil.Field;
import org.tetrevil.MaliciousShapeProvider;
import org.tetrevil.event.TetrevilAdapter;
import org.tetrevil.event.TetrevilEvent;
import org.tetrevil.swing.TetrevilComponent;
import org.tetrevil.swing.TetrevilKeyListener;

public class MainApplet extends JApplet {
	private static final long serialVersionUID = 0;
	
	protected Field field = new Field(true);
	protected TetrevilComponent c;
	protected JButton start = new JButton("<html><center>Controls:<br><br>\n\n" +
			"LEFT: Shift left 1<br>\n" +
			"HOLD LEFT: Shift left all the way<br>\n" +
			"RIGHT: Shift right 1<br>\n" +
			"HOLD RIGHT: Shift right all the way<br>\n" +
			"UP: Rotate left<br>\n" +
			"DOWN: Rotate right<br>\n" +
			"SPACE: Shift down 1<br>\n" +
			"ENTER: Drop<br>\n" +
			"P: Pause<br>\n" +
			"R: Reset<br>\n" +
			"H: Show this help<br><br>\n\n" +
			"Click to begin.</center></html>");
	
	protected Runnable launch = new Runnable() {
		@Override
		public void run() {
//			field.setProvider(new ConcurrentShapeProvider(field, field.getProvider()));
			
			ticker.setRepeats(true);
			start.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					MainApplet.this.remove(start);
					MainApplet.this.add(c, BorderLayout.CENTER);
					MainApplet.this.validate();
					MainApplet.this.repaint();
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							if(!c.getTable().isFocusOwner()) {
								c.getTable().requestFocusInWindow();
								SwingUtilities.invokeLater(this);
							} else {
								ticker.start();
							}
						}
					});
				}
			});
			
			c = new TetrevilComponent(field);
			c.getTable().setFocusable(true);
			KeyAdapter k = new TetrevilKeyListener(field);
			c.getTable().addKeyListener(k);
			addKeyListener(k);
			
			k = new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					if(e.getKeyCode() == KeyEvent.VK_H && !e.isConsumed()) {
						ticker.stop();
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								MainApplet.this.remove(c);
								MainApplet.this.add(start, BorderLayout.CENTER);
								MainApplet.this.validate();
								MainApplet.this.repaint();
							}
						});
						e.consume();
					}
				}
			};
			c.getTable().addKeyListener(k);
			addKeyListener(k);
			
			field.addTetrevilListener(new TetrevilAdapter() {
				@Override
				public void clockTicked(TetrevilEvent e) {
					MainApplet.this.repaint();
				}
			});
			
			setBackground(Color.BLACK);
			setLayout(new BorderLayout());
			add(start, BorderLayout.CENTER);
		}
	};
	
	protected ActionListener tick = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			field.clockTick();
		}
	};
	protected Timer ticker = new Timer(1000, tick);
	
	@Override
	public void init() {
		try {
			SwingUtilities.invokeAndWait(launch);
		} catch(Exception ex) {
		}
	}
	
	@Override
	public void stop() {
		ticker.stop();
	}
}
