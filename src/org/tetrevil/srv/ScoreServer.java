package org.tetrevil.srv;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.ibatis.session.SqlSession;
import org.tetrevil.srv.db.DbInterface;
import org.tetrevil.srv.db.ScoreMapper;
import org.tetrevil.wobj.WebScore;

public class ScoreServer {

	public static void main(String[] args) throws Exception {
		new ScoreServer().start(args);
	}
	
	protected ExecutorService exec = Executors.newCachedThreadPool();
	
	public void start(String[] args) throws IOException {
		ServerSocket server = new ServerSocket(WebScore.PORT);
		System.out.println("Listening on " + server);
		while(true) {
			final Socket socket = server.accept();
			System.out.println("Accepted " + socket);
			exec.submit(new Callable<Object>() {
				@Override
				public Object call() throws Exception {
					serve(socket);
					System.out.println("Returned");
					return null;
				}
			});
		}
	}
	
	public void serve(Socket socket) throws IOException {
		Object robj = null;
		try {
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			Map<String, Object> mreq;
			try {
				mreq = (Map<String, Object>) in.readObject();
			} catch(ClassNotFoundException cnfe) {
				throw new IOException(cnfe);
			}
			
			System.out.println("Received request " + mreq);

			if(WebScore.SUBMIT_SCORE.equals(mreq.get(WebScore.COMMAND))) {
				WebScore score = (WebScore) mreq.get(WebScore.SCORE);
				SqlSession session = DbInterface.getFactory().openSession();
				try {
					session.getMapper(ScoreMapper.class).insert(score);
					session.commit();
				} finally {
					session.close();
				}
				robj = score;
			} else if(WebScore.HIGH_SCORE.equals(mreq.get(WebScore.COMMAND))) {
				WebScore params = (WebScore) mreq.get(WebScore.SCORE);
				SqlSession session = DbInterface.getFactory().openSession();
				try {
					robj = session.getMapper(ScoreMapper.class).highScore(params);
				} finally {
					session.close();
				}
			}
		} catch(Exception ex) {
			ex.printStackTrace();
			robj = ex;
		}
		
		System.out.println("Sending back " + robj);
		
		ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
		out.writeObject(robj);
		out.flush();
		out.close();
		
		socket.close();
	}
}
