package org.tetrevil.runner;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

import org.tetrevil.Field;
import org.tetrevil.swing.TetrevilComponent;
import org.tetrevil.swing.TetrevilFrame;
import org.tetrevil.swing.TetrevilKeyPanel;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		Field field = new Field(true);
		field.setGhosting(true);

		final TetrevilFrame frame = new TetrevilFrame(field);

		try {
			frame.getParameters().load(new FileInputStream(System.getProperty("user.home") + File.separator + ".tetrevilrc"));
		} catch(IOException ioe) {
		}
		
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
			}
		}));
	}

}
