package org.eviline.event;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import org.eviline.PlayerAction;
import org.eviline.ShapeType;
import org.eviline.ai.PlayerMovePath;

public class HistoryMovePath extends PlayerMovePath implements EvilineListener {

	protected List<long[]>[] lastPaths;
	protected ShapeType lastType;
	
	public HistoryMovePath() {
		super(new ArrayDeque<PlayerAction>());
		lastPaths = new List[4];
		for(int i = 0; i < 4; i++)
			lastPaths[i] = new ArrayList<long[]>();
	}

	@Override
	public void shapeSpawned(EvilineEvent e) {
		for(int i = 0; i < 4; i++) {
			lastPaths[i].clear();
			lastPaths[i].addAll(intPaths[i]);
		}
		lastType = pathType;
		pathType = null;
		moves.clear();
		repath();
	}

	@Override
	public void clockTicked(EvilineEvent e) {
		if(e.getShape() != null) {
			moves.offerLast(new PlayerAction(e));
			repath();
		}
	}

	@Override
	public void shapeLocked(EvilineEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void gameOver(EvilineEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void shiftedLeft(EvilineEvent e) {
		moves.offerLast(new PlayerAction(e));
		repath();
	}

	@Override
	public void shiftedRight(EvilineEvent e) {
		moves.offerLast(new PlayerAction(e));
		repath();
	}

	@Override
	public void rotatedLeft(EvilineEvent e) {
		moves.offerLast(new PlayerAction(e));
		repath();
	}

	@Override
	public void rotatedRight(EvilineEvent e) {
		moves.offerLast(new PlayerAction(e));
		repath();
	}

	@Override
	public void gameReset(EvilineEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void gamePaused(EvilineEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void linesCleared(EvilineEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void garbageReceived(EvilineEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void hardDropped(EvilineEvent e) {
		// TODO Auto-generated method stub
		
	}

	public List<long[]>[] getLastPaths() {
		return lastPaths;
	}

	public ShapeType getLastType() {
		return lastType;
	}

}
