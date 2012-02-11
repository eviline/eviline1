package org.tetrevil.wobj;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public class WebScore implements Serializable {
	private static final long serialVersionUID = 0;
	
	public static final int PORT = 24308;
	
	public static final String COMMAND = "command";
	public static final String SUBMIT_SCORE = "submit_score";
	public static final String HIGH_SCORE = "high_score";
	
	public static final String SCORE = "score";
	
	public int lines;
	public String name;
	public long ts;
	
	@Override
	public String toString() {
		return "WebScore[" + lines + "," + name + "," + ts + "]";
	}
	
	public static void submit(WebScore score, String host) throws IOException {
		Map<String, Object> mreq = new HashMap<String, Object>();
		mreq.put(COMMAND, SUBMIT_SCORE);
		mreq.put(SCORE, score);

		Socket socket = new Socket(host, PORT);
		try {
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			out.writeObject(mreq);
			out.flush();
		} finally {
			socket.close();
		}
		
	}
	
	public static WebScore highScore(String host) throws IOException {
		Map<String, Object> mreq = new HashMap<String, Object>();
		mreq.put(COMMAND, HIGH_SCORE);
		
		Socket socket = new Socket(host, PORT);

		try {
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			out.writeObject(mreq);
			out.flush();
		
			return (WebScore) new ObjectInputStream(socket.getInputStream()).readObject();
		} catch (ClassNotFoundException e) {
			throw new IOException(e);
		} finally {
			socket.close();
		}
	}

	public static void main(String[] args) throws IOException{
		WebScore score = highScore("localhost");
		submit(score, "localhost");
	}
}
