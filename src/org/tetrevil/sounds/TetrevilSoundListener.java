package org.tetrevil.sounds;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.sound.sampled.Clip;
import org.tetrevil.ShapeType;
import org.tetrevil.event.TetrevilAdapter;
import org.tetrevil.event.TetrevilEvent;

public class TetrevilSoundListener extends TetrevilAdapter {
	private ExecutorService executor = Executors.newCachedThreadPool();
	
	public TetrevilSoundListener() {
		try {
			Class.forName(TetrevilSounds.class.getName());
		} catch(Exception ex) {
		}
	}
	
	private void play(final Clip clip) {
		if(clip == null)
			return;
		executor.execute(new Runnable() {
			@Override
			public void run() {
				clip.setFramePosition(0);
				clip.start();
			}
		});
	}
	
	@Override
	public void shapeSpawned(TetrevilEvent e) {
		ShapeType type = e.getField().getShape().type();
		Clip clip = TetrevilSounds.getShapeSpawn(type);
		play(clip);
	}
	
	@Override
	public void linesCleared(TetrevilEvent e) {
		play(TetrevilSounds.getLinesCleared(e.getLines()));
	}
	
	@Override
	public void shiftedLeft(TetrevilEvent e) {
		play(TetrevilSounds.getShift());
	}
	
	@Override
	public void shiftedRight(TetrevilEvent e) {
		play(TetrevilSounds.getShift());
	}
	
	@Override
	public void rotatedLeft(TetrevilEvent e) {
		play(TetrevilSounds.getRotate());
	}
	
	@Override
	public void rotatedRight(TetrevilEvent e) {
		play(TetrevilSounds.getRotate());
	}
	
	@Override
	public void shapeLocked(TetrevilEvent e) {
		play(TetrevilSounds.getLock());
	}
}
