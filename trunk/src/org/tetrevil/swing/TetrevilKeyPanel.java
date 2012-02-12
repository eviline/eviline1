package org.tetrevil.swing;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.Field;
import java.text.NumberFormat;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.PlainDocument;
import javax.swing.text.Position;
import javax.swing.text.Segment;

public class TetrevilKeyPanel extends JPanel {
	protected TetrevilKeyListener kl;
	
	protected class KeyButton extends JToggleButton {
		protected Field f;
		
		protected void update() {
			try {
				setText(KeyEvent.getKeyText((Integer) f.get(kl)));
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		
		protected KeyListener setter = new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				try {
					f.set(kl, e.getKeyCode());
					setSelected(false);
				} catch (IllegalAccessException e1) {
					throw new RuntimeException(e1);
				} finally {
					update();
				}
			}
		};
		
		public KeyButton(Field f) {
			super(" ");
			this.f = f;
			update();
			addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					if(isSelected()) {
						addKeyListener(setter);
					} else {
						removeKeyListener(setter);
					}
				}
			});
		}
	}
	
	protected Document dasdoc = new IntegerDocument();
	protected JTextField dastext = new JTextField(dasdoc, "350", 5);
	{{
		dasdoc.addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent arg0) {
				if(dastext.getText().matches("[0-9]+"))
					kl.DAS_TIME = Integer.parseInt(dastext.getText());
			}
			
			@Override
			public void insertUpdate(DocumentEvent arg0) {
				if(dastext.getText().matches("[0-9]+"))
					kl.DAS_TIME = Integer.parseInt(dastext.getText());
			}
			
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				if(dastext.getText().matches("[0-9]+"))
					kl.DAS_TIME = Integer.parseInt(dastext.getText());
			}
		});
	}}
	
	protected JTextField playerName = new JTextField("web user");
	
	public TetrevilKeyPanel(TetrevilKeyListener kl) {
		super(new GridLayout(0, 2));
		this.kl = kl;
		try {
			add(new JLabel("LEFT:")); add(new KeyButton(TetrevilKeyListener.class.getField("LEFT")));
			add(new JLabel("RIGHT:")); add(new KeyButton(TetrevilKeyListener.class.getField("RIGHT")));
			add(new JLabel("ROTATE_LEFT:")); add(new KeyButton(TetrevilKeyListener.class.getField("ROTATE_LEFT")));
			add(new JLabel("ROTATE_RIGHT:")); add(new KeyButton(TetrevilKeyListener.class.getField("ROTATE_RIGHT")));
			add(new JLabel("DOWN:")); add(new KeyButton(TetrevilKeyListener.class.getField("DOWN")));
			add(new JLabel("DROP:")); add(new KeyButton(TetrevilKeyListener.class.getField("DROP")));

			add(new JLabel("DAS_TIME:")); add(dastext);
			
			add(new JLabel("Player name:")); add(playerName);
			
			setBackground(Color.BLACK);
			for(int i = 0; i < getComponentCount(); i++) {
				if(getComponent(i) instanceof JLabel) {
					getComponent(i).setForeground(Color.WHITE);
				}
			}

		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}
	
	public String getPlayerName() {
		return playerName.getText();
	}
}
