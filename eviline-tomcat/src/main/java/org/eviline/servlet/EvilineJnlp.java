package org.eviline.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

/**
 * Servlet implementation class ZeroJnlp
 */
public class EvilineJnlp extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public EvilineJnlp() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String jnlp = IOUtils.toString(EvilineJnlp.class.getResource("eviline.jnlp"));
		String url = request.getRequestURL().toString();
		String codebase = url.replaceAll("/[^/]*$", "");
		jnlp = jnlp.replaceAll("\\$\\{codebase\\}", codebase);
		
		IOUtils.write(jnlp, response.getWriter());
	}

}
