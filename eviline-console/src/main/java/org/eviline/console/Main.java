package org.eviline.console;

import java.util.Properties;
import org.eviline.BasicPropertySource;
import org.eviline.Field;
import org.eviline.PropertySource;
import org.eviline.ai.AI;
import org.eviline.ai.AIKernel;
//import org.eviline.clj.ClojureAIKernel;
import org.eviline.console.engine.ConsoleEngine;
import org.eviline.console.gui.EvilineWindow;
import org.eviline.fitness.AbstractFitness;
import org.eviline.randomizer.QueuedRandomizer;
import org.eviline.randomizer.Randomizer;
import org.eviline.randomizer.RandomizerFactory;
import org.eviline.randomizer.RandomizerPresets;

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.GUIScreen.Position;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.dialog.MessageBox;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PropertySource mode = new BasicPropertySource(new Properties(RandomizerPresets.EVIL.getProperties()));
		mode.put(RandomizerFactory.NEXT, "1");
		mode.put(RandomizerFactory.CONCURRENT, "true");
		Randomizer r = new RandomizerFactory().newRandomizer(mode);
		
		final Field field = new Field();
		field.setProvider(r);
		field.setGhosting(true);
		
//		// FIXME: Shouldn't normally be using ClojureFitness
//		if(System.getProperty("eviline.clojure") != null) {
////			AbstractFitness.setDefaultInstance(new WrapperFitness(new ClojureFitness()));
//			AI.setInstance(new ClojureAIKernel(AbstractFitness.getDefaultInstance()));
//		}
		
		if(System.getProperty("eviline.aikernel", "org.eviline.clj.ClojureAIKernel") != null) {
			String akc = System.getProperty("eviline.aikernel", "org.eviline.clj.ClojureAIKernel");
			try {
				AI.setInstance((AIKernel) Class.forName(akc, true, Main.class.getClassLoader()).newInstance());
				System.out.println("Set AIKernel to " + AI.getInstance());
			} catch(Exception ex) {
			}
		}
		
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
