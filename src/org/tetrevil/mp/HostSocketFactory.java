package org.tetrevil.mp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;

import org.tetrevil.MaliciousRandomizer;

public class HostSocketFactory {
	protected String server;
	
	public HostSocketFactory(String server) {
		this.server = server;
	}
	
	public Socket newHostSocket(String name, MaliciousRandomizer randomizer, boolean privateGame) throws IOException {
		HttpURLConnection http = (HttpURLConnection) new URL("http://" + server + "/tetrevil_tomcat/multiplayer").openConnection();
		
		http.setChunkedStreamingMode(1024);
		http.setRequestMethod("POST");
		http.setDoOutput(true);
		
		ObjectOutputStream out = new ObjectOutputStream(http.getOutputStream());
		try {
			out.writeObject("host");
			out.writeObject(name);
			out.writeObject(randomizer);
			out.writeBoolean(privateGame);
		} finally {
			out.close();
		}
		
		http.connect();
		
		http.getHeaderFields();
		
		ObjectInputStream in = new ObjectInputStream(http.getInputStream());
		
		int port = in.readInt();
		
		return new Socket(server.split(":")[0], port);
	}
}
