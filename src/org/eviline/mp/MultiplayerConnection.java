package org.eviline.mp;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.SwingUtilities;

import org.eviline.event.TetrevilAdapter;
import org.eviline.event.TetrevilEvent;
import org.eviline.randomizer.ConcurrentDelegatingRandomizer;
import org.eviline.swing.TetrevilFrame;

public class MultiplayerConnection {
	public static void disableButtons(TetrevilFrame frame) {
		for(int i = 0; i < frame.getCenter().getComponentCount(); i++) {
			Component c = frame.getCenter().getComponent(i);
			if((c instanceof AbstractButton) && "Quit".equals(((AbstractButton) c).getText()))
				continue;
			c.setEnabled(false);
		}
	}
	
	public static void init(TetrevilFrame frame, Socket socket, boolean host) throws IOException {
		ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
		
		frame.getCenter().removeAll();
		frame.getCenter().add(new JButton(new AbstractAction("Quit") {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		}));
		
		frame.getDp().setEnabled(false);
		
		frame.getField().addTetrevilListener(new TetrevilAdapter() {
			@Override
			public void shapeLocked(final TetrevilEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						e.getField().clockTick();
					}
				});
			}
		});
		frame.getStart().doClick();
		
		frame.getField().setUnpausable(true);
		frame.getField().setMultiplayer(true);
		
		frame.getField().setProvider(new ConcurrentDelegatingRandomizer(frame.getField().getProvider()));
		if(host)
			out.writeObject(frame.getField().getProvider());
		
		
		frame.getField().addTetrevilListener(new TetrevilTableSender(out));
		ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
		frame.getCenter().add(new TetrevilTableReceiver(in, frame.getField()));
		frame.getCenter().revalidate();
		frame.repaint();

	}
}
