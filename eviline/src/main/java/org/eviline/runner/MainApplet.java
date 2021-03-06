package org.eviline.runner;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;

import org.eviline.Field;
import org.eviline.PropertySource;
import org.eviline.Version;
import org.eviline.ai.AI;
import org.eviline.ai.DefaultAIKernel;
//import org.eviline.clj.ClojureAIKernel;
import org.eviline.event.EvilineAdapter;
import org.eviline.event.EvilineEvent;
import org.eviline.fitness.AbstractFitness;
import org.eviline.fitness.WrapperFitness;
import org.eviline.sounds.TetrevilMusicListener;
import org.eviline.sounds.TetrevilSoundListener;
import org.eviline.sounds.TetrevilSounds;
import org.eviline.swing.DifficultyPanel;
import org.eviline.swing.TetrevilComponent;
import org.eviline.swing.TetrevilKeyListener;
import org.eviline.swing.TetrevilKeyPanel;
import org.eviline.wobj.CookieAccess;
import org.eviline.wobj.WebScore;

public class MainApplet extends JApplet implements PropertySource {
	private static final long serialVersionUID = 0;
	
	protected ExecutorService threadPool = Executors.newCachedThreadPool();
	
	protected Map<String, String> parameters = new HashMap<String, String>();
	
	protected Icon gear = new ImageIcon(getClass().getClassLoader().getResource("org/eviline/images/gear.png"));

	
	protected Field field = new Field();
	{{
		field.addEvilineListener(new EvilineAdapter() {
			@Override
			public void gameReset(EvilineEvent e) {
				if(!field.isGameOver())
					submitScore("Reset");
				setProvider();
			}
			@Override
			public void gameOver(EvilineEvent e) {
				try {
					submitScore("Game Over");
					
					setStartText();
					
					if(!right.isVisible())
						toggleSettings();
				} catch(Exception ioe) {
					ioe.printStackTrace();
				}
				if(!right.isVisible())
					toggleSettings();
			}
			@Override
			public void shapeLocked(EvilineEvent e) {
//				SwingUtilities.invokeLater(new Runnable() {
//				@Override
//				public void run() {
//					if(field.getShape() == null) {
//						final Timer ticker = c.getTicker();
//						ticker.stop();
//						ticker.setInitialDelay(ticker.getDelay());
//						threadPool.execute(new Runnable() {
//							@Override
//							public void run() {
//								field.clockTick();
//								ticker.start();
//							}
//						});
//					}
//				}
//			});
				final Timer ticker = c.getTicker();
				ticker.stop();
				ticker.setInitialDelay(ticker.getDelay());
				ticker.start();
//				threadPool.execute(new Runnable() {
//					@Override
//					public void run() {
//						field.clockTick();
//					}
//				});
//				field.clockTick();
			}
		});
		field.setGhosting(true);
	}}
	protected boolean soundEnabled;
	protected TetrevilSoundListener soundListener = new TetrevilSoundListener();
	protected boolean musicEnabled;
	protected TetrevilMusicListener musicListener = new TetrevilMusicListener();
	protected TetrevilComponent c;
	protected JPanel right;
	protected TetrevilKeyListener kl;
	protected TetrevilKeyPanel kp;
	protected JPanel controls;
	
	protected JButton start;
	protected String provText = "Aggressive";
	protected JToggleButton provider;
	
	protected JPanel difficulty;
	
	protected void setCookies() {
		Map<String, String> cookies = CookieAccess.get(this);
		
		cookies.put("player_name", kp.getPlayerName());
		int[] controls = new int[] {
				kl.LEFT, kl.RIGHT, kl.ROTATE_LEFT, kl.ROTATE_RIGHT, kl.DOWN, kl.DROP, kl.DAS_TIME, kl.DOWN_TIME, soundEnabled ? 1 : 0, musicEnabled ? 1 : 0
		};
		cookies.put("controls", Arrays.toString(controls));
		
		CookieAccess.set(this, cookies);
	}
	
