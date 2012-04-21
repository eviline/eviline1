package org.tetrevil.mp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.tetrevil.ConcurrentShapeProvider;
import org.tetrevil.swing.TetrevilFrame;

public class MultiplayerConnection {
	public static void init(TetrevilFrame frame, Socket socket, boolean host) throws IOException {
		ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
		
		frame.getCenter().removeAll();
		frame.getDp().setEnabled(false);
		frame.getTc().getTicker().setInitialDelay(0);
		frame.getStart().doClick();
		
		frame.getField().setUnpausable(true);
		
		frame.getField().setProvider(new ConcurrentShapeProvider(frame.getField().getProvider()));
		out.writeObject(frame.getField().getProvider());
		
		
		frame.getField().addTetrevilListener(new TetrevilTableSender(out));
		ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
		frame.getCenter().add(new RemoteTetrevilTable(in, frame.getField()));
		frame.getCenter().revalidate();
		frame.repaint();

	}
}
