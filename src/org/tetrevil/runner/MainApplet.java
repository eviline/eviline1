package org.tetrevil.runner;

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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;

import org.tetrevil.AngelRandomizer;
import org.tetrevil.BipolarRandomizer;
import org.tetrevil.ConcurrentShapeProvider;
import org.tetrevil.Field;
import org.tetrevil.MaliciousBagRandomizer;
import org.tetrevil.MaliciousRandomizer;
import org.tetrevil.MyndziRandomizer;
import org.tetrevil.RandomizerFactory;
import org.tetrevil.RemoteRandomizer;
import org.tetrevil.ThreadedMaliciousRandomizer;
import org.tetrevil.Version;
import org.tetrevil.event.TetrevilAdapter;
import org.tetrevil.event.TetrevilEvent;
import org.tetrevil.sounds.TetrevilMusicListener;
import org.tetrevil.sounds.TetrevilSoundListener;
import org.tetrevil.sounds.TetrevilSounds;
import org.tetrevil.swing.IntegerDocument;
import org.tetrevil.swing.TetrevilComponent;
import org.tetrevil.swing.TetrevilKeyListener;
import org.tetrevil.swing.TetrevilKeyPanel;
import org.tetrevil.wobj.CookieAccess;
import org.tetrevil.wobj.WebScore;

public class MainApplet extends JApplet {
	private static final long serialVersionUID = 0;
	
	protected ExecutorService threadPool = Executors.newCachedThreadPool();
	
	protected Map<String, String> parameters = new HashMap<String, String>();
	
