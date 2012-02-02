package org.tetrevil.runner;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
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
	
	protected Map<String, String> parameters = new HashMap<String, String>();
	
	protected Field field = new Field(true);
	protected TetrevilComponent c;
	protected JButton start = new JButton("<html><center>Controls:<br><br>\n\n" +
			"LEFT: Shift left 1<br>\n" +
			"HOLD LEFT: Shift left all the way<br>\n" +
			"RIGHT: Shift right 1<br>\n" +
			"HOLD RIGHT: Shift right all the way<br>\n" +
			"A: Rotate left<br>\n" +
			"D: Rotate right<br>\n" +
			"DOWN: Shift down 1<br>\n" +
			"UP: Drop<br>\n" +
			"P: Pause<br>\n" +
			"R: Reset<br>\n" +
			"H: Show this help<br><br>\n\n" +
			"Click to begin.</center></html>");
	protected JLabel provider = new JLabel(" ");
	
	protected void setProvider() {
		field.setProvider(new MaliciousShapeProvider());

		if(getParameter("depth") != null)
			((MaliciousShapeProvider) field.getProvider()).setDepth(Integer.parseInt(getParameter("depth")));
		if(getParameter("rfactor") != null)
			((MaliciousShapeProvider) field.getProvider()).setRfactor(Double.parseDouble(getParameter("rfactor")));
		if(getParameter("fair") != null)
			((MaliciousShapeProvider) field.getProvider()).setFair(Boolean.parseBoolean(getParameter("fair")));
		
		provider.setText(field.getProvider().toString());
	}
	
	protected TetrevilKeyListener setKeys(TetrevilKeyListener kl) {
		if(getParameter("left") != null)
			kl.LEFT = getKeyCode(getParameter("left"));
		if(getParameter("right") != null)
			kl.RIGHT = getKeyCode(getParameter("right"));
		if(getParameter("rotate_left") != null)
			kl.ROTATE_LEFT = getKeyCode(getParameter("rotate_left"));
		if(getParameter("rotate_right") != null)
			kl.ROTATE_RIGHT = getKeyCode(getParameter("rotate_right"));
		if(getParameter("down") != null)
			kl.DOWN = getKeyCode(getParameter("down"));
		if(getParameter("drop") != null)
			kl.DROP = getKeyCode(getParameter("drop"));
		if(getParameter("reset") != null)
			kl.RESET = getKeyCode(getParameter("reset"));
		if(getParameter("pause") != null)
			kl.PAUSE = getKeyCode(getParameter("pause"));
		
		start.setText("<html><center>Controls:<br><br>\n\n" +
				KeyEvent.getKeyText(kl.LEFT) + ": Shift left 1<br>\n" +
				"HOLD " + KeyEvent.getKeyText(kl.LEFT) + ": Shift left all the way<br>\n" +
				KeyEvent.getKeyText(kl.RIGHT) + ": Shift right 1<br>\n" +
				"HOLD " + KeyEvent.getKeyText(kl.RIGHT) + ": Shift right all the way<br>\n" +
				KeyEvent.getKeyText(kl.ROTATE_LEFT) + ": Rotate left<br>\n" +
				KeyEvent.getKeyText(kl.ROTATE_RIGHT) + ": Rotate right<br>\n" +
				KeyEvent.getKeyText(kl.DOWN) + ": Shift down 1<br>\n" +
				KeyEvent.getKeyText(kl.DROP) + ": Drop<br>\n" +
				KeyEvent.getKeyText(kl.PAUSE) + ": Pause<br>\n" +
				KeyEvent.getKeyText(kl.RESET) + ": Reset<br>\n" +
				"H: Show this help<br><br>\n\n" +
				"Click to begin.</center></html>");
		
		return kl;
	}
	
	protected int getKeyCode(String code) {
		code = code.toUpperCase();
		try {
			java.lang.reflect.Field kf = KeyEvent.class.getField("VK_" + code);
			return (Integer) kf.get(null);
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
	protected Runnable launch = new Runnable() {
		@Override
		public void run() {
//			field.setProvider(new ConcurrentShapeProvider(field, field.getProvider()));
			
			getContentPane().setBackground(Color.BLACK);
			
			provider.setBackground(Color.BLACK);
			provider.setForeground(Color.WHITE);
			provider.setOpaque(true);
			
			setProvider();
			
			field.addTetrevilListener(new TetrevilAdapter() {
				public void gameReset(TetrevilEvent e) {
					setProvider();
				}
			});
			
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
			KeyAdapter k = setKeys(new TetrevilKeyListener(field));
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
			
			setBackground(Color.BLACK);
			setLayout(new BorderLayout());
			add(start, BorderLayout.CENTER);
			add(provider, BorderLayout.SOUTH);
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
			ex.printStackTrace();
		}
	}
	
	@Override
	public void stop() {
		ticker.stop();
	}
	
	@Override
	public String getParameter(String name) {
		if(parameters.containsKey(name))
			return parameters.get(name);
		else {
			try {
				return super.getParameter(name);
			} catch(NullPointerException npe) {
				return null;
			}
		}
	}
	
	public void setParameter(String name, String value) {
		parameters.put(name, value);
	}
}
