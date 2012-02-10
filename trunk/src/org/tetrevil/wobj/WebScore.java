package org.tetrevil.wobj;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public class WebScore implements Serializable {
	private static final long serialVersionUID = 0;
	
	public static final String COMMAND = "command";
	public static final String SUBMIT_SCORE = "submit_score";
	public static final String HIGH_SCORE = "high_score";
	
	public static final String SCORE = "score";
	
	public int lines;
	public String name;
	public long ts;
	
	public static void submit(WebScore score, URL url) throws IOException {
		HttpURLConnection http = (HttpURLConnection) url.openConnection();
		http.setDoInput(true);
		http.setDoOutput(true);
		http.setRequestMethod("POST");
		
		Map<String, Object> mreq = new HashMap<String, Object>();
		mreq.put(COMMAND, SUBMIT_SCORE);
		mreq.put(SCORE, score);

		http.setChunkedStreamingMode(256);
		
		http.connect();
		
		ObjectOutputStream out = new ObjectOutputStream(http.getOutputStream());
		out.writeObject(mreq);
		out.flush();
		out.close();
		
	}
	
	public static WebScore highScore(URL url) throws IOException {
		HttpURLConnection http = (HttpURLConnection) url.openConnection();
		http.setDoInput(true);
		http.setDoOutput(true);
		http.setRequestMethod("POST");
		
		Map<String, Object> mreq = new HashMap<String, Object>();
		mreq.put(COMMAND, HIGH_SCORE);

		http.setChunkedStreamingMode(256);
		
		http.connect();
		
		ObjectOutputStream out = new ObjectOutputStream(http.getOutputStream());
		out.writeObject(mreq);
		out.flush();
		out.close();
		
		try {
			return (WebScore) new ObjectInputStream(http.getInputStream()).readObject();
		} catch (ClassNotFoundException e) {
			throw new IOException(e);
		}
	}
}