	protected Icon gear = new ImageIcon(getClass().getClassLoader().getResource("org/tetrevil/images/gear.png"));

	
	protected Field field = new Field(true);
	{{
		field.addTetrevilListener(new TetrevilAdapter() {
			public void gameReset(TetrevilEvent e) {
				if(!field.isGameOver())
					submitScore("Reset");
				setProvider();
			}
			@Override
			public void gameOver(TetrevilEvent e) {
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
			public void shapeLocked(TetrevilEvent e) {
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
			WebScore score = new WebScore();
			score.setScore(field.getLines());
			score.setName(kp.getPlayerName());
			score.setTs(new Date());
			MaliciousRandomizer p = field.getProvider().getMaliciousRandomizer();
			if(p == null)
				return;
			score.setDepth(p.getDepth());
			score.setRfactor(p.getRfactor());
			score.setFair(p.isFair() ? 1 : 0);
			score.setDistribution(p.getDistribution());
			score.setRandomizer(field.getProvider().getRandomizerName());
			score.setAdaptive(p.isAdaptive() ? 1 : 0);
			score.setReason(reason);
			WebScore.submit(score, getParameter("score_host"));
		} catch(Exception ioe) {
			ioe.printStackTrace();
		}
	}
	
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
		if(getParameter("adaptive") != null)
			((MaliciousRandomizer) field.getProvider()).setAdaptive(field, Boolean.parseBoolean(getParameter("adaptive")));
		
		if("true".equals(getParameter("concurrent")))
			field.setProvider(new ConcurrentShapeProvider(field.getProvider()));
		
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
		WebScore highScore = new WebScore();
		highScore.setScore(0);
		highScore.setName("[no score for these settings]");
		highScore.setTs(new Date());
		MaliciousRandomizer p = (MaliciousRandomizer) field.getProvider().getMaliciousRandomizer();
		highScore.setDepth(p.getDepth());
		highScore.setRfactor(p.getRfactor());
		highScore.setFair(p.isFair() ? 1 : 0);
		highScore.setAdaptive(p.isAdaptive() ? 1 : 0);
		highScore.setDistribution(p.getDistribution());
		highScore.setRandomizer(field.getProvider().getRandomizerName());
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
				"&copy;2012 Robin Kirkman<br>\n" +
				"Version " + Version.getVersion() + "</center></html>");
	}
	
	protected JPanel createDifficultyPanel() {
		MaliciousRandomizer p = (MaliciousRandomizer) field.getProvider();
		
		final JButton sadistic = new JButton("Sadistic");
		final JButton evil = new JButton("Evil");
		final JButton normal = new JButton("Aggressive");
		final JButton easy = new JButton("Rude");
		final JButton angelic = new JButton("Angelic");
		final JButton bipolarPreset = new JButton("Bipolar");
		final JButton myndziPreset = new JButton("myndzi");
		
		final JRadioButton malicious = new JRadioButton("Malicious"); malicious.setForeground(Color.BLACK); malicious.setBackground(Color.WHITE); malicious.setPreferredSize(new Dimension(80, malicious.getPreferredSize().height));
		final JRadioButton bag = new JRadioButton("Bag"); bag.setForeground(Color.BLACK); bag.setBackground(Color.WHITE); bag.setPreferredSize(new Dimension(80, bag.getPreferredSize().height));
		final JRadioButton angel = new JRadioButton("Angel"); angel.setForeground(Color.BLACK); angel.setBackground(Color.WHITE); angel.setPreferredSize(new Dimension(80, angel.getPreferredSize().height));
		final JRadioButton bipolar = new JRadioButton("Bipolar"); bipolar.setForeground(Color.BLACK); bipolar.setBackground(Color.WHITE); bipolar.setPreferredSize(new Dimension(80, bipolar.getPreferredSize().height));
		final JRadioButton myndzi = new JRadioButton("myndzi"); myndzi.setForeground(Color.BLACK); myndzi.setBackground(Color.WHITE); myndzi.setPreferredSize(new Dimension(80, myndzi.getPreferredSize().height));
		ButtonGroup g = new ButtonGroup(); g.add(malicious); g.add(bag); g.add(angel); g.add(bipolar); g.add(myndzi);
		
		final JRadioButton fair = new JRadioButton("Fair"); fair.setForeground(Color.BLACK); fair.setBackground(Color.WHITE); fair.setPreferredSize(new Dimension(80, fair.getPreferredSize().height));
		final JRadioButton unfair = new JRadioButton("Unfair"); unfair.setForeground(Color.BLACK); unfair.setBackground(Color.WHITE); unfair.setPreferredSize(new Dimension(80, unfair.getPreferredSize().height));
		g = new ButtonGroup(); g.add(fair); g.add(unfair);

		final JTextField depth = new JTextField(new IntegerDocument(), "" + p.getDepth(), 5);
		final JTextField rfactor = new JTextField(new IntegerDocument(), "" + (int)(100 * p.getRfactor()), 5);
		final JTextField distribution = new JTextField(new IntegerDocument(), "" + p.getDistribution(), 5);
		
		final JCheckBox adaptive = new JCheckBox("Adaptive dist"); adaptive.setForeground(Color.BLACK); adaptive.setBackground(Color.WHITE); 
		final JCheckBox concurrent = new JCheckBox("Concurrent"); concurrent.setBackground(Color.WHITE);
		
		final JButton set = new JButton(new AbstractAction("Set") {
			private boolean setting = false;
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(setting)
					return;
				setting = true;
				provText = "Custom";
				setParameter("depth", depth.getText());
				setParameter("rfactor", "" + (Double.parseDouble(rfactor.getText()) / 100));
				setParameter("distribution", distribution.getText());
				setParameter("fair", "" + fair.isSelected());
				setParameter("adaptive", "" + adaptive.isSelected());
				setParameter("concurrent", "" + concurrent.isSelected());
				if(bag.isSelected())
					RandomizerFactory.setClazz(MaliciousBagRandomizer.class);
				else if(angel.isSelected())
					RandomizerFactory.setClazz(AngelRandomizer.class);
				else if(bipolar.isSelected())
					RandomizerFactory.setClazz(BipolarRandomizer.class);
				else if(myndzi.isSelected())
					RandomizerFactory.setClazz(MyndziRandomizer.class);
				else
					RandomizerFactory.setClazz(ThreadedMaliciousRandomizer.class);
				setProvider();
				setStartText();
				right.remove(difficulty);
				right.add(start, new GridBagConstraints(0, 1, 2, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0));
				start.revalidate();
				validate();
				repaint();
				setting = false;
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						if(provider.isSelected())
							provider.setSelected(false);
					}
				});
			}
		});
		
