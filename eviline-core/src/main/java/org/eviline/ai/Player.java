package org.eviline.ai;

import org.eviline.Field;
import org.eviline.PlayerAction;

public interface Player extends Iterable<PlayerAction> {
	public void reset();
}
