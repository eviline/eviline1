package org.tetrevil.sounds;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.sound.sampled.Clip;

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
	
	public void play(final Clip clip) {
		if(!ingame)
			return;
		if(clip == null)
			return;
		
		executor.execute(new Runnable() {
			@Override
			public void run() {
				clip.start();
			}
		});
	}
	
	public void pause(final Clip clip) {
		if(clip == null)
			return;
		executor.execute(new Runnable() {
			@Override
			public void run() {
				clip.stop();
			}
		});
	}
	
	@Override
	public void clockTicked(TetrevilEvent e) {
		if(!ingame) {
			ingame = true;
			TetrevilSounds.setMusicPaused(false);
			play(TetrevilSounds.getMusic());
		}
		ingame = true;
	}
	
	@Override
	public void gameReset(TetrevilEvent e) {
		ingame = false;
		TetrevilSounds.setMusicPaused(true);
		pause(TetrevilSounds.getMusic());
		TetrevilSounds.getMusic().setFramePosition(0);
	}
	
	@Override
	public void gameOver(TetrevilEvent e) {
		ingame = false;
		TetrevilSounds.setMusicPaused(true);
		pause(TetrevilSounds.getMusic());
		TetrevilSounds.getMusic().setFramePosition(0);
	}
	
	@Override
	public void gamePaused(TetrevilEvent e) {
		if(e.getField().isPaused()) {
			ingame = false;
			TetrevilSounds.setMusicPaused(true);
			pause(TetrevilSounds.getMusic());
		} else {
			if(!ingame) {
				ingame = true;
				TetrevilSounds.setMusicPaused(false);
				play(TetrevilSounds.getMusic());
			}
			ingame = true;
		}
	}
}
