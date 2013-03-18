package org.eviline;

import org.eviline.event.EvilineEvent;

public enum PlayerActionType {
	DOWN_ONE,
	SHIFT_LEFT,
	SHIFT_RIGHT,
	ROTATE_LEFT,
	ROTATE_RIGHT,
	HOLD,
	HARD_DROP,
	DAS_LEFT,
	DAS_RIGHT,
	;
	
	public String toString() {
		switch(this) {
		case DOWN_ONE: return "D";
		case ROTATE_LEFT: return "RL";
		case ROTATE_RIGHT: return "RR";
		case SHIFT_LEFT: return "SL";
		case SHIFT_RIGHT: return "SR";
		}
		return null;
	}
	
	
	public static PlayerActionType[] shiftFirstValues() {
		return new PlayerActionType[] {
				ROTATE_LEFT, ROTATE_RIGHT, SHIFT_LEFT, SHIFT_RIGHT, /*DAS_LEFT, DAS_RIGHT,*/ DOWN_ONE
		};
	}
	
	public static PlayerActionType[] rotateOnlyValues() {
		return new PlayerActionType[] {
				ROTATE_LEFT, ROTATE_RIGHT
		};
	}

	public static PlayerActionType[] dropFirstValues() {
		return new PlayerActionType[] {
				DOWN_ONE, SHIFT_LEFT, SHIFT_RIGHT, /*DAS_LEFT, DAS_RIGHT,*/ ROTATE_LEFT, ROTATE_RIGHT
		};
	}
	
	public static PlayerActionType fromEvent(EvilineEvent e) {
		switch(e.getId()) {
		case EvilineEvent.CLOCK_TICKED:
			return DOWN_ONE;
		case EvilineEvent.SHIFTED_LEFT:
			return SHIFT_LEFT;
		case EvilineEvent.SHIFTED_RIGHT:
			return SHIFT_RIGHT;
		case EvilineEvent.ROTATED_LEFT:
			return ROTATE_LEFT;
		case EvilineEvent.ROTATED_RIGHT:
			return ROTATE_RIGHT;
		}
		return null;
	}
}