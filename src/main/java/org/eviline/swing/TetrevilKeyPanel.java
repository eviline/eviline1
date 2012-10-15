package org.eviline.swing;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.Field;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

/**
 * Panel used to set the control values of a {@link TetrevilKeyListener}
 * @author robin
 *
 */
public class TetrevilKeyPanel extends JPanel {
	private static final long serialVersionUID = 0;
	
	protected TetrevilKeyListener kl;
	
	/**
	 * A button which will set the value of a particular {@link Field} to whatever key is pressed
	 * after the button is clicked
	 * @author robin
	 *
	 */
	protected class KeyButton extends JToggleButton {
		private static final long serialVersionUID = 0;
		
		protected Field f;
		
		public void update() {
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
				if(dastext.getText().matches("[0-9]+")) {
					kl.DAS_TIME = Integer.parseInt(dastext.getText());
					kl.updateTimers();
				}
			}
			
			@Override
			public void insertUpdate(DocumentEvent arg0) {
				if(dastext.getText().matches("[0-9]+")) {
					kl.DAS_TIME = Integer.parseInt(dastext.getText());
					kl.updateTimers();
				}
			}
			
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				if(dastext.getText().matches("[0-9]+")) {
					kl.DAS_TIME = Integer.parseInt(dastext.getText());
					kl.updateTimers();
				}
			}
		});
	}}
	
	protected Document downdoc = new IntegerDocument();
	protected JTextField downtext = new JTextField(downdoc, "50", 5);
	{{
		downdoc.addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				if(downtext.getText().matches("[0-9]+")) {
					kl.DOWN_TIME = Integer.parseInt(downtext.getText());
					kl.updateTimers();
				}
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				if(downtext.getText().matches("[0-9]+")) {
					kl.DOWN_TIME = Integer.parseInt(downtext.getText());
					kl.updateTimers();
				}
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				if(downtext.getText().matches("[0-9]+")) {
					kl.DOWN_TIME = Integer.parseInt(downtext.getText());
					kl.updateTimers();
				}
			}
		});
	}}
	
	protected JTextField playerName = new JTextField("web user");
	
	public TetrevilKeyPanel(TetrevilKeyListener kl) {
		super(new GridLayout(0, 1));
		this.kl = kl;
		try {
			JPanel p = new JPanel(new GridLayout(0, 4)); p.setBackground(Color.WHITE);
			p.add(new JLabel("LEFT")); p.add(new KeyButton(TetrevilKeyListener.class.getField("LEFT")));
			p.add(new JLabel("RIGHT")); p.add(new KeyButton(TetrevilKeyListener.class.getField("RIGHT")));
			add(p); p = new JPanel(new GridLayout(0, 4)); p.setBackground(Color.WHITE);
			p.add(new JLabel("<html>ROTATE<br>LEFT</html>")); p.add(new KeyButton(TetrevilKeyListener.class.getField("ROTATE_LEFT")));
			p.add(new JLabel("<html>ROTATE<br>RIGHT</html>")); p.add(new KeyButton(TetrevilKeyListener.class.getField("ROTATE_RIGHT")));
			add(p); p = new JPanel(new GridLayout(0, 4)); p.setBackground(Color.WHITE);
			p.add(new JLabel("DOWN")); p.add(new KeyButton(TetrevilKeyListener.class.getField("DOWN")));
			p.add(new JLabel("DROP")); p.add(new KeyButton(TetrevilKeyListener.class.getField("DROP")));
			add(p); p = new JPanel(new GridLayout(0, 4)); p.setBackground(Color.WHITE);
			p.add(new JLabel("<html>DAS<br>TIME</html>")); p.add(dastext);
			p.add(new JLabel("<html>DOWN<br>TIME</html>")); p.add(downtext);
			
			add(p);

			p = new JPanel(new GridLayout(0, 2)); p.setBackground(Color.WHITE);
			p.add(new JLabel("Player name:")); p.add(playerName);
			
			add(p);
			
			setBackground(Color.WHITE);
			for(int i = 0; i < getComponentCount(); i++) {
				if(getComponent(i) instanceof JLabel) {
					getComponent(i).setForeground(Color.BLACK);
				}
			}

		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void update() {
		for(int i = 0; i < getComponentCount(); i++) {
			if(getComponent(i) instanceof KeyButton) {
				((KeyButton) getComponent(i)).update();
			}
		}
		dastext.setText("" + kl.DAS_TIME);
		downtext.setText("" + kl.DOWN_TIME);
	}
	
	public String getPlayerName() {
		return playerName.getText();
	}
	
	public void setPlayerName(String playerName) {
		this.playerName.setText(playerName);
	}
}
