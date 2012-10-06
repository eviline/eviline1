package org.tetrevil.sounds;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.tetrevil.event.TetrevilAdapter;
import org.tetrevil.event.TetrevilEvent;

public class TetrevilMusicListener extends TetrevilAdapter {
	private ExecutorService executor = Executors.newCachedThreadPool();
	
	public TetrevilMusicListener() {
		try {
			Class.forName(TetrevilSounds.class.getName());
		} catch(Exception ex) {
		}
	}
	
	protected boolean ingame = false;
	
	@Override
	public void clockTicked(TetrevilEvent e) {
		if(!ingame) {
			ingame = true;
			TetrevilSounds.setMusicPaused(false);
		}
		ingame = true;
	}
	
	@Override
	public void gameReset(TetrevilEvent e) {
		ingame = false;
		TetrevilSounds.setMusicPaused(true);
	}
	
	@Override
	public void gameOver(TetrevilEvent e) {
		ingame = false;
		TetrevilSounds.setMusicPaused(true);
	}
	
	@Override
	public void gamePaused(TetrevilEvent e) {
		if(e.getField().isPaused()) {
			ingame = false;
			TetrevilSounds.setMusicPaused(true);
		} else {
			if(!ingame) {
				ingame = true;
			}
			TetrevilSounds.setMusicPaused(false);
			ingame = true;
		}
	}
}