	protected void loadCookies() {
		try {
			Map<String, String> cookies = CookieAccess.get(this);

			String playerName = cookies.get("player_name");
			if(playerName == null)
				playerName = "web user";

			kp.setPlayerName(playerName);

			String ia = cookies.get("controls");
			if(ia != null) {
				ia = ia.substring(1, ia.length() - 1);
				String[] controls = ia.split(", ");
				kl.LEFT = Integer.parseInt(controls[0]);
				kl.RIGHT = Integer.parseInt(controls[1]);
				kl.ROTATE_LEFT = Integer.parseInt(controls[2]);
				kl.ROTATE_RIGHT = Integer.parseInt(controls[3]);
				kl.DOWN = Integer.parseInt(controls[4]);
				kl.DROP = Integer.parseInt(controls[5]);
				kl.DAS_TIME = Integer.parseInt(controls[6]);
				kl.DOWN_TIME = Integer.parseInt(controls[7]);
				if(controls.length >= 9)
					soundEnabled = Integer.parseInt(controls[8]) != 0;
				else
					soundEnabled = true;
				if(controls.length >= 10)
					musicEnabled = Integer.parseInt(controls[9]) != 0;
				else
					musicEnabled = true;
			}

			kp.update();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	protected void submitScore(String reason) {
		setCookies();
		if(!field.isPlaying() && !field.isGameOver())
			return;
		try {
			WebScore score = new WebScore(field);
			score.setName(kp.getPlayerName());
			score.setReason(reason);
			score.submit(getParameter("score_host"));
		} catch(Exception ioe) {
			ioe.printStackTrace();
		}
	}
	
	protected void setProvider() {
//		field.setProvider(new RandomizerFactory().newRandomizer(this));
		provider.setText("Difficulty Settings: " + provText);
	}
	
	protected void saveKeys() {
		setParameter("left", KeyEvent.getKeyText(kl.LEFT));
		setParameter("right", KeyEvent.getKeyText(kl.RIGHT));
		setParameter("rotate_left", KeyEvent.getKeyText(kl.ROTATE_LEFT));
		setParameter("rotate_right", KeyEvent.getKeyText(kl.ROTATE_RIGHT));
		setParameter("down", KeyEvent.getKeyText(kl.DOWN));
		setParameter("drop", KeyEvent.getKeyText(kl.DROP));
		setParameter("das_time", "" + kl.DAS_TIME);
		setParameter("down_time", "" + kl.DOWN_TIME);
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
		if(getParameter("down_time") != null)
			kl.DOWN_TIME = Integer.parseInt(getParameter("down_time"));
		if(getParameter("reset") != null)
			kl.RESET = TetrevilKeyListener.getKeyCode(getParameter("reset"));
		if(getParameter("pause") != null)
			kl.PAUSE = TetrevilKeyListener.getKeyCode(getParameter("pause"));
		
		
		return kl;
	}
	
	protected void setStartText() {
		WebScore highScore = new WebScore(field);
		highScore.setScore(0);
		highScore.setName("[no score for these settings]");
		try {
			WebScore ws = highScore.highScore(getParameter("score_host"));
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
				"&copy;2012 Robin Kirkman<br>\n" +
				"Version " + Version.getVersion() + "</center></html>");
	}
	
	protected JPanel createDifficultyPanel() {
		final DifficultyPanel ret = new DifficultyPanel(field, this, false);

		ret.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				provText = ret.getProvText();
				setProvider();
				setStartText();
			}
		});
		
		provText = ret.getProvText();
		setProvider();
		setStartText();
		
		return ret;
	}
	
	protected JPanel createControlsPanel() {
		final JPanel ret = new JPanel(new BorderLayout());
		ret.setBackground(Color.WHITE);
		
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
		b.setIcon(gear);
		b.setSelectedIcon(gear);
		
		JCheckBox sound = new JCheckBox("Sound");
		sound.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent l) {
				JCheckBox sound = (JCheckBox) l.getSource();
				if(soundEnabled = sound.isSelected())
					field.addEvilineListener(soundListener);
				else
					field.removeEvilineListener(soundListener);
				setParameter("sounds", "" + soundEnabled);
			}
		});
		sound.setBackground(Color.WHITE);
		
		JCheckBox music = new JCheckBox("Music");
		music.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent l) {
				JCheckBox sound = (JCheckBox) l.getSource();
				if(musicEnabled = sound.isSelected()) {
					field.addEvilineListener(musicListener);
				} else {
					field.removeEvilineListener(musicListener);
				}
				setParameter("music", "" + musicEnabled);
			}
		});
		music.setBackground(Color.WHITE);
		
		JPanel p = new JPanel(new GridLayout(0, 2));
		p.setBackground(Color.WHITE);
		p.add(music);
		p.add(sound);
		kp.add(p);
		
		ret.add(kp, BorderLayout.CENTER);
		b.setSelected(true);
		b.doClick();
		
		loadCookies();
		if(soundEnabled)
			sound.doClick();
		if(musicEnabled)
			music.doClick();
		
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
			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					Dimension cd = c.getSize();
					Dimension rd = right.getSize();
					double factor = ((double) rd.height) / ((double) cd.height);
					cd.height = rd.height;
					cd.width *= factor;
					c.setPreferredSize(cd);
					c.revalidate();
				}
			});
