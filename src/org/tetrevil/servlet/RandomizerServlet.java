package org.tetrevil.servlet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.tetrevil.Block;
import org.tetrevil.Field;
import org.tetrevil.MaliciousRandomizer;
import org.tetrevil.Shape;
import org.tetrevil.ThreadedMaliciousRandomizer;

/**
 * Servlet implementation class RandomizerServlet
 */
public class RandomizerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RandomizerServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
		ObjectInputStream in = new ObjectInputStream(request.getInputStream());
		ObjectOutputStream out = new ObjectOutputStream(response.getOutputStream());
		
		int depth = in.readInt();
		
		Block[][] blocks;
		try {
			blocks = (Block[][]) in.readObject();
		} catch(ClassNotFoundException cnfe) {
			throw new IOException(cnfe);
		}
		
		Field field = new Field(true);
		field.setField(blocks);
		
		MaliciousRandomizer randomizer = new ThreadedMaliciousRandomizer();
		randomizer.setAdaptive(field, false);
		randomizer.setDepth(depth);
		randomizer.setFair(false);
		randomizer.setRfactor(0);
		field.setProvider(randomizer);
		
		System.out.println("Providing shape for " + field + " depth " + depth);
		
		randomizer.provideShape(field); // Skip the random starting shape
		Shape shape = randomizer.provideShape(field);
		
		System.out.println("Chose to provide " + shape);
		
		out.writeObject(shape);
		out.close();
		} catch(IOException ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

}
