package org.tetrevil.runner;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;

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
	{{
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
			}
			@Override
			public void gameReset(TetrevilEvent e) {
				ticker.setDelay(1000);
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
					
					if(!right.isVisible())
						toggleSettings();
				} catch(Exception ioe) {
					ioe.printStackTrace();
				}
			}
		});
		field.setGhosting(true);
	}}
	protected TetrevilComponent c;
	protected JPanel right;
	protected TetrevilKeyListener kl;
	protected TetrevilKeyPanel kp;
	protected JPanel controls;
	
	protected JButton start;
	protected String provText = "Aggressive";
	protected JButton provider;
	
	protected JPanel difficulty;
	
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
		
		provider.setText("Settings: " + provText);
	}
	
	protected void saveKeys() {
		setParameter("left", KeyEvent.getKeyText(kl.LEFT));
		setParameter("right", KeyEvent.getKeyText(kl.RIGHT));
		setParameter("rotate_left", KeyEvent.getKeyText(kl.ROTATE_LEFT));
		setParameter("rotate_right", KeyEvent.getKeyText(kl.ROTATE_RIGHT));
		setParameter("down", KeyEvent.getKeyText(kl.DOWN));
		setParameter("drop", KeyEvent.getKeyText(kl.DROP));
		setParameter("das_time", "" + kl.DAS_TIME);
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
		if(getParameter("das_time") != null)
			kl.DAS_TIME = Integer.parseInt(getParameter("das_time"));
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
				"Click to begin.<br><br>\n\n" +
				"&copy;2012 Robin Kirkman</center></html>");
	}
	
	protected JPanel createDifficultyPanel() {
		MaliciousRandomizer p = (MaliciousRandomizer) field.getProvider();
		
		final JButton evil = new JButton("Evil");
		final JButton normal = new JButton("Aggressive");
		final JButton easy = new JButton("Rude");
		
		final JRadioButton malicious = new JRadioButton("Malicious"); malicious.setForeground(Color.WHITE); malicious.setPreferredSize(new Dimension(80, malicious.getPreferredSize().height));
		final JRadioButton bag = new JRadioButton("Bag"); bag.setForeground(Color.WHITE); bag.setPreferredSize(new Dimension(80, bag.getPreferredSize().height));
		ButtonGroup g = new ButtonGroup(); g.add(malicious); g.add(bag);
		
		final JRadioButton fair = new JRadioButton("Fair"); fair.setForeground(Color.WHITE); fair.setPreferredSize(new Dimension(80, fair.getPreferredSize().height));
		final JRadioButton unfair = new JRadioButton("Unfair"); unfair.setForeground(Color.WHITE); unfair.setPreferredSize(new Dimension(80, unfair.getPreferredSize().height));
		g = new ButtonGroup(); g.add(fair); g.add(unfair);

		final JTextField depth = new JTextField(new IntegerDocument(), "" + p.getDepth(), 5);
		final JTextField rfactor = new JTextField(new IntegerDocument(), "" + (int)(100 * p.getRfactor()), 5);
		final JTextField distribution = new JTextField(new IntegerDocument(), "" + p.getDistribution(), 5);
		
		final JButton set = new JButton(new AbstractAction("Set") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				provText = "Custom";
				setParameter("depth", depth.getText());
				setParameter("rfactor", "" + (Double.parseDouble(rfactor.getText()) / 100));
				setParameter("distribution", distribution.getText());
				setParameter("fair", "" + fair.isSelected());
				if(bag.isSelected())
					RandomizerFactory.setClazz(MaliciousBagRandomizer.class);
				else
					RandomizerFactory.setClazz(MaliciousRandomizer.class);
				setProvider();
				setStartText();
				right.remove(difficulty);
				right.add(start, new GridBagConstraints(0, 1, 2, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0));
				start.revalidate();
				validate();
				repaint();
			}
		});
		
		if(MaliciousBagRandomizer.class == RandomizerFactory.getClazz()) {
			bag.setSelected(true);
			fair.setEnabled(false);
			unfair.setEnabled(false);
			fair.setSelected(true);
		} else {
			malicious.setSelected(true);
			fair.setEnabled(true);
			unfair.setEnabled(true);
			fair.setSelected(p.isFair());
			unfair.setSelected(!p.isFair());
		}
		
		evil.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				malicious.setSelected(true);
				depth.setText("3");
				rfactor.setText("5");
				fair.setEnabled(true);
				unfair.setEnabled(true);
				unfair.setSelected(true);
				distribution.setEnabled(false);
				distribution.setText("30");
				set.doClick();
				provText = "Evil";
				setProvider();
			}
		});
		
		normal.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				malicious.setSelected(true);
				depth.setText("3");
				rfactor.setText("5");
				fair.setEnabled(true);
				unfair.setEnabled(true);
				fair.setSelected(true);
				distribution.setEnabled(true);
				distribution.setText("30");
				set.doClick();
				provText = "Aggressive";
				setProvider();
			}
		});
		
		easy.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				bag.setSelected(true);
				depth.setText("4");
				rfactor.setText("5");
				fair.setEnabled(false);
				unfair.setEnabled(false);
				fair.setSelected(true);
				distribution.setEnabled(true);
				distribution.setText("3");
				set.doClick();
				provText = "Rude";
				setProvider();
			}
		});
		
		malicious.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fair.setEnabled(true);
				unfair.setEnabled(true);
			}
		});
		
		bag.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fair.setEnabled(false);
				unfair.setEnabled(false);
			}
		});
				
		fair.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				distribution.setEnabled(fair.isSelected());
			}
		});
		
		unfair.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				distribution.setEnabled(!unfair.isSelected());

			}
		});

		JPanel ret = new JPanel(new GridBagLayout()); ret.setBackground(Color.BLACK);
		GridBagConstraints c = new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
		
		JPanel presets = new JPanel(new GridBagLayout()); presets.setBackground(Color.BLACK);
		presets.add(evil, c);
		c.gridx++; presets.add(normal, c);
		c.gridx++; presets.add(easy, c);
		
		c.gridx = 0;  ret.add(presets, c);
		
		JLabel l;
		JPanel details = new JPanel(new GridLayout(0, 2)); details.setBackground(Color.BLACK);
		
		details.add(l = new JLabel("Randomizer:")); details.add(malicious); l.setForeground(Color.WHITE);
		details.add(new JLabel("")); details.add(bag);
		
		details.add(l = new JLabel("Distribution:")); details.add(unfair); l.setForeground(Color.WHITE);
		details.add(new JLabel("")); details.add(fair);
		
		details.add(l = new JLabel("Depth:")); details.add(depth); l.setForeground(Color.WHITE);
		details.add(l = new JLabel("Random Factor %:")); details.add(rfactor); l.setForeground(Color.WHITE);
		
		details.add(l = new JLabel("Dist factor:")); details.add(distribution); l.setForeground(Color.WHITE);
		c.gridy++; c.weighty = 1; ret.add(details, c);

		c.gridy++; c.weighty = 0; ret.add(set, c);
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				normal.doClick();
			}
		});
		
		return ret;
	}
	
	protected JPanel createControlsPanel() {
		final JPanel ret = new JPanel(new BorderLayout());
		ret.setBackground(Color.BLACK);
		
		JToggleButton b;
		ret.add(b = new JToggleButton(new AbstractAction("Controls & Player Name") {
			private static final long serialVersionUID = 0;
			@Override
			public void actionPerformed(ActionEvent e) {
				JToggleButton b = (JToggleButton) e.getSource();
				if(b.isSelected()) {
					ret.add(kp, BorderLayout.CENTER);
					validate();
					repaint();
				} else {
					ret.remove(kp);
					validate();
					repaint();
				}
			}
		}), BorderLayout.NORTH);
		
		ret.add(kp, BorderLayout.CENTER);
		b.setSelected(true);
		
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
	
	protected void startupLayout() {
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints(0, 0, 1, 1, 0, 1, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0);
		add(right, c);
	}
	
	protected ActionListener tick = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			field.clockTick();
		}
	};
	protected Timer ticker = new Timer(1000, tick);
	{{
		ticker.setRepeats(true);
		ticker.setInitialDelay(500);
		ticker.setDelay(1000);
	}}
	@Override
	public void init() {
		if(!SwingUtilities.isEventDispatchThread()) {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					@Override
					public void run() {
						init();
					}
				});
			} catch(Exception ex) {
			}
			return;
		}
