package org.tetrevil.swing;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

import org.tetrevil.Field;
import org.tetrevil.MaliciousRandomizer;
import org.tetrevil.RandomizerFactory;
import org.tetrevil.event.TetrevilAdapter;
import org.tetrevil.event.TetrevilEvent;
import org.tetrevil.wobj.WebScore;

public class TetrevilFrame extends JFrame {
	protected Properties parameters = new Properties();
	
	protected Field field;
	protected TetrevilComponent tc;
	protected TetrevilKeyListener kl;
	protected TetrevilKeyPanel tkp;
	protected DifficultyPanel dp;
	
	protected JButton start = new JButton(new AbstractAction(" ") {
		@Override
		public void actionPerformed(ActionEvent e) {
			dp.setEnabled(false);
			tc.start();
		}
	});
	
	public TetrevilFrame(Field f) {
		super("TETREVIL");
		this.field = f;
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new GridBagLayout());
		setBackground(Color.BLACK);
		
		tc = new TetrevilComponent(field);
		tkp = new TetrevilKeyPanel(kl = tc.getTetrevilKeyListener());
		dp = new DifficultyPanel(field, parameters);
		dp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setStartText();
			}
		});
		
		GridBagConstraints c = new GridBagConstraints(0, 0, 1, 4, 0, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0);
		
		add(tc, c);
		c.gridx++; c.weightx = 1; add(new JLabel(" "), c);
		c.gridx++; c.weightx = 0; c.gridheight = 1; c.weighty = 0; add(tkp, c);
		c.gridy++; add(dp, c);
		c.gridy++; c.weighty = 1; add(new JLabel(" "), c);
		c.gridy++; c.weighty = 0; add(start, c);
		
		field.addTetrevilListener(new TetrevilAdapter() {
			@Override
			public void gameReset(TetrevilEvent e) {
				field.setPaused(true);
				dp.setEnabled(true);
			}
		});
	}
	
	public void init() {
		setStartText();
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
}
