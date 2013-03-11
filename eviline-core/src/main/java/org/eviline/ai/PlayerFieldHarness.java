package org.eviline.ai;

import java.util.Iterator;

import org.eviline.Field;
import org.eviline.PlayerAction;
import org.eviline.PlayerActionNode;
import org.eviline.ShapeDirection;

public class PlayerFieldHarness {
	protected Field field;
	protected Player player;

	protected Iterator<PlayerAction> moves;
	
	public PlayerFieldHarness(Field field, Player player) {
		this.field = field;
		this.player = player;
		moves = player.iterator();
	}
	
	public boolean tick() {
		PlayerAction move = moves.next();
		if(move == null) {
			field.clockTick();
			return false;
		}
		switch(move.getType()) {
		case DAS_LEFT:
			field.setAutoShift(ShapeDirection.LEFT);
			field.autoshift();
			break;
		case DAS_RIGHT:
			field.setAutoShift(ShapeDirection.RIGHT);
			field.autoshift();
			break;
		case DOWN_ONE:
			field.clockTick();
			break;
		case HARD_DROP:
			while(!field.isGrounded())
				field.clockTick();
			field.clockTick();
			break;
		case ROTATE_LEFT:
			field.rotateLeft();
			break;
		case ROTATE_RIGHT:
			field.rotateRight();
			break;
		case SHIFT_LEFT:
			field.shiftLeft();
			break;
		case SHIFT_RIGHT:
			field.shiftRight();
			break;
		case HOLD:
			//FIXME: Support hold
			throw new UnsupportedOperationException("Eviline doesn't currently support hold");
		}
		
		boolean mismove = false;
		if(move.getEndShape() == null ? field.getShape() != null : field.getShape() == null)
			mismove = true;
		else {
			PlayerActionNode mpan = new PlayerActionNode(move.getEndShape(), move.getEndX(), move.getEndY());
			PlayerActionNode apan = new PlayerActionNode(field.getShape(), field.getShapeX(), field.getShapeY());
			if(!mpan.equals(apan)) {
//				System.err.println("mismove:");
//				System.err.println("\texpected:" + mpan);
//				System.err.println("\tresulted:" + apan);
				mismove = true;
			}
		}
		if(mismove)
			player.reset();
		return mismove;
	}
}
