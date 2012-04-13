package org.tetrevil.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.tetrevil.Field;
import org.tetrevil.MaliciousRandomizer;
import org.tetrevil.RandomizerFactory;
import org.tetrevil.event.TetrevilAdapter;
import org.tetrevil.event.TetrevilEvent;
import org.tetrevil.wobj.WebScore;

public class TetrevilFrame extends JFrame {
	private class TFLayoutManager implements LayoutManager {

		@Override
		public void addLayoutComponent(String name, Component comp) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void removeLayoutComponent(Component comp) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Dimension preferredLayoutSize(Container parent) {
			Dimension d = parent.getSize();
			d.width = Math.max(d.width, d.height);
			d.width = Math.max(d.width, 1024);
			d.height = Math.max(d.height, 768);
			return d;
		}

		@Override
		public Dimension minimumLayoutSize(Container parent) {
			Dimension d = parent.getSize();
			d.width = d.height / 3;
			d.width = Math.max(d.width, 1024);
			d.height = Math.max(d.height, 768);
			return d;
		}

		@Override
		public void layoutContainer(Container parent) {
			Dimension psize = parent.getSize();
			
			Dimension d = new Dimension(psize);
			d.width = d.height;
			tc.setLocation(0, 0);
			tc.setSize(d);
			
			d = new Dimension(psize);
			d.height /= 3;
			d.width = Math.max(d.height, 300);
			tkp.setSize(d); dp.setSize(d); start.setSize(d);
			
			Point p = new Point(psize.width - d.width, 0);
			tkp.setLocation(p);
			p.y += d.height; dp.setLocation(p);
			p.y += d.height; start.setLocation(p);
			
			d = new Dimension(psize.width - tc.getSize().width - d.width, psize.height);
			p = new Point(tc.getSize().width, 0);
			center.setSize(d);
			center.setLocation(p);
		}
		
	}
	
	protected Properties parameters;
	
	protected Field field;
	protected TetrevilComponent tc;
	protected TetrevilKeyListener kl;
	protected TetrevilKeyPanel tkp;
	protected DifficultyPanel dp;
	protected JPanel center = new JPanel();
	
	protected JButton start = new JButton(new AbstractAction(" ") {
		@Override
		public void actionPerformed(ActionEvent e) {
			dp.setEnabled(false);
			tc.start();
			tc.getTable().requestFocusInWindow();
			field.setPaused(false);
		}
	});
	
	public TetrevilFrame(Field f, Properties properties) {
		super("TETREVIL");
		this.field = f;
		this.parameters = properties;
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new GridBagLayout());
		setBackground(Color.WHITE);
		getContentPane().setBackground(Color.WHITE);
		((JPanel) getContentPane()).setOpaque(true);
		
		tc = new TetrevilComponent(field);
		kl = tc.getTetrevilKeyListener();
		setKeysFromParams();
		tkp = new TetrevilKeyPanel(kl);
		dp = new DifficultyPanel(field, parameters);
		center.setBackground(Color.WHITE);
		center.setOpaque(true);
		
		dp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setStartText();
			}
		});
		
//		GridBagConstraints c = new GridBagConstraints(0, 0, 1, 4, 0, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0);
//		
//		add(tc, c);
//		c.gridx++; c.weightx = 1; add(new JLabel(" "), c);
//		c.gridx++; c.weightx = 0; c.gridheight = 1; c.weighty = 0; add(tkp, c);
//		c.gridy++; add(dp, c);
//		c.gridy++; c.weighty = 1; add(new JLabel(" "), c);
//		c.gridy++; c.weighty = 0; add(start, c);
		
		add(tc); add(tkp); add(dp); add(start); add(center);
		
		setLayout(new TFLayoutManager());
		
		field.addTetrevilListener(new TetrevilAdapter() {
			@Override
			public void gameReset(TetrevilEvent e) {
				if(!field.isGameOver())
					submitScore();
				field.setPaused(true);
				dp.setEnabled(true);
				dp.setProvider();
			}
			@Override
			public void gameOver(TetrevilEvent e) {
				try {
					submitScore();

					setStartText();
				} catch(Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		
		FocusListener fcl = new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
//				field.setPaused(false);
			}
			@Override
			public void focusLost(FocusEvent e) {
				field.setPaused(true);
			}
		};
		tc.getTable().addFocusListener(fcl);
		
	}
	
	public void init() {
		setStartText();
		setKeysFromParams();
		tkp.update();
	}
	
	protected void setStartText() {
		WebScore highScore = new WebScore();
		highScore.setScore(0);
		highScore.setName("[nobody]");
		highScore.setTs(new Date());
		MaliciousRandomizer p = (MaliciousRandomizer) field.getProvider();
		highScore.setDepth(p.getDepth());
		highScore.setRfactor(p.getRfactor());
		highScore.setFair(p.isFair() ? 1 : 0);
		highScore.setAdaptive(p.isAdaptive() ? 1 : 0);
		highScore.setDistribution(p.getDistribution());
		highScore.setRandomizer(RandomizerFactory.newRandomizer().getRandomizerName());
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

	public void submitScore() {
		if(!field.isPlaying() && !field.isGameOver())
			return;
		try {
			WebScore score = new WebScore();
			score.setScore(field.getLines());
			score.setName(tkp.getPlayerName());
			score.setTs(new Date());
			MaliciousRandomizer p = (MaliciousRandomizer) field.getProvider();
			score.setDepth(p.getDepth());
			score.setRfactor(p.getRfactor());
			score.setFair(p.isFair() ? 1 : 0);
			score.setDistribution(p.getDistribution());
			score.setRandomizer(p.getRandomizerName());
			score.setAdaptive(p.isAdaptive() ? 1 : 0);
			WebScore.submit(score, getParameter("score_host"));
		} catch(Exception ioe) {
			ioe.printStackTrace();
		}
	}

	public void setParamsFromKeys() {
		setParameter("left", "" + kl.LEFT);
		setParameter("right", "" + kl.RIGHT);
		setParameter("rotate_left", "" + kl.ROTATE_LEFT);
		setParameter("rotate_right", "" + kl.ROTATE_RIGHT);
		setParameter("down", "" + kl.DOWN);
		setParameter("drop", "" + kl.DROP);
		setParameter("das_time", "" + kl.DAS_TIME);
		setParameter("player_name", tkp.getPlayerName());
	}
	
	public void setKeysFromParams() {
		if(kl != null) {
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
		}
		if(tkp != null) {
			if(getParameter("player_name") != null)
				tkp.setPlayerName(getParameter("player_name"));
		}
	}

	public Properties getParameters() {
		return parameters;
	}

	public String getParameter(String name) {
		return parameters.getProperty(name);
	}
	
	public void setParameter(String name, String value) {
		parameters.setProperty(name, value);
	}
	public TetrevilComponent getTc() {
		return tc;
	}
	
	public TetrevilKeyPanel getTkp() {
		return tkp;
	}
	
	public JPanel getCenter() {
		return center;
	}
}

