package org.eviline.console;

import org.eviline.Field;
import org.eviline.ai.AIKernel;
import org.eviline.ai.DefaultPlayer;
import org.eviline.ai.Player;
import org.eviline.ai.PlayerFieldHarness;
import org.eviline.console.gui.FieldComponent;
import org.eviline.randomizer.Bag7Randomizer;

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.gui.Border;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.component.Panel;
import com.googlecode.lanterna.gui.component.Panel.Orientation;
import com.googlecode.lanterna.gui.layout.BorderLayout;
import com.googlecode.lanterna.gui.layout.LayoutParameter;
import com.googlecode.lanterna.terminal.swing.SwingTerminal;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final Field field = new Field();
		field.setProvider(new Bag7Randomizer());
		Player player = new DefaultPlayer(field, new AIKernel());
		final PlayerFieldHarness harness = new PlayerFieldHarness(field, player);
		
		final GUIScreen gui = TerminalFacade.createGUIScreen();
		gui.getScreen().startScreen();
		Window w = new Window("EVILINE");
		Panel p = new Panel(new Border.Invisible(), Orientation.HORISONTAL);
		p.setLayoutManager(new BorderLayout());
		p.addComponent(new FieldComponent(field), BorderLayout.LEFT);
		w.addComponent(p);

		Runnable ticker = new Runnable() {
			@Override
			public void run() {
				while(!field.isGameOver()) {
					synchronized(field) {
						harness.tick();
					}
					gui.invalidate();
				}
			}
		};
		
		new Thread(ticker).start();
		
		gui.showWindow(w);
	}

}
