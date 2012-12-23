package org.eviline.console.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.eviline.BasicPropertySource;
import org.eviline.Field;
import org.eviline.PropertySource;
import org.eviline.ShapeDirection;
import org.eviline.ai.AIKernel;
import org.eviline.ai.DefaultPlayer;
import org.eviline.ai.PlayerFieldHarness;
import org.eviline.event.TetrevilAdapter;
import org.eviline.event.TetrevilEvent;
import org.eviline.event.TetrevilListener;
import org.eviline.randomizer.Bag7Randomizer;
import org.eviline.randomizer.Randomizer;
import org.eviline.randomizer.RandomizerFactory;
import org.eviline.randomizer.RandomizerPresets;

import com.googlecode.lanterna.gui.Action;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.dialog.DialogButtons;
import com.googlecode.lanterna.gui.dialog.DialogResult;
import com.googlecode.lanterna.gui.dialog.MessageBox;
import com.googlecode.lanterna.gui.listener.WindowAdapter;
import com.googlecode.lanterna.input.InputProvider;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.input.Key.Kind;

public class KeyboardInputHandler extends WindowAdapter {
	protected Field field;
	
	protected Map<Key, Long> lastInputTimes = new HashMap<Key, Long>();
	protected long doubleTapTime = 200;
	
	protected Randomizer aiProvider;
	protected Randomizer playerProvider;
	protected TetrevilListener aiScoreAdjuster = new TetrevilAdapter() {
		@Override
		public void linesCleared(TetrevilEvent e) {
			e.getField().setLines(e.getField().getLines() - 2 * e.getLines());
		}
	};
	
	public KeyboardInputHandler(Field field) {
		this.field = field;
		
//		PropertySource mode = new BasicPropertySource(new Properties(RandomizerPresets.ANGELIC.getProperties()));
//		mode.put(RandomizerFactory.NEXT, "0");
//		aiProvider = new RandomizerFactory().newRandomizer(mode);
		
		aiProvider = new Bag7Randomizer();
	}
	
	@Override
	public void onUnhandledKeyboardInteraction(Window window, Key key) {
		long now = System.currentTimeMillis();
		long lastInputTime = lastInputTimes.containsKey(key) ? lastInputTimes.get(key) : 0;
		boolean doubleTab = now - lastInputTime < doubleTapTime;
		lastInputTimes.put(key, now);
		
		if(key.getKind() == Kind.ArrowDown && !doubleTab)
			shiftDown();
		else if(key.getKind() == Kind.ArrowDown && doubleTab)
			softDrop();
		else if(key.getKind() == Kind.ArrowUp)
			hardDrop();
		else if(key.getKind() == Kind.ArrowLeft && !doubleTab)
			shiftLeft();
		else if(key.getKind() == Kind.ArrowLeft && doubleTab)
			dasLeft();
		else if(key.getKind() == Kind.ArrowRight && !doubleTab)
			shiftRight();
		else if(key.getKind() == Kind.ArrowRight && doubleTab)
			dasRight();
		else if(key.getCharacter() == 'z')
			rotateLeft();
		else if(key.getCharacter() == 'x')
			rotateRight();
		else if(key.getKind() == Kind.Escape)
			promptExit(window);
		else if(key.getCharacter() == 'a') {
			if(playerProvider == null) {
				playerProvider = field.getProvider();
				field.setProvider(aiProvider);
				field.addTetrevilListener(aiScoreAdjuster);
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
				field.removeTetrevilListener(aiScoreAdjuster);
				playerProvider = null;
			}
		}
	}
	
	protected void promptExit(Window window) {
		field.setPaused(true);
		DialogResult r = MessageBox.showMessageBox(window.getOwner(), "Really Quit?", "Really quit EVILINE?", DialogButtons.YES_NO);
		if(r == DialogResult.YES) {
			window.getOwner().getScreen().stopScreen();
			window.getOwner().getScreen().clear();
			window.getOwner().getScreen().getTerminal().setCursorVisible(true);
			System.exit(0);
		}
		field.setPaused(false);
	}
	
	protected void shiftDown() {
		field.clockTick();
	}
	
	protected void softDrop() {
		while(!field.isGrounded())
			field.clockTick();
	}
	
	protected void hardDrop() {
		softDrop();
		field.clockTick();
	}
	
	protected void shiftLeft() {
		field.shiftLeft();
	}
	
	protected void shiftRight() {
		field.shiftRight();
	}
	
	protected void dasLeft() {
		field.setAutoShift(ShapeDirection.LEFT);
		field.autoshift();
		field.setAutoShift(null);
	}
	
	protected void dasRight() {
		field.setAutoShift(ShapeDirection.RIGHT);
		field.autoshift();
		field.setAutoShift(null);
	}
	
	protected void rotateLeft() {
		field.rotateLeft();
	}
	
	protected void rotateRight() {
		field.rotateRight();
	}
}
