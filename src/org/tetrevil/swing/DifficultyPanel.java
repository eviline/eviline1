package org.tetrevil.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

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
import org.tetrevil.sounds.TetrevilMusicListener;
import org.tetrevil.sounds.TetrevilSoundListener;
import org.tetrevil.sounds.TetrevilSounds;

public class DifficultyPanel extends JPanel {
	protected Field field;
	protected JLabel provText = new JLabel("Aggressive");
	{{
		provText.setForeground(Color.BLACK);
		provText.setHorizontalAlignment(SwingConstants.CENTER);
		provText.setBorder(BorderFactory.createTitledBorder("Difficulty Setting"));
	}}
	protected JButton set;
	protected JButton worst = new JButton("Sadistic");;
	protected JButton evil = new JButton("Evil");
	protected JButton normal = new JButton("Aggressive");
	protected JButton easy = new JButton("Rude");
	protected JButton angelic = new JButton("Angelic");
	protected JButton bipolarPreset = new JButton("Bipolar");
	protected JButton myndziPreset = new JButton("myndzi");

	protected Properties props;

	public DifficultyPanel(Field f, Properties props) {
		super(new GridBagLayout());
		this.field = f;
		this.props = props;
		
		MaliciousRandomizer p = (MaliciousRandomizer) field.getProvider();
		
		
		
		
		
		final JRadioButton malicious = new JRadioButton("Malicious"); 
		malicious.setForeground(Color.BLACK); malicious.setBackground(Color.WHITE); 
		malicious.setPreferredSize(new Dimension(80, malicious.getPreferredSize().height));
		
		final JRadioButton bag = new JRadioButton("Bag"); 
		bag.setForeground(Color.BLACK); bag.setBackground(Color.WHITE);
		bag.setPreferredSize(new Dimension(80, bag.getPreferredSize().height));
		
		final JRadioButton angel = new JRadioButton("Angel");
		angel.setForeground(Color.BLACK); angel.setBackground(Color.WHITE);
		angel.setPreferredSize(new Dimension(80, bag.getPreferredSize().height));
		
		final JRadioButton bipolar = new JRadioButton("Bipolar"); 
		bipolar.setForeground(Color.BLACK); bipolar.setBackground(Color.WHITE); 
		bipolar.setPreferredSize(new Dimension(80, bipolar.getPreferredSize().height));
		
		final JRadioButton myndzi = new JRadioButton("myndzi"); 
		myndzi.setForeground(Color.BLACK); myndzi.setBackground(Color.WHITE); 
		myndzi.setPreferredSize(new Dimension(80, myndzi.getPreferredSize().height));

		ButtonGroup g = new ButtonGroup(); g.add(malicious); g.add(bag); g.add(angel); g.add(bipolar); g.add(myndzi);
		
		final JRadioButton fair = new JRadioButton("Fair"); 
		fair.setForeground(Color.BLACK); fair.setBackground(Color.WHITE); 
		fair.setPreferredSize(new Dimension(80, fair.getPreferredSize().height));
		
		final JRadioButton unfair = new JRadioButton("Unfair"); 
		unfair.setForeground(Color.BLACK); unfair.setBackground(Color.WHITE);
		unfair.setPreferredSize(new Dimension(80, unfair.getPreferredSize().height));
		g = new ButtonGroup(); g.add(fair); g.add(unfair);

		final JTextField depth = new JTextField(new IntegerDocument(), "" + p.getDepth(), 5);
		final JTextField rfactor = new JTextField(new IntegerDocument(), "" + (int)(100 * p.getRfactor()), 5);
		final JTextField distribution = new JTextField(new IntegerDocument(), "" + p.getDistribution(), 5);
		
		final JCheckBox adaptive = new JCheckBox("Adaptive dist"); 
		adaptive.setForeground(Color.BLACK); adaptive.setBackground(Color.WHITE);
		
		final JCheckBox concurrent = new JCheckBox("Concurrent"); 
		concurrent.setForeground(Color.BLACK); concurrent.setBackground(Color.WHITE);

		
		final JCheckBox music = new JCheckBox("Music");
		music.setForeground(Color.BLACK); music.setBackground(Color.WHITE);

		final JCheckBox sounds = new JCheckBox("Sounds");
		sounds.setForeground(Color.BLACK); sounds.setBackground(Color.WHITE);

		
		set = new JButton(new AbstractAction("Set Custom Difficulty") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(!DifficultyPanel.this.isEnabled())
					return;
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
				provText.setText(field.getProvider().toString());
			}
		});
		
		worst.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!DifficultyPanel.this.isEnabled())
					return;
				malicious.setSelected(true);
				depth.setText("5");
				rfactor.setText("0");
				fair.setEnabled(true);
				unfair.setEnabled(true);
				unfair.setSelected(true);
				distribution.setEnabled(false);
				distribution.setText("30");
				adaptive.setEnabled(false);
				adaptive.setSelected(false);
				set.doClick();
				provText.setText("Sadistic");
				setProvider();
			}
		});
		
		evil.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(!DifficultyPanel.this.isEnabled())
					return;
				malicious.setSelected(true);
				depth.setText("3");
				rfactor.setText("0");
				fair.setEnabled(true);
				unfair.setEnabled(true);
				unfair.setSelected(true);
				distribution.setEnabled(false);
				distribution.setText("30");
				adaptive.setEnabled(false);
				adaptive.setSelected(false);
				set.doClick();
				provText.setText("Evil");
				setProvider();
			}
		});
		
		normal.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!DifficultyPanel.this.isEnabled())
					return;
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
				set.doClick();
				provText.setText("Aggressive");
				setProvider();
			}
		});
		
		easy.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!DifficultyPanel.this.isEnabled())
					return;
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
				set.doClick();
				provText.setText("Rude");
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
				provText.setText("Angelic");
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
				provText.setText("Bipolar");
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
				provText.setText("myndzi");
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
		
		music.addActionListener(new ActionListener() {
			private TetrevilMusicListener tml;
			@Override
			public void actionPerformed(ActionEvent e) {
				TetrevilSounds.setMusicPaused(true);
				if(music.isSelected()) {
					tml = new TetrevilMusicListener();
					field.addTetrevilListener(tml);
				} else {
					field.removeTetrevilListener(tml);
				}
				setParameter("music", "" + music.isSelected());
			}
		});
		
		sounds.addActionListener(new ActionListener() {
			private TetrevilSoundListener tsl = new TetrevilSoundListener();
			@Override
			public void actionPerformed(ActionEvent e) {
				if(sounds.isSelected()) {
					field.addTetrevilListener(tsl);
				} else {
					field.removeTetrevilListener(tsl);
				}
				setParameter("sounds", "" + sounds.isSelected());
			}
		});

		setBackground(Color.WHITE);
		GridBagConstraints c = new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
		
		JPanel presets = new JPanel(new GridBagLayout()); presets.setBackground(Color.WHITE);
		presets.add(worst, c); 
		c.gridx++; presets.add(evil, c);
		c.gridx++; presets.add(normal, c);
		c.gridx++; presets.add(easy, c);
		c.gridx = 0; c.gridy++; presets.add(angelic, c);
		c.gridx++; presets.add(bipolarPreset, c);
		c.gridx++; presets.add(myndziPreset, c);
		
		c.gridx = 0; c.gridy = 0; add(presets, c);
		
		JLabel l;
		JPanel details = new JPanel(new GridLayout(0, 2)); details.setBackground(Color.WHITE);
		
		details.add(l = new JLabel("Randomizer:")); details.add(malicious); l.setForeground(Color.BLACK);
		details.add(new JLabel("")); details.add(bag); details.add(new JLabel("")); details.add(angel);
		details.add(new JLabel("")); details.add(bipolar); details.add(new JLabel("")); details.add(myndzi);
		
		details.add(l = new JLabel("Distribution:")); details.add(unfair); l.setForeground(Color.BLACK);
		details.add(new JLabel("")); details.add(fair);
		
		details.add(l = new JLabel("Depth:")); details.add(depth); l.setForeground(Color.BLACK);
		details.add(l = new JLabel("Random Factor %:")); details.add(rfactor); l.setForeground(Color.BLACK);
		
		details.add(l = new JLabel("Dist factor:")); details.add(distribution); l.setForeground(Color.BLACK);
		
		details.add(concurrent); details.add(adaptive);
		details.add(music); details.add(sounds);
		
		
		c.gridy++; c.weighty = 1; add(details, c);

		c.weighty = 0;
		c.gridy++; add(set, c);
		
		c.gridy++; add(provText, c);
		
		bipolarPreset.doClick();
		
		if("true".equals(getParameter("music")))
			music.doClick();
		if("true".equals(getParameter("sounds")))
			sounds.doClick();
	}
	
	public void addActionListener(ActionListener l) {
		listenerList.add(ActionListener.class, l);
	}
	
	public void removeActionListener(ActionListener l) {
		listenerList.remove(ActionListener.class, l);
	}
	
	protected void fireActionPerformed(String command) {
		Object[] ll = listenerList.getListenerList();
		ActionEvent e = null;
		for(int i = ll.length - 2; i >= 0; i -= 2) {
			if(ActionListener.class == ll[i]) {
				if(e == null)
					e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, command);
				((ActionListener) ll[i+1]).actionPerformed(e);
			}
		}
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		set.setEnabled(enabled);
		worst.setEnabled(enabled);
		evil.setEnabled(enabled);
		normal.setEnabled(enabled);
		easy.setEnabled(enabled);
		angelic.setEnabled(enabled);
		bipolarPreset.setEnabled(enabled);
		myndziPreset.setEnabled(enabled);
		
	}
	
	public void setProvider() {
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
		fireActionPerformed("difficulty");
	}
	
	protected void setParameter(String key, String value) {
		props.setProperty(key, value);
	}
	
	protected String getParameter(String key) {
		return props.getProperty(key);
	}

}
