package org.tetrevil;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RemoteRandomizer extends ThreadedMaliciousRandomizer {
	
	protected String server = "www.tetrevil.org:8080";
	
	public RemoteRandomizer() {
		this(DEFAULT_DEPTH, DEFAULT_DIST);
	}
	
	public RemoteRandomizer(int depth, int dist) {
		super(depth, dist);
	}
	
	public Shape provideLocalShape(Field field) {
		return super.provideShape(field);
	}
	
	@Override
	public Shape provideShape(Field field) {
		if(randomFirst) {
			randomFirst = false;
			ShapeType type;
			do {
				type = ShapeType.values()[(int)(Math.random() * ShapeType.values().length)];
			} while(type == ShapeType.S || type == ShapeType.Z);
			return type.starter();
		}
		try {
			HttpURLConnection http = (HttpURLConnection) new URL("http://" + server + "/tetrevil_tomcat/randomizer").openConnection();

			http.setChunkedStreamingMode(1024);
			http.setRequestMethod("POST");
			http.setDoOutput(true);
			
			ObjectOutputStream out = new ObjectOutputStream(http.getOutputStream());
			out.writeObject(this);
			out.writeObject(field);
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
