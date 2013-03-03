package org.eviline.ai;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.List;

import org.eviline.Field;
import org.eviline.PlayerAction;
import org.eviline.Shape;
import org.eviline.ShapeType;

public class PlayerMovePath {
	protected List<long[]>[] intPaths;
	protected Deque<PlayerAction> moves;
	protected ShapeType pathType;

	public PlayerMovePath(Deque<PlayerAction> moves) {
		this.moves = moves;
		repath();
	}
	
	public void repath() {
		intPaths = new List[4];
		for(int i = 0; i < 4; i++) {
			intPaths[i] = new ArrayList<long[]>();
			if(moves.size() > 2) {
			} else
				continue;
			for(PlayerAction m : moves) {
				Shape s = m.getEndShape();
				pathType = s.type();
				intPaths[i].add(new long[] {
						m.getEndX() - Field.BUFFER + s.x(i), 
						m.getEndY() - Field.BUFFER + s.y(i),
						m.getTimestamp()});
			}
		}
	}
	
	public List<long[]>[] getIntPaths() {
		return intPaths;
	}
	
	public Deque<PlayerAction> getMoves() {
		return moves;
	}

	public ShapeType getPathType() {
		return pathType;
	}
}
