package org.tetrevil.sounds;

import java.io.IOException;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;

import org.tetrevil.ShapeType;

public class TetrevilSounds {
	private static Clip[] shapeSpawns = new Clip[ShapeType.values().length];
	
	static {
		for(int i = 0; i < ShapeType.values().length; i++) {
			try {
				Clip clip = shapeSpawns[i] = AudioSystem.getClip();
				clip.open(AudioSystem.getAudioInputStream(TetrevilSounds.class.getResource("piece" + i + ".wav")));
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public static Clip getShapeSpawn(ShapeType type) {
		return shapeSpawns[type.ordinal()];
	}
	
	private static Clip[] linesCleared = new Clip[4];
	
	static {
		for(int i = 1; i <= 4; i++) {
			try {
				Clip clip = linesCleared[i-1] = AudioSystem.getClip();
				clip.open(AudioSystem.getAudioInputStream(TetrevilSounds.class.getResource("erase" + i + ".wav")));
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public static Clip getLinesCleared(int lines) {
		if(lines <= 0 || lines > 4)
			return null;
		return linesCleared[lines - 1];
	}
	
	private static Clip shift;
	private static Clip rotate;
	private static Clip lock;
	
	static {
		try {
			shift = AudioSystem.getClip();
			rotate = AudioSystem.getClip();
			lock = AudioSystem.getClip();
			shift.open(AudioSystem.getAudioInputStream(TetrevilSounds.class.getResource("step.wav")));
			rotate.open(AudioSystem.getAudioInputStream(TetrevilSounds.class.getResource("rotate.wav")));
			lock.open(AudioSystem.getAudioInputStream(TetrevilSounds.class.getResource("lock.wav")));
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static Clip getShift() {
		return shift;
	}
	
	public static Clip getRotate() {
		return rotate;
	}
	
	public static Clip getLock() {
		return lock;
	}
}
