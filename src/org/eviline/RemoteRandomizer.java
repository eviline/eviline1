package org.eviline;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class RemoteRandomizer extends ThreadedMaliciousRandomizer {
	private static final long serialVersionUID = -2826120548011502136L;
	
	protected String server = "www.tetrevil.org:8080";
	protected transient Future<Shape> future = null;
	
	protected transient boolean intermediateNulls = true;
	
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
	public Shape provideShape(final Field field) {
		if(randomFirst) {
			randomFirst = false;
			ShapeType type;
			do {
				type = ShapeType.values()[(int)(Math.random() * ShapeType.values().length)];
			} while(type == ShapeType.S || type == ShapeType.Z);
			return type.starter();
		}
	
		if(future == null || future.isCancelled()) {
			future = ThreadedMaliciousRandomizer.EXECUTOR.submit(new Callable<Shape>() {
				@Override
				public Shape call() throws Exception {
					Shape shape = remote(field);
					recent.add(shape.type());
					while(recent.size() > HISTORY_SIZE)
						recent.remove(0);
					typeCounts[shape.type().ordinal()]++;
					typeCounts[(int)(typeCounts.length * Math.random())]--;
					return shape;
				}
			});
		}
		
		if(intermediateNulls && !future.isDone())
			return null;
		
		try {
			return future.get();
		} catch(Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {
			future = null;
		}
		
		
//		Shape shape = remote(field);
//		recent.add(shape.type());
//		while(recent.size() > HISTORY_SIZE)
//			recent.remove(0);
//		typeCounts[shape.type().ordinal()]++;
//		typeCounts[(int)(typeCounts.length * Math.random())]--;
//		return shape;

	}
	
	public boolean isIntermediateNulls() {
		return intermediateNulls;
	}
	
	public void setIntermediateNulls(boolean intermediateNulls) {
		this.intermediateNulls = intermediateNulls;
	}
	
	protected Shape remote(Field field) {
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
