package org.eviline.sounds;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.sound.sampled.Clip;

import org.eviline.ShapeType;
import org.eviline.event.EvilineAdapter;
import org.eviline.event.EvilineEvent;

public class TetrevilSoundListener extends EvilineAdapter {
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
	public void shapeSpawned(EvilineEvent e) {
		ShapeType type = e.getField().getShape().type();
		Clip clip = TetrevilSounds.getShapeSpawn(type);
		play(clip);
	}
	
	@Override
	public void linesCleared(EvilineEvent e) {
		play(TetrevilSounds.getLinesCleared(e.getLines()));
	}
	
	@Override
	public void shiftedLeft(EvilineEvent e) {
		play(TetrevilSounds.getShift());
	}
	
	@Override
	public void shiftedRight(EvilineEvent e) {
		play(TetrevilSounds.getShift());
	}
	
	@Override
	public void rotatedLeft(EvilineEvent e) {
		play(TetrevilSounds.getRotate());
	}
	
	@Override
	public void rotatedRight(EvilineEvent e) {
		play(TetrevilSounds.getRotate());
	}
	
	@Override
	public void shapeLocked(EvilineEvent e) {
		play(TetrevilSounds.getLock());
	}
}
