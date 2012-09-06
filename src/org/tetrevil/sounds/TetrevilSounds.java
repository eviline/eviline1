package org.tetrevil.sounds;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;

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
	
	private static SourceDataLine music;
	private static byte[] musicBuffer;
	private static int BUFFER_SIZE = 65536 * 4;
	private static int frameSize;
	private static int musicBufferPosition = 0;
	private static boolean musicPaused = true;
	static {
		try {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			byte[] buf = new byte[8192];
			final AudioInputStream in = AudioSystem.getAudioInputStream(TetrevilSounds.class.getResource("Tetris.wav"));
			for(int r = in.read(buf); r != -1; r = in.read(buf))
				bout.write(buf, 0, r);
			musicBuffer = bout.toByteArray();

			frameSize = in.getFormat().getFrameSize();
			
			DataLine.Info info = new DataLine.Info(SourceDataLine.class, in.getFormat());
			music = (SourceDataLine) AudioSystem.getLine(info);
			music.open(in.getFormat());
			new Thread(new Runnable() {
				@Override
				public void run() {
					while(true) {
						while(musicPaused) {
							try { Thread.sleep(100); }
							catch(InterruptedException ie) {}
						}
						fillMusicLine();
					}
				}
			}).start();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private static void fillMusicLine() {
		synchronized(musicBuffer) {
			if(musicPaused)
				return;
		}
		System.out.println("Buffer pos:" + musicBufferPosition);
		int length = Math.min(BUFFER_SIZE, musicBuffer.length - musicBufferPosition);
		//		length = Math.min(music.available(), length);
		int frames = length / frameSize;
		length = frames * frameSize;
		length = music.write(musicBuffer, musicBufferPosition, length);
		musicBufferPosition += length;
		if(musicBufferPosition >= musicBuffer.length)
			musicBufferPosition = 0;
	}
	
	public static boolean isMusicPaused() {
		return musicPaused;
	}
	
	public static void setMusicPaused(boolean musicPaused) {
		synchronized(musicBuffer) {
			System.out.println("Setting musicPaused to:" + musicPaused);
			TetrevilSounds.musicPaused = musicPaused;
			if(musicPaused)
				music.stop();
			else {
				music.start();
			}
		}
	}
}
