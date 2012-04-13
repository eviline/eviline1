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

import org.tetrevil.Field;
import org.tetrevil.MaliciousBagRandomizer;
import org.tetrevil.MaliciousRandomizer;
import org.tetrevil.RandomizerFactory;
import org.tetrevil.RemoteRandomizer;
import org.tetrevil.ThreadedMaliciousRandomizer;

public class DifficultyPanel extends JPanel {
	protected Field field;
	protected JLabel provText = new JLabel("Aggressive");
	{{
		provText.setForeground(Color.BLACK);
		provText.setHorizontalAlignment(SwingConstants.CENTER);
		provText.setBorder(BorderFactory.createTitledBorder("Difficulty Setting"));
	}}
	protected JButton set;
	protected JButton worst;
	protected JButton evil;
	protected JButton normal;
	protected JButton easy;
	
	protected Properties props;

	public DifficultyPanel(Field f, Properties props) {
		super(new GridBagLayout());
		this.field = f;
		this.props = props;
		
		MaliciousRandomizer p = (MaliciousRandomizer) field.getProvider();
		
		worst = new JButton("Sadistic");
		evil = new JButton("Evil");
		normal = new JButton("Aggressive");
		easy = new JButton("Rude");
		
		final JRadioButton malicious = new JRadioButton("Malicious"); 
		malicious.setForeground(Color.BLACK); malicious.setBackground(Color.WHITE); 
		malicious.setPreferredSize(new Dimension(80, malicious.getPreferredSize().height));
		
		final JRadioButton bag = new JRadioButton("Bag"); 
		bag.setForeground(Color.BLACK); bag.setBackground(Color.WHITE);
		bag.setPreferredSize(new Dimension(80, bag.getPreferredSize().height));
		ButtonGroup g = new ButtonGroup(); g.add(malicious); g.add(bag);
		
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
				if(bag.isSelected())
					RandomizerFactory.setClazz(MaliciousBagRandomizer.class);
				else
					RandomizerFactory.setClazz(ThreadedMaliciousRandomizer.class);
				setProvider();
				provText.setText(field.getProvider().toString());
			}
		});
		
		if(MaliciousBagRandomizer.class == RandomizerFactory.getClazz()) {
			bag.setSelected(true);
			fair.setEnabled(false);
			unfair.setEnabled(false);
			fair.setSelected(true);
			adaptive.setEnabled(false);
			adaptive.setSelected(false);
		} else {
			malicious.setSelected(true);
			fair.setEnabled(true);
			unfair.setEnabled(true);
			fair.setSelected(p.isFair());
			unfair.setSelected(!p.isFair());
			adaptive.setEnabled(true);
			adaptive.setSelected(p.isAdaptive());
		}
		
		worst.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!DifficultyPanel.this.isEnabled())
					return;
				malicious.setSelected(true);
				depth.setText("6");
				rfactor.setText("0");
				fair.setEnabled(true);
				unfair.setEnabled(true);
				unfair.setSelected(true);
				distribution.setEnabled(false);
				distribution.setText("30");
				adaptive.setEnabled(false);
				adaptive.setSelected(false);
				set.doClick();
				RandomizerFactory.setClazz(RemoteRandomizer.class);
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
				depth.setText("4");
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

		setBackground(Color.WHITE);
		GridBagConstraints c = new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
		
		JPanel presets = new JPanel(new GridBagLayout()); presets.setBackground(Color.WHITE);
		presets.add(worst, c); c.gridx++; 
		presets.add(evil, c);
		c.gridx++; presets.add(normal, c);
		c.gridx++; presets.add(easy, c);
		
		c.gridx = 0;  add(presets, c);
		
		JLabel l;
		JPanel details = new JPanel(new GridLayout(0, 2)); details.setBackground(Color.WHITE);
		
		details.add(l = new JLabel("Randomizer:")); details.add(malicious); l.setForeground(Color.BLACK);
		details.add(new JLabel("")); details.add(bag);
		
		details.add(l = new JLabel("Distribution:")); details.add(unfair); l.setForeground(Color.BLACK);
		details.add(new JLabel("")); details.add(fair);
		
		details.add(l = new JLabel("Depth:")); details.add(depth); l.setForeground(Color.BLACK);
		details.add(l = new JLabel("Random Factor %:")); details.add(rfactor); l.setForeground(Color.BLACK);
		
		details.add(l = new JLabel("Dist factor:")); details.add(distribution); l.setForeground(Color.BLACK);
		
		details.add(l = new JLabel("")); details.add(adaptive);
		
		c.gridy++; c.weighty = 1; add(details, c);

		c.gridy++; c.weighty = 0; add(set, c);
		
		c.gridy++; add(provText, c);
		
		normal.doClick();
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
		fireActionPerformed("difficulty");
	}
	
	protected void setParameter(String key, String value) {
		props.setProperty(key, value);
	}
	
	protected String getParameter(String key) {
		return props.getProperty(key);
	}

}
