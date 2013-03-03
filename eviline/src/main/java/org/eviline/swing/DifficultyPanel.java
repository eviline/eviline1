package org.eviline.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

import org.eviline.Field;
import org.eviline.PropertySource;
import org.eviline.event.EvilineAdapter;
import org.eviline.event.EvilineEvent;
import org.eviline.randomizer.AngelRandomizer;
import org.eviline.randomizer.BipolarRandomizer;
import org.eviline.randomizer.RandomizerFactory;
import org.eviline.randomizer.RandomizerPresets;
import org.eviline.randomizer.ThreadedMaliciousRandomizer;
import org.eviline.sounds.TetrevilMusicListener;
import org.eviline.sounds.TetrevilSoundListener;
import org.eviline.sounds.TetrevilSounds;

import static org.eviline.randomizer.RandomizerFactory.*;

public class DifficultyPanel extends JPanel implements PropertySource {
	protected Field field;
	protected JLabel provText = new JLabel("Aggressive");
	{{
		provText.setForeground(Color.BLACK);
		provText.setHorizontalAlignment(SwingConstants.CENTER);
		provText.setBorder(BorderFactory.createTitledBorder("Difficulty Setting"));
	}}
	protected JButton set;
	
	protected List<JButton> presetButtons = new ArrayList<JButton>();
	
	protected PropertySource props;

	protected JRadioButton malicious = new JRadioButton("Malicious"); 
	protected JRadioButton angel = new JRadioButton("Angel");
	protected JRadioButton bipolar = new JRadioButton("Bipolar"); 

	protected JRadioButton fair = new JRadioButton("Fair"); 
	protected JRadioButton unfair = new JRadioButton("Unfair"); 

	protected JTextField depth = new JTextField(new IntegerDocument(), "", 5);
	protected JTextField rfactor = new JTextField(new IntegerDocument(), "", 5);
	protected JTextField distribution = new JTextField(new IntegerDocument(), "", 5);

	protected JCheckBox concurrent = new JCheckBox("Concurrent"); 
	protected JTextField nextSize = new JTextField(new IntegerDocument(), "", 5);
	
	protected JCheckBox music = new JCheckBox("Music");
	protected JCheckBox sounds = new JCheckBox("Sounds");

	public DifficultyPanel(Field f, PropertySource props, boolean soundControls) {
		super(new GridBagLayout());
		this.field = f;
		this.props = props;
		
		malicious.setForeground(Color.BLACK); malicious.setBackground(Color.WHITE); 
		malicious.setPreferredSize(new Dimension(80, malicious.getPreferredSize().height));
		
		angel.setForeground(Color.BLACK); angel.setBackground(Color.WHITE);
		angel.setPreferredSize(new Dimension(80, angel.getPreferredSize().height));
		
		bipolar.setForeground(Color.BLACK); bipolar.setBackground(Color.WHITE); 
		bipolar.setPreferredSize(new Dimension(80, bipolar.getPreferredSize().height));
		
		ButtonGroup g = new ButtonGroup(); g.add(malicious); g.add(angel); g.add(bipolar);
		
		fair.setForeground(Color.BLACK); fair.setBackground(Color.WHITE); 
		fair.setPreferredSize(new Dimension(80, fair.getPreferredSize().height));
		
		unfair.setForeground(Color.BLACK); unfair.setBackground(Color.WHITE);
		unfair.setPreferredSize(new Dimension(80, unfair.getPreferredSize().height));
		g = new ButtonGroup(); g.add(fair); g.add(unfair);

		
		concurrent.setForeground(Color.BLACK); concurrent.setBackground(Color.WHITE);

		
		music.setForeground(Color.BLACK); music.setBackground(Color.WHITE);

		sounds.setForeground(Color.BLACK); sounds.setBackground(Color.WHITE);

		
		set = new JButton(new AbstractAction("Set Custom Difficulty") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(!DifficultyPanel.this.isEnabled())
					return;
				updateProperties();
				provText.setText(field.getProvider().toString());
				setProvider();
			}
		});
		
		for(final RandomizerPresets preset : RandomizerPresets.values()) {
			JButton b = new JButton(new AbstractAction(preset.getName()) {
				@Override
				public void actionPerformed(ActionEvent e) {
//					DifficultyPanel.this.props.putAll(preset.getProperties());
					for(String key : preset.keys())
						DifficultyPanel.this.props.put(key, preset.get(key));
					updateFields();
					provText.setText(preset.getName());
					setProvider();
				}
			});
			presetButtons.add(b);
		}
		
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
		
		music.addActionListener(new ActionListener() {
			private TetrevilMusicListener tml;
			@Override
			public void actionPerformed(ActionEvent e) {
				TetrevilSounds.setMusicPaused(true);
				if(music.isSelected()) {
					tml = new TetrevilMusicListener();
					field.addEvilineListener(tml);
				} else {
					field.removeEvilineListener(tml);
				}
				setParameter("music", "" + music.isSelected());
			}
		});
		
		sounds.addActionListener(new ActionListener() {
			private TetrevilSoundListener tsl = new TetrevilSoundListener();
			@Override
			public void actionPerformed(ActionEvent e) {
				if(sounds.isSelected()) {
					field.addEvilineListener(tsl);
				} else {
					field.removeEvilineListener(tsl);
				}
				setParameter("sounds", "" + sounds.isSelected());
			}
		});

		setBackground(Color.WHITE);
		GridBagConstraints c = new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
		
		JPanel presets = new JPanel(new GridLayout(0, 3)); presets.setBackground(Color.WHITE);