//		field.setProvider(new ConcurrentShapeProvider(field, field.getProvider()));
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		
		getContentPane().setBackground(Color.BLACK);
		
		right = new JPanel(new BorderLayout());

		start = new JButton("");
		start.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveKeys();
				right.remove(difficulty);
				right.add(start, new GridBagConstraints(0, 1, 2, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0));
				toggleSettings();
				if(field.isPaused())
					field.setPaused(false);
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

		provider = new JButton("Settings");
		provider.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!field.isPlaying() || field.isGameOver()) {
					right.remove(start);
					right.add(difficulty, new GridBagConstraints(0, 1, 2, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0));
					difficulty.revalidate();
					validate();
					repaint();
				}
			}
		});
		setProvider();
				
		if(getParameter("score_host") == null)
			setParameter("score_host", "www.tetrevil.org");

		
		
		
		
		c = new TetrevilComponent(field);
		c.getTable().setFocusable(true);
		setKeys(kl = new TetrevilKeyListener(field));
		c.getTable().addKeyListener(kl);
		addKeyListener(kl);
		kp = new TetrevilKeyPanel(kl);
		
		difficulty = createDifficultyPanel();
		controls = createControlsPanel();
		
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
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						if(field.getShape() == null) {
							ticker.stop();
							field.clockTick();
							repaint();
							ticker.start();
						}
					}
				});
			}
		};
		c.getTable().addKeyListener(k);
		c.addKeyListener(k);
		addKeyListener(k);
		
		setBackground(Color.BLACK);
		right.setBackground(Color.BLACK);
		right.setPreferredSize(new Dimension(260,500));
		right.setMaximumSize(right.getPreferredSize());
		
		right.setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints(0, 0, 2, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
		right.add(controls, gc);
		gc.gridy++; gc.weighty = 1; right.add(start, gc);
		gc.gridy++; gc.weighty = 0; right.add(provider, gc);
		
//		setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
////		add(c);
//		add(right);
		startupLayout();
	}
	
	@Override
	public void stop() {
		ticker.stop();
	}
	
	@Override
	public String getParameter(String name) {
		if(parameters.containsKey(name))
			return parameters.get(name);
		String ret = null;
		try {
			ret = super.getParameter(name);
		} catch(NullPointerException npe) {
		}
		return ret;
	}
	
	public void setParameter(String name, String value) {
		parameters.put(name, value);
	}
}