//		if(MaliciousBagRandomizer.class.isAssignableFrom(RandomizerFactory.getClazz())) {
//			bag.setSelected(true);
//			fair.setEnabled(false);
//			unfair.setEnabled(false);
//			fair.setSelected(true);
//			adaptive.setEnabled(false);
//			adaptive.setSelected(false);
//		} else {
//			malicious.setSelected(true);
//			fair.setEnabled(true);
//			unfair.setEnabled(true);
//			fair.setSelected(p.isFair());
//			unfair.setSelected(!p.isFair());
//			adaptive.setEnabled(true);
//			adaptive.setSelected(p.isAdaptive());
//		}
		
		sadistic.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				malicious.setSelected(true);
				depth.setText("5");
				rfactor.setText("0");
				fair.setEnabled(true);
				unfair.setEnabled(true);
				fair.setSelected(false);
				unfair.setSelected(true);
				distribution.setEnabled(false);
				distribution.setText("30");
				adaptive.setEnabled(false);
				adaptive.setSelected(false);
				concurrent.setSelected(false);
				set.doClick();
//				RandomizerFactory.setClazz(RemoteRandomizer.class);
				provText = "Sadistic";
				setProvider();
			}
		});
		
		evil.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				malicious.setSelected(true);
				depth.setText("3");
				rfactor.setText("0");
				fair.setEnabled(true);
				unfair.setEnabled(true);
				fair.setSelected(false);
				unfair.setSelected(true);
				distribution.setEnabled(false);
				distribution.setText("30");
				adaptive.setEnabled(false);
				adaptive.setSelected(false);
				concurrent.setSelected(false);
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
				rfactor.setText("2");
				fair.setEnabled(true);
				unfair.setEnabled(true);
				fair.setSelected(true);
				distribution.setEnabled(true);
				distribution.setText("30");
				adaptive.setEnabled(true);
				adaptive.setSelected(true);
				concurrent.setSelected(false);
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
				adaptive.setEnabled(false);
				adaptive.setSelected(false);
				concurrent.setSelected(false);
				set.doClick();
				provText = "Rude";
				setProvider();
			}
		});

		angelic.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				angel.setSelected(true);
				depth.setText("3");
				rfactor.setText("1");
				fair.setEnabled(true);
				unfair.setEnabled(true);
				fair.setSelected(true);
				distribution.setEnabled(true);
				distribution.setText("15");
				adaptive.setEnabled(true);
				adaptive.setSelected(false);
				concurrent.setSelected(true);
				set.doClick();
				provText = "Angelic";
				setProvider();
			}
		});
		
		bipolarPreset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				bipolar.setSelected(true);
				depth.setText("3");
				rfactor.setText("1");
				fair.setEnabled(true);
				unfair.setEnabled(true);
				fair.setSelected(true);
				distribution.setEnabled(true);
				distribution.setText("15");
				adaptive.setEnabled(true);
				adaptive.setSelected(false);
				concurrent.setSelected(true);
				set.doClick();
				provText = "Bipolar";
				setProvider();
			}
		});

		myndziPreset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				myndzi.setSelected(true);
				depth.setText("3");
				rfactor.setText("0");
				fair.setEnabled(false);
				unfair.setEnabled(false);
				unfair.setSelected(true);
				distribution.setEnabled(true);
				distribution.setText("2");
				adaptive.setEnabled(true);
				adaptive.setSelected(false);
				concurrent.setSelected(true);
				set.doClick();
				provText = "myndzi";
				setProvider();
			}
		});
		
		malicious.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fair.setEnabled(true);
				unfair.setEnabled(true);
				adaptive.setEnabled(true);
			}
		});
		
		bag.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fair.setEnabled(false);
				unfair.setEnabled(false);
				adaptive.setEnabled(false);
			}
		});
				
		fair.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				distribution.setEnabled(fair.isSelected());
				adaptive.setEnabled(fair.isSelected());
			}
		});
		
		unfair.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				distribution.setEnabled(!unfair.isSelected());
				adaptive.setEnabled(!unfair.isSelected());
			}
		});

		JPanel ret = new JPanel(new GridBagLayout()); ret.setBackground(Color.WHITE);
		GridBagConstraints c = new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
		
		JPanel presets = new JPanel(new GridBagLayout()); presets.setBackground(Color.WHITE);
		presets.add(evil, c);
		c.gridwidth = 2;
		c.gridx += 2; presets.add(normal, c);
		c.gridx += 2; presets.add(easy, c);
		
		angelic.setPreferredSize(sadistic.getPreferredSize());
		sadistic.setPreferredSize(sadistic.getPreferredSize());
		c.gridx = 0; c.gridy++; c.gridwidth = 2; presets.add(sadistic, c);
		c.gridx += 2; presets.add(bipolarPreset, c);
		c.gridx += 2; presets.add(angelic, c);
		c.gridx = 0; c.gridy++; presets.add(myndziPreset, c);
		
		presets.setBorder(BorderFactory.createTitledBorder("Presets"));
		
		c.gridx = 0; c.gridy = 0; c.gridwidth = 1;  ret.add(presets, c);
		
		JLabel l;
		JPanel details = new JPanel(new GridLayout(0, 2)); details.setBackground(Color.WHITE);
		
		details.add(l = new JLabel("Randomizer:")); details.add(malicious); l.setForeground(Color.BLACK);
		details.add(new JLabel("")); details.add(bag);
		details.add(new JLabel("")); details.add(angel);
		details.add(new JLabel("")); details.add(bipolar);
		details.add(new JLabel("")); details.add(myndzi);
		
		
		details.add(l = new JLabel("Distribution:")); details.add(unfair); l.setForeground(Color.BLACK);
		details.add(new JLabel("")); details.add(fair);
		
		details.add(l = new JLabel("Depth:")); details.add(depth); l.setForeground(Color.BLACK);
		details.add(l = new JLabel("Random Factor %:")); details.add(rfactor); l.setForeground(Color.BLACK);
		
		details.add(l = new JLabel("Dist factor:")); details.add(distribution); l.setForeground(Color.BLACK);
		
		details.add(concurrent); details.add(adaptive);
		
		c.gridy++; c.weighty = 1; ret.add(details, c);

