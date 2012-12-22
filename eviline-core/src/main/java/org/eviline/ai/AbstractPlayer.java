package org.eviline.ai;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Queue;

import org.eviline.Field;
import org.eviline.PlayerAction;

public abstract class AbstractPlayer implements Player {
	
	protected class AbstractPlayerIterator implements Iterator<PlayerAction> {

		@Override
		public boolean hasNext() {
			return true;
		}

		@Override
		public PlayerAction next() {
			if(moves.size() == 0) {
				for(PlayerAction a : compute(field)) {
					moves.offerLast(a);
				}
			}
			return moves.pollFirst();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
	}

	private Field field;
	private Deque<PlayerAction> moves = new ArrayDeque<PlayerAction>();
	
	public AbstractPlayer(Field field) {
		this.field = field;
	}
	
	protected abstract Queue<PlayerAction> compute(Field field);
	
	@Override
	public void reset() {
		moves.clear();
	}

	@Override
	public Iterator<PlayerAction> iterator() {
		return new AbstractPlayerIterator();
	}
}