//		presets.add(worst, c); 
//		c.gridx++; presets.add(evil, c);
//		c.gridx++; presets.add(normal, c);
//		c.gridx = 0; c.gridy++; presets.add(angelic, c);
//		c.gridx++; presets.add(bipolarPreset, c);
		for(JButton b : presetButtons)
			presets.add(b);
		
		c.gridx = 0; c.gridy = 0; add(presets, c);
		
		JLabel l;
		JPanel details = new JPanel(new GridLayout(0, 2)); details.setBackground(Color.WHITE);
		
		details.add(l = new JLabel("Randomizer:")); details.add(malicious); l.setForeground(Color.BLACK);
		details.add(new JLabel("")); details.add(angel);
		details.add(new JLabel("")); details.add(bipolar);
		
		details.add(l = new JLabel("Distribution:")); details.add(unfair); l.setForeground(Color.BLACK);
		details.add(new JLabel("")); details.add(fair);
		
		details.add(l = new JLabel("Depth:")); details.add(depth); l.setForeground(Color.BLACK);
		details.add(l = new JLabel("Random Factor %:")); details.add(rfactor); l.setForeground(Color.BLACK);
		
		details.add(l = new JLabel("Dist factor:")); details.add(distribution); l.setForeground(Color.BLACK);
		
		details.add(new JLabel("")); details.add(concurrent);
		details.add(new JLabel("Next Pieces:")); details.add(nextSize);
		if(soundControls) {
			details.add(music); details.add(sounds);
		}
		
		
		c.gridy++; c.weighty = 1; add(details, c);

		c.weighty = 0;
		c.gridy++; add(set, c);
		
		c.gridy++; add(provText, c);

		presetButtons.get(RandomizerPresets.BIPOLAR.ordinal()).doClick();
		
		if("true".equals(getParameter("music")))
			music.doClick();
		if("true".equals(getParameter("sounds")))
			sounds.doClick();
		
		field.addEvilineListener(new EvilineAdapter() {
			@Override
			public void gameReset(EvilineEvent e) {
				setProvider();
			}
		});
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
		for(JButton b : presetButtons)
			b.setEnabled(enabled);
		
	}
	
	public void setProvider() {
		field.setProvider(new RandomizerFactory().newRandomizer(this));
		fireActionPerformed("difficulty");
	}
	
	protected void setParameter(String key, String value) {
		props.put(key, value);
	}
	
	protected String getParameter(String key) {
		return props.get(key);
	}

	@Override
	public boolean containsKey(String key) {
		return getParameter(key) != null;
	}

	@Override
	public String get(String key) {
		return getParameter(key);
	}

	protected String nnget(String key) {
		String val = get(key);
		return val == null ? "" : val;
	}
	
	public void updateFields() {
		depth.setText(nnget(DEPTH));
		rfactor.setText("" + (int)(100 * Double.parseDouble(nnget(RFACTOR))));
		distribution.setText(nnget(DISTRIBUTION));
		fair.setSelected("true".equals(get(FAIR)));
		concurrent.setSelected("true".equals(get(CONCURRENT)));
		if(AngelRandomizer.class.getName().equals(get(CLASS)))
			angel.setSelected(true);
		else if(BipolarRandomizer.class.getName().equals(get(CLASS)))
			bipolar.setSelected(true);
		else
			malicious.setSelected(true);
		nextSize.setText(nnget(NEXT));
	}
	
	public void updateProperties() {
		setParameter(DEPTH, depth.getText());
		setParameter(RFACTOR, "" + Double.parseDouble(rfactor.getText()) / 100);
		setParameter(DISTRIBUTION, distribution.getText());
		setParameter(FAIR, "" + fair.isSelected());
		setParameter(CONCURRENT, "" + concurrent.isSelected());
		setParameter(NEXT, "" + Integer.parseInt(nextSize.getText()));
		if(angel.isSelected())
			setParameter(CLASS, AngelRandomizer.class.getName());
		else if(bipolar.isSelected())
			setParameter(CLASS, BipolarRandomizer.class.getName());
		else
			setParameter(CLASS, ThreadedMaliciousRandomizer.class.getName());
	}

	@Override
	public String put(String key, String value) {
		return (String) props.put(key, value);
	}

	@Override
	public Set<String> keys() {
		return props.keys();
	}

	public String getProvText() {
		return provText.getText();
	}
}
