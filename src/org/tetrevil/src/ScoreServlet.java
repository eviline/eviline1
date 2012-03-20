package org.tetrevil.src;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.tetrevil.srv.db.DbInterface;
import org.tetrevil.srv.db.ScoreMapper;
import org.tetrevil.wobj.WebScore;

/**
 * Servlet implementation class ScoreServlet
 */
public class ScoreServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ScoreServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Object robj = null;
		try {
			ObjectInputStream in = new ObjectInputStream(request.getInputStream());
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
		
		ObjectOutputStream out = new ObjectOutputStream(response.getOutputStream());
		out.writeObject(robj);
		out.flush();
		out.close();

	}

}
