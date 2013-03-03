package org.eviline.console.gui;

import java.util.HashMap;
import java.util.Map;
import org.eviline.Field;
import org.eviline.ai.AIKernel;
import org.eviline.ai.DefaultPlayer;
import org.eviline.ai.PlayerFieldHarness;
import org.eviline.console.engine.ConsoleEngine;
import org.eviline.event.EvilineAdapter;
import org.eviline.event.EvilineEvent;
import org.eviline.event.EvilineListener;
import org.eviline.randomizer.Bag7Randomizer;
import org.eviline.randomizer.QueuedRandomizer;
import org.eviline.randomizer.Randomizer;
import com.googlecode.lanterna.gui.Action;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.listener.WindowAdapter;
import com.googlecode.lanterna.input.Key;

public class KeyboardInputHandler extends WindowAdapter {
	public static class Controls {
		public static final int PAUSE_EXIT = 0;
		public static final int SHIFT_DOWN = 1;
		public static final int SHIFT_LEFT = 2;
		public static final int SHIFT_RIGHT = 3;
		public static final int HARD_DROP = 4;
		public static final int ROTATE_LEFT = 5;
		public static final int ROTATE_RIGHT = 6;
		public static final int TOGGLE_AI = 7;
		public static final int TOGGLE_CONTROLS = 8;
		
		public static final Controls DEFAULT_CONTROLS = new Controls(
				PAUSE_EXIT, Key.Kind.Escape.getRepresentationKey(),
				SHIFT_DOWN, Key.Kind.ArrowDown.getRepresentationKey(),
				SHIFT_LEFT, Key.Kind.ArrowLeft.getRepresentationKey(),
				SHIFT_RIGHT, Key.Kind.ArrowRight.getRepresentationKey(),
				HARD_DROP, Key.Kind.ArrowUp.getRepresentationKey(),
				ROTATE_LEFT, 'z',
				ROTATE_RIGHT, 'x',
				TOGGLE_AI, 'a',
				TOGGLE_CONTROLS, 'm');
		
		public static final Controls MOBILE_CONTROLS = new Controls(
				PAUSE_EXIT, 'q',
				SHIFT_DOWN, 's',
				SHIFT_LEFT, 'a',
				SHIFT_RIGHT, 'd',
				HARD_DROP, 'w',
				ROTATE_LEFT, 'z',
				ROTATE_RIGHT, 'x',
				TOGGLE_AI, 't',
				TOGGLE_CONTROLS, 'm');
		
		protected char[] chars = new char[9];
		
		public Controls(int... ctrls) {
			for(int i = 0; i < ctrls.length - 1; i += 2) {
				chars[ctrls[i]] = (char) ctrls[i+1];
			}
		}
	}
	
	protected ConsoleEngine engine;
	
	protected Controls controls = Controls.DEFAULT_CONTROLS;
	
	protected Map<Key, Long> lastInputTimes = new HashMap<Key, Long>();
	protected long doubleTapTime = 200;
	
	protected Randomizer aiProvider;
	protected Randomizer playerProvider;
	protected EvilineListener aiScoreAdjuster = new EvilineAdapter() {
		@Override
		public void linesCleared(EvilineEvent e) {
			e.getField().setLines(e.getField().getLines() - 2 * e.getLines());
		}
	};
	
	public KeyboardInputHandler(ConsoleEngine engine) {
		this.engine = engine;
		
//		PropertySource mode = new BasicPropertySource(new Properties(RandomizerPresets.ANGELIC.getProperties()));
//		mode.put(RandomizerFactory.NEXT, "0");
//		aiProvider = new RandomizerFactory().newRandomizer(mode);
		
		aiProvider = new QueuedRandomizer(new Bag7Randomizer(), 1, true);
	}
	
	@Override
	public void onUnhandledKeyboardInteraction(Window window, Key key) {
		long now = System.currentTimeMillis();
		long lastInputTime = lastInputTimes.containsKey(key) ? lastInputTimes.get(key) : 0;
		boolean doubleTab = now - lastInputTime < doubleTapTime;
		lastInputTimes.put(key, now);
		
		if(key.getCharacter() == controls.chars[Controls.SHIFT_DOWN] && !doubleTab)
			engine.shiftDown();
		else if(key.getCharacter() == controls.chars[Controls.SHIFT_DOWN] && doubleTab)
			engine.softDrop();
		else if(key.getCharacter() == controls.chars[Controls.HARD_DROP])
			engine.hardDrop();
		else if(key.getCharacter() == controls.chars[Controls.SHIFT_LEFT] && !doubleTab)
			engine.shiftLeft();
		else if(key.getCharacter() == controls.chars[Controls.SHIFT_LEFT] && doubleTab)
			engine.dasLeft();
		else if(key.getCharacter() == controls.chars[Controls.SHIFT_RIGHT] && !doubleTab)
			engine.shiftRight();
		else if(key.getCharacter() == controls.chars[Controls.SHIFT_RIGHT] && doubleTab)
			engine.dasRight();
		else if(key.getCharacter() == controls.chars[Controls.ROTATE_LEFT])
			engine.rotateLeft();
		else if(key.getCharacter() == controls.chars[Controls.ROTATE_RIGHT])
			engine.rotateRight();
		else if(key.getCharacter() == controls.chars[Controls.PAUSE_EXIT])
			engine.promptExit();
		else if(key.getCharacter() == controls.chars[Controls.TOGGLE_AI]) {
			Field field = engine.getField();
			if(playerProvider == null) {
				playerProvider = field.getProvider();
				field.setProvider(aiProvider);
				field.addEvilineListener(aiScoreAdjuster);
				DefaultPlayer player = new DefaultPlayer(field, new AIKernel());
				player.setBlocking(true);
				final PlayerFieldHarness harness = new PlayerFieldHarness(field, player);
				final GUIScreen gui = window.getOwner();
				gui.runInEventThread(new Action() {
					@Override
					public void doAction() {
						if(playerProvider == null)
							return;
						harness.tick();
						gui.runInEventThread(this);
					}
				});
			} else {
				field.setProvider(playerProvider);
				field.removeEvilineListener(aiScoreAdjuster);
				playerProvider = null;
			}
		} else if(key.getCharacter() == controls.chars[Controls.TOGGLE_CONTROLS]) {
			if(controls == Controls.DEFAULT_CONTROLS)
				controls = Controls.MOBILE_CONTROLS;
			else
				controls = Controls.DEFAULT_CONTROLS;
		}
	}
}