//		c.gridy++; c.weighty = 0; ret.add(set, c);
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if("true".equals(getParameter("angelic")))
					angelic.doClick();
				else
					bipolarPreset.doClick();
			}
		});
		
		provider.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!provider.isSelected() && (!field.isPlaying() || field.isGameOver())) {
					set.doClick();
				}
			}
		});
		
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
					field.addTetrevilListener(soundListener);
				else
					field.removeTetrevilListener(soundListener);
			}
		});
		sound.setBackground(Color.WHITE);
		
		JCheckBox music = new JCheckBox("Music");
		music.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent l) {
				JCheckBox sound = (JCheckBox) l.getSource();
				if(musicEnabled = sound.isSelected()) {
					field.addTetrevilListener(musicListener);
				} else {
					field.removeTetrevilListener(musicListener);
				}
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
			}
			return;
		}
//		field.setProvider(new ConcurrentShapeProvider(field, field.getProvider()));
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		getContentPane().setBackground(Color.WHITE);
		
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
				} else if(provider.isSelected()) {
					provider.setSelected(false);
				}
			}
		});
		setProvider();
				
		if(getParameter("score_host") == null)
			setParameter("score_host", "www.tetrevil.org:8080");

		
		
		
		
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
		
		setBackground(Color.WHITE);
		right.setBackground(Color.WHITE);
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
}
