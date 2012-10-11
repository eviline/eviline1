package org.eviline.mp;

import java.awt.Dimension;
import java.io.ObjectInputStream;
import javax.swing.BorderFactory;
import javax.swing.SwingUtilities;

import org.eviline.Field;
import org.eviline.randomizer.ConcurrentDelegatingRandomizer;
import org.eviline.randomizer.Randomizer;
import org.eviline.swing.TetrevilTable;

public class TetrevilTableReceiver extends TetrevilTable implements Runnable {
	protected ObjectInputStream in;
	protected Field local;
	
	public TetrevilTableReceiver(ObjectInputStream in, Field local) {
		super(new Field());
		this.in = in;
		this.local = local;
		
		setBorder(BorderFactory.createTitledBorder("Remote Player"));
		
		setPreferredSize(new Dimension(300, 600));
		
		new Thread(this).start();
	}
	
	@Override
	public void run() {
		try {
			while(true) {
				Object obj = in.readObject();
				if(obj instanceof Field) {
					final Field f = (Field) obj;
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							f.copyInto(getField());
							repaint();
							if(f.isGameOver()) {
								getField().setGameOver(true);
								getField().setWinner(false);
								local.setGameOver(true);
								local.setUnpausable(false);
								local.setPaused(true);
								local.setUnpausable(true);
								local.setWinner(true);
							}
						}
					});
				}
				if(obj instanceof Integer) {
					int lines = (Integer) obj;
					if(lines > 1) {
						local.garbage(lines - 1);
					}
				}
				if(obj instanceof Randomizer) {
					local.setProvider((Randomizer) obj);
					local.setProvider(new ConcurrentDelegatingRandomizer(local.getProvider()));
				}
			}
		} catch(Exception ex) {
			ex.printStackTrace();
			getField().setGameOver(true);
			getField().setWinner(false);
			repaint();
			local.setGameOver(true);
			local.setUnpausable(false);
			local.setUnpausable(false);
			local.setPaused(true);
			local.setUnpausable(true);
			local.setWinner(true);
		}
	}
	
}