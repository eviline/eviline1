package org.tetrevil.runner;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.tetrevil.ConcurrentShapeProvider;
import org.tetrevil.Field;
import org.tetrevil.MaliciousBagRandomizer;
import org.tetrevil.MaliciousRandomizer;
import org.tetrevil.RandomizerFactory;
import org.tetrevil.event.TetrevilAdapter;
import org.tetrevil.event.TetrevilEvent;
import org.tetrevil.swing.IntegerDocument;
import org.tetrevil.swing.TetrevilComponent;
import org.tetrevil.swing.TetrevilKeyListener;
import org.tetrevil.swing.TetrevilKeyPanel;
import org.tetrevil.wobj.WebScore;

public class MainApplet extends JApplet {
	private static final long serialVersionUID = 0;
	
	protected Map<String, String> parameters = new HashMap<String, String>();
	
	protected Field field = new Field(true);
	protected TetrevilComponent c;
	protected JPanel right = new JPanel(new BorderLayout());
	protected TetrevilKeyListener kl;
	protected TetrevilKeyPanel kp;
	
	protected JButton start = new JButton("");
	protected JLabel provider = new JLabel(" ");
	{{
		provider.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(!field.isPlaying() || field.isGameOver()) {
					right.remove(provider);
					right.add(difficulty, BorderLayout.SOUTH);
					difficulty.revalidate();
					validate();
					repaint();
				}
			}
		});
	}}
	
	protected JPanel difficulty = createDifficultyPanel();
	
	protected void setProvider() {
		if(getParameter("distribution") != null)
			field.setProvider(RandomizerFactory.newRandomizer(
					MaliciousRandomizer.DEFAULT_DEPTH,
					Integer.parseInt(getParameter("distribution"))));
		else
			field.setProvider(RandomizerFactory.newRandomizer());

		if(getParameter("depth") != null)
			((MaliciousRandomizer) field.getProvider()).setDepth(Integer.parseInt(getParameter("depth")));
		if(getParameter("rfactor") != null)
			((MaliciousRandomizer) field.getProvider()).setRfactor(Double.parseDouble(getParameter("rfactor")));
		if(getParameter("fair") != null)
			((MaliciousRandomizer) field.getProvider()).setFair(Boolean.parseBoolean(getParameter("fair")));
		
		provider.setText(field.getProvider().toString() + "   [Change]");
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
	
	protected void setStartText() {
		WebScore highScore = new WebScore();
		highScore.setScore(0);
		highScore.setName("[no score for these settings]");
		highScore.setTs(new Date());
		MaliciousRandomizer p = (MaliciousRandomizer) field.getProvider();
		highScore.setDepth(p.getDepth());
		highScore.setRfactor(p.getRfactor());
		highScore.setFair(p.isFair() ? 1 : 0);
		highScore.setDistribution(p.getDistribution());
		highScore.setRandomizer(RandomizerFactory.getClazz().getName());
		try {
			WebScore ws = WebScore.highScore(highScore, getParameter("score_host"));
			if(ws != null)
				highScore = ws;
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		start.setText("<html><center>High Score: " + highScore.getScore() + 
				" by " + highScore.getName() + 
				" on " + df.format(highScore.getTs()) + "<br>\n" +
				"<br>\n" +
				KeyEvent.getKeyText(kl.PAUSE) + ": Pause / " +
				KeyEvent.getKeyText(kl.RESET) + ": Reset / " +
				"H: Settings<br>\n" +
//				"H: Show this help<br>\n" +
				"<br>\n" +
				"Click to begin.<br>\n" +
				"&copy;2012 Robin Kirkman</center></html>");
	}
	
	protected JPanel createDifficultyPanel() {
		JPanel ret = new JPanel(new GridLayout(0, 2));
		ret.setBackground(Color.BLACK);
		MaliciousRandomizer p = (MaliciousRandomizer) field.getProvider();
		
		final JRadioButton stat = new JRadioButton("Default"); stat.setForeground(Color.WHITE);
		final JRadioButton bag = new JRadioButton("Bag"); bag.setForeground(Color.WHITE);

		final JTextField depth = new JTextField(new IntegerDocument(), "" + p.getDepth(), 5);
		final JTextField rfactor = new JTextField(new IntegerDocument(), "" + (int)(100 * p.getRfactor()), 5);
		ButtonGroup g = new ButtonGroup(); g.add(stat); g.add(bag);
		final JTextField distribution = new JTextField(new IntegerDocument(), "" + p.getDistribution(), 5);
		
		if(MaliciousBagRandomizer.class == RandomizerFactory.getClazz())
			bag.setSelected(true);
		else
			stat.setSelected(true);
		
		bag.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				depth.setText("4");
				rfactor.setText("5");
				distribution.setText("5");
			}
		});
		stat.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				depth.setText("3");
				rfactor.setText("5");
				distribution.setText("30");
			}
		});
		
		JLabel l;
		ret.add(stat); ret.add(bag);
		ret.add(l = new JLabel("Depth:")); ret.add(depth); l.setForeground(Color.WHITE);
		ret.add(l = new JLabel("Random factor %:")); ret.add(rfactor); l.setForeground(Color.WHITE);
		ret.add(l = new JLabel("dist factor:")); ret.add(distribution); l.setForeground(Color.WHITE);
		
		ret.add(new JButton(new AbstractAction("Set") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setParameter("depth", depth.getText());
				setParameter("rfactor", "" + (Double.parseDouble(rfactor.getText()) / 100));
				setParameter("distribution", distribution.getText());
				if(bag.isSelected())
					RandomizerFactory.setClazz(MaliciousBagRandomizer.class);
				else
					RandomizerFactory.setClazz(MaliciousRandomizer.class);
				setProvider();
				setStartText();
			}
		}));
		ret.add(new JButton(new AbstractAction("Hide") {
			@Override
			public void actionPerformed(ActionEvent e) {
				right.remove(difficulty);
				right.add(provider, BorderLayout.SOUTH);
				provider.revalidate();
				validate();
				repaint();
			}
		}));
		
		return ret;
	}
	
	protected void toggleSettings() {
		if(right.isVisible()) {
			remove(c);
			remove(right);
			right.setVisible(false);
			setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
			c.setPreferredSize(new Dimension(260, 500));
			c.setMaximumSize(c.getPreferredSize());
			add(c);
		} else {
			remove(c);
			setLayout(new GridLayout(0, 2));
			add(c);
			add(right);
			right.setVisible(true);
		}
		validate();
		repaint();
		c.getTable().requestFocusInWindow();
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
						MaliciousRandomizer p = (MaliciousRandomizer) e.getField().getProvider();
						score.setDepth(p.getDepth());
						score.setRfactor(p.getRfactor());
						score.setFair(p.isFair() ? 1 : 0);
						score.setDistribution(p.getDistribution());
						score.setRandomizer(p.getClass().getName());
						WebScore.submit(score, getParameter("score_host"));
						
						setStartText();
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
					right.remove(difficulty);
					right.add(provider, BorderLayout.SOUTH);
					toggleSettings();
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
			
			
			setStartText();

			KeyListener k = new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					if(e.getKeyCode() == KeyEvent.VK_H && !e.isConsumed()) {
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								toggleSettings();
							}
						});
						e.consume();
					}
				}
			};
			c.getTable().addKeyListener(k);
			c.addKeyListener(k);
			addKeyListener(k);
			
			setBackground(Color.BLACK);
			right.setBackground(Color.BLACK);
			
			right.setLayout(new BorderLayout());
			right.add(kp, BorderLayout.NORTH);
			right.add(start, BorderLayout.CENTER);
			right.add(provider, BorderLayout.SOUTH);
			
			setLayout(new GridLayout(0, 2));
			add(c);
			add(right);
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
