package org.tetrevil.servlet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class CookieServlet
 */
public class CookieServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CookieServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DataInputStream in = new DataInputStream(request.getInputStream());
		DataOutputStream out = new DataOutputStream(response.getOutputStream());
		
		response.setContentType("text/plain");
		
		String command = in.readUTF();
		
		if("set".equals(command)) {
			String name = in.readUTF();
			String value = in.readUTF();
			System.out.println("Setting cookie " + name + "=" + value);
			Cookie c = new Cookie(name, value);
			c.setMaxAge((int) TimeUnit.SECONDS.convert(365, TimeUnit.DAYS));
			c.setDomain(".tetrevil.org");
			c.setPath("/");
			response.addCookie(c);
			out.writeUTF(value);
		} else if("get".equals(command)) {
			String name = in.readUTF();
			String value = "";
			if(request.getCookies() != null) {
				for(Cookie c : request.getCookies()) {
					System.out.println("cookie named " + c.getName());
					if(name.equals(c.getName()))
						value = c.getValue();
				}
			} else {
				System.out.println("Null cookies!");
			}
			System.out.println("Getting cookie " + name + "=" + value);
			out.writeUTF(value);
		} else {
			System.out.println("Unknown request " + command);
		}
		
		out.flush();
		out.close();
	}

}
