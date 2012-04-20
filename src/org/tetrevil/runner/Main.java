package org.tetrevil.runner;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import org.tetrevil.Field;
import org.tetrevil.mp.ClientGameButton;
import org.tetrevil.mp.HostGameButton;
import org.tetrevil.swing.TetrevilComponent;
import org.tetrevil.swing.TetrevilFrame;
import org.tetrevil.swing.TetrevilKeyPanel;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		try {
//			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		final Field field = new Field(true);
		field.setGhosting(true);

		Properties p = new Properties();
		try {
			p.load(new FileInputStream(System.getProperty("user.home") + File.separator + ".tetrevilrc"));
		} catch(IOException ioe) {
		}
		
		final TetrevilFrame frame = new TetrevilFrame(field, p);

		
		for(String arg : args) {
			if(arg.contains("=")) {
				String[] f = arg.split("=", 2);
				frame.setParameter(f[0], f[1]);
			}
		}
		
		if(frame.getParameter("score_host") == null)
			frame.setParameter("score_host", "www.tetrevil.org:8080");
		
		boolean fullscreen = Boolean.parseBoolean(frame.getParameters().getProperty("fullscreen", "false"));
		
		frame.init();
		
//		frame.getCenter().add(new JLabel("<html>HAHAHA<br>AHAHAH</html>"));
		
		frame.getCenter().add(new JButton(new AbstractAction("Pause") {
			@Override
			public void actionPerformed(ActionEvent e) {
				field.setPaused(true);
			}
		}));
		frame.getCenter().add(new JButton(new AbstractAction("Resume") {
			@Override
			public void actionPerformed(ActionEvent e) {
				field.setPaused(false);
			}
		}));
		frame.getCenter().add(new JButton(new AbstractAction("Reset") {
			@Override
			public void actionPerformed(ActionEvent e) {
				field.reset();
			}
		}));
		
		frame.getCenter().add(new JButton(new AbstractAction("Quit") {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		}));
		
		frame.getCenter().add(new HostGameButton(frame));
		frame.getCenter().add(new ClientGameButton(frame));
		
		if(fullscreen)
			frame.setUndecorated(true);
		frame.pack();
		frame.setVisible(true);
		
		if(fullscreen) {
			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
				}
			});
		}
		
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				frame.setParamsFromKeys();
				try {
					frame.getParameters().store(new FileOutputStream(System.getProperty("user.home") + File.separator + ".tetrevilrc"), "Tetrevil Settings");
				} catch(IOException ioe) {
				}
				if(!field.isGameOver())
					frame.submitScore("Quit");
			}
		}));
	}

}
