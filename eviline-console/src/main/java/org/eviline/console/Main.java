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

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.gui.Action;
import com.googlecode.lanterna.gui.Border;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.component.Label;
import com.googlecode.lanterna.gui.component.Panel;
import com.googlecode.lanterna.gui.component.Panel.Orientation;
import com.googlecode.lanterna.gui.component.Separator;
import com.googlecode.lanterna.gui.layout.BorderLayout;
import com.googlecode.lanterna.gui.layout.LayoutParameter;
import com.googlecode.lanterna.terminal.swing.SwingTerminal;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final Field field = new Field();
		field.setProvider(new QueuedRandomizer(new Bag7Randomizer(), 1, true) );
		Player player = new DefaultPlayer(field, new AIKernel());
		final PlayerFieldHarness harness = new PlayerFieldHarness(field, player);
		
		final GUIScreen gui = TerminalFacade.createGUIScreen();
		Window w = new EvilineWindow(field);
		
		new ConsoleEngine(field, gui).startEngine();
		
		gui.getScreen().startScreen();
		gui.showWindow(w);
	}

}
