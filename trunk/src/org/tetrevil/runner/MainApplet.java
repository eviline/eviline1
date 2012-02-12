package org.tetrevil.runner;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
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
import org.tetrevil.swing.TetrevilKeyPanel;
import org.tetrevil.wobj.WebScore;

public class MainApplet extends JApplet {
	private static final long serialVersionUID = 0;
	
	protected Map<String, String> parameters = new HashMap<String, String>();
	
	protected Field field = new Field(true);
	protected TetrevilComponent c;
	protected TetrevilKeyListener kl;
	protected TetrevilKeyPanel kp;
	
	protected JButton start = new JButton("");
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
			kl.LEFT = TetrevilKeyListener.getKeyCode(getParameter("left"));
		if(getParameter("right") != null)
			kl.RIGHT = TetrevilKeyListener.getKeyCode(getParameter("right"));
		if(getParameter("rotate_left") != null)
			kl.ROTATE_LEFT = TetrevilKeyListener.getKeyCode(getParameter("rotate_left"));
		if(getParameter("rotate_right") != null)
			kl.ROTATE_RIGHT = TetrevilKeyListener.getKeyCode(getParameter("rotate_right"));
		if(getParameter("down") != null)
			kl.DOWN = TetrevilKeyListener.getKeyCode(getParameter("down"));
		if(getParameter("drop") != null)
			kl.DROP = TetrevilKeyListener.getKeyCode(getParameter("drop"));
		if(getParameter("reset") != null)
			kl.RESET = TetrevilKeyListener.getKeyCode(getParameter("reset"));
		if(getParameter("pause") != null)
			kl.PAUSE = TetrevilKeyListener.getKeyCode(getParameter("pause"));
		
		
		return kl;
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
			
			if(getParameter("score_host") == null)
				setParameter("score_host", "www.tetrevil.org");

			
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
			});

			field.addTetrevilListener(new TetrevilAdapter() {
				public void gameReset(TetrevilEvent e) {
					setProvider();
				}
				@Override
				public void gameOver(TetrevilEvent e) {
					try {
						WebScore score = new WebScore();
						score.setScore(e.getField().getLines());
						score.setName(kp.getPlayerName());
						score.setTs(new Date());
						WebScore.submit(score, getParameter("score_host"));

						
						WebScore highScore = WebScore.highScore(getParameter("score_host"));
						
						start.setText("<html><center>High Score: " + highScore.getScore() + " by " + highScore.getName() + "<br><br>\n\n" +
								"Controls:<br><br>\n\n" +
								KeyEvent.getKeyText(kl.PAUSE) + ": Pause<br>\n" +
								KeyEvent.getKeyText(kl.RESET) + ": Reset<br>\n" +
								"H: Show this help<br><br>\n\n" +
								"Click to begin.<br><br>\n\n" +
								"&copy;2012 Robin Kirkman</center></html>");
					} catch(Exception ioe) {
						ioe.printStackTrace();
					}
				}
			});
			field.setGhosting(true);
			
			ticker.setRepeats(true);
			start.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					MainApplet.this.remove(start);
					MainApplet.this.remove(kp);
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
			setKeys(kl = new TetrevilKeyListener(field));
			c.getTable().addKeyListener(kl);
			addKeyListener(kl);
			kp = new TetrevilKeyPanel(kl);
			
			WebScore highScore = new WebScore();
			try {
				highScore = WebScore.highScore(getParameter("score_host"));
			} catch(IOException ioe) {
			}
			
			start.setText("<html><center>High Score: " + highScore.getScore() + " by " + highScore.getName() + "<br><br>\n\n" +
					"Controls:<br><br>\n\n" +
					KeyEvent.getKeyText(kl.PAUSE) + ": Pause<br>\n" +
					KeyEvent.getKeyText(kl.RESET) + ": Reset<br>\n" +
					"H: Show this help<br><br>\n\n" +
					"Click to begin.<br><br>\n\n" +
					"&copy;2012 Robin Kirkman</center></html>");

			KeyListener k = new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					if(e.getKeyCode() == KeyEvent.VK_H && !e.isConsumed()) {
						ticker.stop();
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								MainApplet.this.remove(c);
								MainApplet.this.add(kp, BorderLayout.NORTH);
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
			add(kp, BorderLayout.NORTH);
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
