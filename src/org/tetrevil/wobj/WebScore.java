package org.tetrevil.wobj;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.tetrevil.Field;
import org.tetrevil.MaliciousRandomizer;

/**
 * A score that gets submitted to the tetrevil score server
 * @author robin
 *
 */
public class WebScore implements Serializable {
	private static final long serialVersionUID = 4;
	
	public static final int PORT = 24309;
	
	public static final String COMMAND = "command";
	public static final String SUBMIT_SCORE = "submit_score";
	public static final String HIGH_SCORE = "high_score";
	
	public static final String SCORE = "score";
	
	protected Integer score;
	protected String name;
	protected Date ts;
	
	protected Integer depth;
	protected Double rfactor;
	protected Integer fair;
	protected Integer distribution;
	protected Integer adaptive;
	
	protected String randomizer;
	
	protected String reason;
	
	public WebScore() {}
	
	public WebScore(Field field) {
		score = field.getLines();
		ts = new Date();
		MaliciousRandomizer r = field.getProvider().getMaliciousRandomizer();
		if(r != null) {
			depth = r.getDepth();
			rfactor = r.getRfactor();
			fair = r.isFair() ? 1 : 0;
			distribution = r.getDistribution();
			adaptive = r.isAdaptive() ? 1 : 0;
		}
		randomizer = field.getProvider().getRandomizerName();
	}
	
	@Override
	public String toString() {
		return "WebScore[" + score + "," + name + "," + ts + "]";
	}
	
	public void submit(String host) throws IOException {
		submit(this, host);
	}
	
	/**
	 * Submit a high score
	 * @param score
	 * @param host
	 * @throws IOException
	 */
	private static void submit(WebScore score, String host) throws IOException {
		Map<String, Object> mreq = new HashMap<String, Object>();
		mreq.put(COMMAND, SUBMIT_SCORE);
		mreq.put(SCORE, score);

//		Socket socket = new Socket(host, PORT);
//		try {
//			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
//			out.writeObject(mreq);
//			out.flush();
//		} finally {
//			socket.close();
//		}
		
		HttpURLConnection http = (HttpURLConnection) new URL("http://" + host + "/tetrevil_tomcat/score").openConnection();
		
		http.setChunkedStreamingMode(1024);
		http.setRequestMethod("POST");
		http.setDoOutput(true);
		
		try {
			ObjectOutputStream out = new ObjectOutputStream(http.getOutputStream());
			out.writeObject(mreq);
			out.flush();
		} finally {
			http.getOutputStream().close();
		}
		
		http.connect();
	}
	
	public WebScore highScore(String host) throws IOException {
		return highScore(this, host);
	}
	
	/**
	 * Return the high score matching the argument score parameters
	 * @param params
	 * @param host
	 * @return
	 * @throws IOException
	 */
	private static WebScore highScore(WebScore params, String host) throws IOException {
		Map<String, Object> mreq = new HashMap<String, Object>();
		mreq.put(COMMAND, HIGH_SCORE);
		mreq.put(SCORE, params);
		
//		Socket socket = new Socket(host, PORT);
//
//		try {
//			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
//			out.writeObject(mreq);
//			out.flush();
//		
//			return (WebScore) new ObjectInputStream(socket.getInputStream()).readObject();
//		} catch (ClassNotFoundException e) {
//			throw new IOException(e);
//		} finally {
//			socket.close();
//		}
		
		HttpURLConnection http = (HttpURLConnection) new URL("http://" + host + "/tetrevil_tomcat/score").openConnection();
		
		http.setChunkedStreamingMode(1024);
		http.setRequestMethod("POST");
		http.setDoOutput(true);
		
		try {
			ObjectOutputStream out = new ObjectOutputStream(http.getOutputStream());
			out.writeObject(mreq);
			out.flush();
		} finally {
			http.getOutputStream().close();
		}
		
		http.connect();
		
		try {
			http.getHeaderFields();
			return (WebScore) new ObjectInputStream(http.getInputStream()).readObject();
		} catch(ClassNotFoundException cnfe) {
			throw new IOException(cnfe);
		} 
	}

//	public static void main(String[] args) throws IOException{
//		WebScore score = highScore("localhost");
//		System.out.println(score);
//		submit(score, "localhost");
//	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getTs() {
		return ts;
	}

	public void setTs(Date ts) {
		this.ts = ts;
	}

	public Integer getDepth() {
		return depth;
	}

	public void setDepth(Integer depth) {
		this.depth = depth;
	}

	public Double getRfactor() {
		return rfactor;
	}

	public void setRfactor(Double rfactor) {
		this.rfactor = rfactor;
	}

	public Integer getFair() {
		return fair;
	}

	public void setFair(Integer fair) {
		this.fair = fair;
	}

	public Integer getDistribution() {
		return distribution;
	}

	public void setDistribution(Integer distribution) {
		this.distribution = distribution;
	}

	public String getRandomizer() {
		return randomizer;
	}

	public void setRandomizer(String randomizer) {
		this.randomizer = randomizer;
	}

	public Integer getAdaptive() {
		return adaptive;
	}

	public void setAdaptive(Integer adaptive) {
		this.adaptive = adaptive;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
}
