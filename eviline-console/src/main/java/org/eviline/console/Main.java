package org.eviline.console;

import java.util.concurrent.Semaphore;

import org.eviline.Field;
import org.eviline.ai.AIKernel;
import org.eviline.ai.DefaultPlayer;
import org.eviline.ai.Player;
import org.eviline.ai.PlayerFieldHarness;
import org.eviline.console.engine.ConsoleEngine;
import org.eviline.console.gui.EvilineWindow;
import org.eviline.console.gui.FieldComponent;
import org.eviline.console.gui.FieldStatisticsPanel;
import org.eviline.randomizer.Bag7Randomizer;
import org.eviline.randomizer.QueuedRandomizer;
import org.eviline.randomizer.Randomizer;
import org.eviline.randomizer.RandomizerFactory;
import org.eviline.randomizer.RandomizerPresets;

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.gui.Action;
import com.googlecode.lanterna.gui.Border;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.GUIScreen.Position;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.component.Label;
import com.googlecode.lanterna.gui.component.Panel;
import com.googlecode.lanterna.gui.component.Panel.Orientation;
import com.googlecode.lanterna.gui.component.Separator;
import com.googlecode.lanterna.gui.dialog.MessageBox;
import com.googlecode.lanterna.gui.layout.BorderLayout;
import com.googlecode.lanterna.gui.layout.LayoutParameter;
import com.googlecode.lanterna.terminal.swing.SwingTerminal;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Randomizer r = new RandomizerFactory().newRandomizer(RandomizerPresets.AGGRESSIVE);
		
		final Field field = new Field();
		field.setProvider(new QueuedRandomizer(r, 1, true) );
		field.setGhosting(true);
		
		final GUIScreen gui = TerminalFacade.createGUIScreen();
		Window w = new EvilineWindow(field);
		
		new ConsoleEngine(field, gui).startEngine();
		
		gui.getScreen().startScreen();
		
		MessageBox.showMessageBox(
				gui, 
				"Controls", 
				"EVILINE Controls:\n\n" +
				"LEFT:           shift left one\n" +
				"DOUBLE-LEFT:    autoshift left\n" +
				"RIGHT:          shift right one\n" +
				"DOUBLE-RIGHT:   autoshift right\n" +
				"DOWN:           shift down one\n" +
				"DOUBLE-DOWN:    soft drop\n" +
				"UP:             hard drop\n" +
				"Z:              rotate left\n" +
				"X:              rotate right\n" +
				"ESC:            pause/exit");
		
		gui.showWindow(w, Position.CENTER);
	}

}
