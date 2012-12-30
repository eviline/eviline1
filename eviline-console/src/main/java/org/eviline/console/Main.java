package org.eviline.console;

import java.awt.TrayIcon.MessageType;
import java.util.Properties;
import java.util.concurrent.Semaphore;

import org.eviline.BasicPropertySource;
import org.eviline.Field;
import org.eviline.PropertySource;
import org.eviline.ai.AIKernel;
import org.eviline.ai.DefaultPlayer;
import org.eviline.ai.Player;
import org.eviline.ai.PlayerFieldHarness;
import org.eviline.console.engine.ConsoleEngine;
import org.eviline.console.gui.EvilineWindow;
import org.eviline.console.gui.FieldComponent;
import org.eviline.console.gui.FieldStatisticsPanel;
import org.eviline.event.TetrevilAdapter;
import org.eviline.event.TetrevilEvent;
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
import com.googlecode.lanterna.gui.dialog.DialogButtons;
import com.googlecode.lanterna.gui.dialog.DialogResult;
import com.googlecode.lanterna.gui.dialog.MessageBox;
import com.googlecode.lanterna.gui.layout.BorderLayout;
import com.googlecode.lanterna.gui.layout.LayoutParameter;
import com.googlecode.lanterna.terminal.swing.SwingTerminal;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PropertySource mode = new BasicPropertySource(new Properties(RandomizerPresets.EVIL.getProperties()));
		mode.put(RandomizerFactory.NEXT, "1");
		Randomizer r = new RandomizerFactory().newRandomizer(mode);
		
		final Field field = new Field();
		field.setProvider(new QueuedRandomizer(r, 1, true) );
		field.setGhosting(true);
		
		
		final GUIScreen gui = TerminalFacade.createGUIScreen();
		ConsoleEngine engine = new ConsoleEngine(field, gui);

		Window w = new EvilineWindow(engine);
		
		gui.getScreen().startScreen();
		
		MessageBox.showMessageBox(
				gui, 
				"Controls", 
				"             EVILINE Controls:\n\n" +
				"Default Key         Action         Mobile Key\n" + 
				"LEFT:           shift left one             :A\n" +
				"DOUBLE-LEFT:    autoshift left      :DOUBLE-A\n" +
				"RIGHT:          shift right one            :D\n" +
				"DOUBLE-RIGHT:   autoshift right     :DOUBLE-D\n" +
				"DOWN:           shift down one             :S\n" +
				"DOUBLE-DOWN:    soft drop           :DOUBLE-S\n" +
				"UP:             hard drop                  :W\n" +
				"Z:              rotate left                :Z\n" +
				"X:              rotate right               :X\n" +
				"A:              toggle AI                  :T\n" +
				"ESC:            pause/exit                 :Q\n" +
				"M:              toggle mobile controls     :M");
		
		engine.startEngine();
		gui.showWindow(w, Position.CENTER);
	}

}
