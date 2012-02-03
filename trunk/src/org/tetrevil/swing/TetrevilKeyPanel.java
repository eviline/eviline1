package org.tetrevil.swing;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.Field;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

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


		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}
}
