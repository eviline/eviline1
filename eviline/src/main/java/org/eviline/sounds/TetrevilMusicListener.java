package org.eviline.sounds;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eviline.event.EvilineAdapter;
import org.eviline.event.EvilineEvent;

public class TetrevilMusicListener extends EvilineAdapter {
	private ExecutorService executor = Executors.newCachedThreadPool();
	
	public TetrevilMusicListener() {
		try {
			Class.forName(TetrevilSounds.class.getName());
		} catch(Exception ex) {
		}
	}
	
	protected boolean ingame = false;
	
	@Override
	public void clockTicked(EvilineEvent e) {
		if(!ingame) {
			ingame = true;
			TetrevilSounds.setMusicPaused(false);
		}
		ingame = true;
	}
	
	@Override
	public void gameReset(EvilineEvent e) {
		ingame = false;
		TetrevilSounds.setMusicPaused(true);
	}
	
	@Override
	public void gameOver(EvilineEvent e) {
		ingame = false;
		TetrevilSounds.setMusicPaused(true);
	}
	
	@Override
	public void gamePaused(EvilineEvent e) {
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
