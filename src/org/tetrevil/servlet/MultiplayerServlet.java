package org.tetrevil.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.tetrevil.MaliciousRandomizer;

/**
 * Servlet implementation class MultiplayerServlet
 */
public class MultiplayerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected static class MultiplayerHost {
		protected String name;
		protected MaliciousRandomizer randomizer;

		protected ServerSocket hostServer;
		protected ServerSocket clientServer;
		
		protected Object socketLock = new Object();
		
		protected Socket hostSocket;
		protected Socket clientSocket;
		
		protected class HostThread extends Thread {
			@Override
			public void run() {
				try {
					hostSocket = hostServer.accept();
					hostSocket.setTcpNoDelay(true);
					synchronized(socketLock) {
						socketLock.notifyAll();
						while(clientSocket == null)
							socketLock.wait();
					}
					
					hosts.remove(MultiplayerHost.this);

					InputStream hostIn = hostSocket.getInputStream();
					OutputStream clientOut = clientSocket.getOutputStream();
					
					
					byte[] buf = new byte[8192];
					while(true) {
						int r = hostIn.available();
						if(r == 0)
							r = 1;
						
						r = hostIn.read(buf, 0, Math.min(r, buf.length));
						clientOut.write(buf, 0, r);
						clientOut.flush();
					}
					
				} catch(Exception ex) {
					ex.printStackTrace();
					disconnect();
				}
			}
		}
		
		protected class ClientThread extends Thread {
			@Override
			public void run() {
				try {

					clientSocket = clientServer.accept();
					clientSocket.setTcpNoDelay(true);
					synchronized(socketLock) {
						socketLock.notifyAll();
						while(hostSocket == null)
							socketLock.wait();
					}

					hosts.remove(MultiplayerHost.this);

					InputStream clientIn = clientSocket.getInputStream();
					OutputStream hostOut = hostSocket.getOutputStream();
					
					
					byte[] buf = new byte[8192];
					while(true) {
						int r = clientIn.available();
						if(r == 0)
							r = 1;
						
						r = clientIn.read(buf, 0, Math.min(r, buf.length));
						hostOut.write(buf, 0, r);
						hostOut.flush();
					}
				} catch(Exception ex) {
					ex.printStackTrace();
					disconnect();
				}
			}
		}
		
		protected class AutoDisconnectThread extends Thread {
			@Override
			public void run() {
				try {
					Thread.sleep(30 * 60 * 1000);
				} catch(Exception ex) {
				} finally {
					if(hostSocket == null || clientSocket == null)
						disconnect();
				}
			}
		}
		
		public MultiplayerHost(String name, MaliciousRandomizer randomizer) 
		throws IOException {
			this.name = name;
			this.randomizer = randomizer;
			
			hostServer = new ServerSocket(0, 1);
			clientServer = new ServerSocket(0, 1);
			
			hosts.add(this);
			
			new HostThread().start();
			new ClientThread().start();
			new AutoDisconnectThread().start();
			
			System.out.println("Now hosting " + this);
		}
		
		public void disconnect() {
			try {
				hostServer.close();
				clientServer.close();
				if(hostSocket != null)
					hostSocket.close();
				if(clientSocket != null)
					clientSocket.close();
			} catch(Exception ex) {
				ex.printStackTrace();
			} finally {
				hosts.remove(this);
			}
		}
		
		@Override
		public String toString() {
			return "MultiplayerHost name=" + name 
					+ " hostPort=" + hostServer.getLocalPort()
					+ " clientPort=" + clientServer.getLocalPort()
					+ " hostSocket=" + hostSocket
					+ " clientSocket=" + clientSocket;
		}
	}
	
	protected static volatile Set<MultiplayerHost> hosts = Collections.synchronizedSet(new HashSet<MultiplayerHost>());
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MultiplayerServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ObjectInputStream in = new ObjectInputStream(request.getInputStream());
		ObjectOutputStream out = new ObjectOutputStream(response.getOutputStream());
		
		String command;
		String name;
		MaliciousRandomizer randomizer;
		
		try {
			command = (String) in.readObject();
			name = (String) in.readObject();
			randomizer = (MaliciousRandomizer) in.readObject();
		} catch(ClassNotFoundException c) {
			throw new IOException(c);
		}
		
		System.out.println("MultiplayerServlet commanded:" + command);
		
		if("host".equals(command)) {
			MultiplayerHost host = new MultiplayerHost(name, randomizer);
			System.out.println("Now hosting:" + host);
			out.writeInt(host.hostServer.getLocalPort());
		} else if("list".equals(command)) {
			synchronized(hosts) {
				for(MultiplayerHost host : hosts) {
					if(host.hostSocket == null || host.hostSocket.isClosed())
						continue;
					System.out.println("Available host:" + host);
					out.writeObject(host.name);
					out.writeObject(host.randomizer);
					out.writeInt(host.clientServer.getLocalPort());
				}
				out.writeObject(null);
			}
		}
		
		out.close();
	}

}
