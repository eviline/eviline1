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
import javax.swing.SwingConstants;
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
					right.remove(start);
					right.add(difficulty, BorderLayout.CENTER);
					difficulty.revalidate();
					validate();
					repaint();
				}
			}
		});
		provider.setHorizontalAlignment(SwingConstants.RIGHT);
		provider.setFont(provider.getFont().deriveFont(provider.getFont().getSize2D() / 1.125f));
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
		MaliciousRandomizer p = (MaliciousRandomizer) field.getProvider();
		
		final JButton hard = new JButton("Hard");
		final JButton normal = new JButton("Normal");
		final JButton easy = new JButton("Easy");
		
		final JRadioButton malicious = new JRadioButton("Malicious"); malicious.setForeground(Color.WHITE);
		final JRadioButton bag = new JRadioButton("Bag"); bag.setForeground(Color.WHITE);
		ButtonGroup g = new ButtonGroup(); g.add(malicious); g.add(bag);
		final JRadioButton fair = new JRadioButton("Fair"); fair.setForeground(Color.WHITE);
		final JRadioButton unfair = new JRadioButton("Unfair"); unfair.setForeground(Color.WHITE);

		final JTextField depth = new JTextField(new IntegerDocument(), "" + p.getDepth(), 5);
		final JTextField rfactor = new JTextField(new IntegerDocument(), "" + (int)(100 * p.getRfactor()), 5);
		g = new ButtonGroup(); g.add(fair); g.add(unfair);
		final JTextField distribution = new JTextField(new IntegerDocument(), "" + p.getDistribution(), 5);
		
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
		
		hard.addActionListener(new ActionListener() {
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
				distribution.setEnabled(true);
				distribution.setText("3");
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

		GridBagConstraints gc = new GridBagConstraints(0, 0, 2, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);
		JPanel ret = new JPanel(new GridBagLayout());
		ret.setBackground(Color.BLACK);

		JLabel l;

		gc.gridx = 0; gc.gridwidth = 2; ret.add(hard, gc);
		gc.gridx += 2; ret.add(normal, gc);
		gc.gridx += 2; ret.add(easy, gc);
		
		gc.gridy++; gc.gridx = 0; gc.gridwidth = 4; ret.add(malicious, gc);
		gc.gridx += 4; ret.add(bag, gc);

		gc.gridy++; gc.gridx = 0; ret.add(unfair, gc);
		gc.gridx += 4; ret.add(fair, gc);

		gc.gridy++; gc.gridx = 0; ret.add(l = new JLabel("Depth:"), gc); l.setForeground(Color.WHITE); 
		gc.gridx += 4; ret.add(depth, gc);
		
		gc.gridy++; gc.gridx = 0; ret.add(l = new JLabel("Random factor %:"), gc); l.setForeground(Color.WHITE); 
		gc.gridx += 4; ret.add(rfactor, gc);
		
		gc.gridy++; gc.gridx = 0; ret.add(l = new JLabel("dist factor:"), gc); l.setForeground(Color.WHITE); 
		gc.gridx += 4; ret.add(distribution, gc);
		
		gc.gridy++; gc.gridx = 0; gc.gridwidth = 6;
		ret.add(new JButton(new AbstractAction("Set") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
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
				right.add(start, BorderLayout.CENTER);
				start.revalidate();
				validate();
				repaint();
			}
		}), gc);
		
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
			ticker.setInitialDelay(1500);
			ticker.setDelay(1000);
			
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
			
			right.setLayout(new BorderLayout());
			right.add(kp, BorderLayout.NORTH);
			right.add(start, BorderLayout.CENTER);
			right.add(provider, BorderLayout.SOUTH);
			
//			setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
////			add(c);
//			add(right);
			startupLayout();
		}
	};
	
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