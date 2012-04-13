package org.tetrevil;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RemoteRandomizer extends MaliciousRandomizer {
	
	protected String server;
	
	public RemoteRandomizer() {
		this("www.tetrevil.org:8080");
	}
	
	public RemoteRandomizer(int depth, int dist) {
		this("www.tetrevil.org:8080");
	}
	
	public RemoteRandomizer(String server) {
		this.server = server;
	}
	
	@Override
	public Shape provideShape(Field field) {
		try {
			HttpURLConnection http = (HttpURLConnection) new URL("http://" + server + "/tetrevil_tomcat/randomizer").openConnection();

			http.setChunkedStreamingMode(1024);
			http.setRequestMethod("POST");
			http.setDoOutput(true);
			
			ObjectOutputStream out = new ObjectOutputStream(http.getOutputStream());
			out.writeInt(getDepth());
			out.writeObject(field.getField());
			out.close();
			
			http.connect();
			
			http.getHeaderFields();
			
			return (Shape) new ObjectInputStream(http.getInputStream()).readObject();
		} catch(IOException ioe) {
			throw new RuntimeException(ioe);
		} catch (ClassNotFoundException cnfe) {
			throw new RuntimeException(cnfe);
		}
	}
}