//			setLayout(new FlowLayout());
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
	
//	protected ActionListener tick = new ActionListener() {
//		@Override
//		public void actionPerformed(ActionEvent arg0) {
//			field.clockTick();
//		}
//	};
//	protected Timer ticker = new Timer(1000, tick);
//	{{
//		ticker.setRepeats(true);
//		ticker.setInitialDelay(500);
//		ticker.setDelay(1000);
//	}}
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
				ex.printStackTrace();
			}
			return;
		}
//		field.setProvider(new ConcurrentShapeProvider(field, field.getProvider()));
		
//		// FIXME: Shouldn't normally be using ClojureFitness
//		if(System.getProperty("eviline.clojure") != null) {
////			AbstractFitness.setDefaultInstance(new WrapperFitness(new ClojureFitness()));
//			AI.setInstance(new ClojureAIKernel(AbstractFitness.getDefaultInstance()));
//		}
		
//		AI.setInstance(new ClojureAIKernel());
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		getContentPane().setBackground(new Color(224, 255, 224));
		if(getContentPane() instanceof JComponent)
			((JComponent) getContentPane()).setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		right = new JPanel(new BorderLayout());

		start = new JButton("");
		start.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveKeys();
				right.remove(difficulty);
				right.add(start, new GridBagConstraints(0, 1, 2, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0));
				toggleSettings();
				if(field.isGameOver())
					field.reset();
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
							c.start();
						}
					}
				});
			}
		});

		provider = new JToggleButton("Settings", gear);
		provider.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(provider.isSelected() && (!field.isPlaying() || field.isGameOver())) {
					right.remove(start);
					right.add(difficulty, new GridBagConstraints(0, 1, 2, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0));
					difficulty.revalidate();
					validate();
					repaint();
				} else {
					right.remove(difficulty);
					right.add(start, new GridBagConstraints(0, 1, 2, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0));
					start.revalidate();
					validate();
					repaint();
				}
			}
		});
		setProvider();
				
		if(getParameter("score_host") == null)
			setParameter("score_host", "www.eviline.org:8080");

		
		
		
		
		c = new TetrevilComponent(field);
		c.getTable().setFocusable(true);
		
		JToolBar buttons = new JToolBar();
		buttons.setFloatable(false);
		buttons.add(new AbstractAction("Pause") {
			@Override
			public void actionPerformed(ActionEvent e) {
				field.setPaused(true);
				c.requestFocus();
			}
		});
		buttons.add(new AbstractAction("Resume") {
			@Override
			public void actionPerformed(ActionEvent e) {
				field.setPaused(false);
				c.requestFocus();
			}
		});
		buttons.add(new AbstractAction("Reset") {
			@Override
			public void actionPerformed(ActionEvent e) {
				field.reset();
				c.requestFocus();
			}
		});
		buttons.add(new AbstractAction("Settings") {
			@Override
			public void actionPerformed(ActionEvent e) {
				toggleSettings();
				c.requestFocus();
			}
		});
		c.add(buttons, BorderLayout.SOUTH);
		
		setKeys(kl = c.getTetrevilKeyListener());
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
			}
		};
		c.getTable().addKeyListener(k);
		c.addKeyListener(k);
		addKeyListener(k);
		
		setBackground(new Color(224,255,224));
		right.setBackground(new Color(224,255,224));
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
		c.stop();
		if(!field.isGameOver())
			submitScore("Quit");
		try {
			TetrevilSounds.setMusicPaused(true);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
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

	@Override
	public boolean containsKey(String key) {
		return getParameter(key) != null;
	}

	@Override
	public String get(String key) {
		return getParameter(key);
	}

	@Override
	public String put(String key, String value) {
		String ret = get(key);
		setParameter(key, value);
		return ret;
	}

	@Override
	public Set<String> keys() {
		return parameters.keySet();
	}
}
