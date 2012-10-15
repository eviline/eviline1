package org.eviline.mp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eviline.randomizer.MaliciousRandomizer;

public class ClientSocketFactory {
	protected String server;
	
	public ClientSocketFactory(String server) {
		this.server = server;
	}
	
	public List<String> listGames() throws IOException {
		List<String> ret = new ArrayList<String>();
		HttpURLConnection http = (HttpURLConnection) new URL("http://" + server + "/tetrevil_tomcat/multiplayer").openConnection();
		
		http.setChunkedStreamingMode(1024);
		http.setRequestMethod("POST");
		http.setDoOutput(true);
		
		ObjectOutputStream out = new ObjectOutputStream(http.getOutputStream());
		try {
			out.writeObject("list");
			out.writeObject(null);
			out.writeObject(null);
		} finally {
			out.close();
		}
		
		http.connect();
		
		http.getHeaderFields();
		
		ObjectInputStream in = new ObjectInputStream(http.getInputStream());
		try {
			for(String hostName = (String) in.readObject(); hostName != null; hostName = (String) in.readObject()) {
				in.readObject(); // Randomizer
				in.readInt(); // port
				
				ret.add(hostName);
			}
		} catch(ClassNotFoundException c) {
			throw new IOException(c);
		}
		
		return ret;
	}
	
	public Socket newClientSocket(String name) throws IOException {
		HttpURLConnection http = (HttpURLConnection) new URL("http://" + server + "/tetrevil_tomcat/multiplayer").openConnection();
		
		http.setChunkedStreamingMode(1024);
		http.setRequestMethod("POST");
		http.setDoOutput(true);
		
		ObjectOutputStream out = new ObjectOutputStream(http.getOutputStream());
		try {
			out.writeObject("list");
			out.writeObject(name);
			out.writeObject(null);
		} finally {
			out.close();
		}
		
		http.connect();
		
		http.getHeaderFields();
		
		ObjectInputStream in = new ObjectInputStream(http.getInputStream());
		try {
			for(String hostName = (String) in.readObject(); hostName != null; hostName = (String) in.readObject()) {
				MaliciousRandomizer randomizer = (MaliciousRandomizer) in.readObject();
				int port = in.readInt();
				
				if(name.equals(hostName))
					return new Socket(server.split(":")[0], port);
			}
		} catch(ClassNotFoundException c) {
			throw new IOException(c);
		}
		
		return null;
	}
}
