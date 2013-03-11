package org.eviline;

import java.util.Arrays;
import java.util.HashMap;

import org.eviline.event.EvilineEvent;

public class PlayerAction {
	public static class NodeMap<V> extends HashMap<PlayerActionNode, V> {
//		private static int W = Field.WIDTH + Field.BUFFER * 2;
//		private static int H = Field.HEIGHT + Field.BUFFER * 2;
//		
//		private Map.Entry<PlayerActionNode, V>[][][] entries = new Map.Entry[Shape.values().length][H][W];
//		
//		@Override
//		public Set<Map.Entry<PlayerActionNode, V>> entrySet() {
//			Set<Map.Entry<PlayerActionNode, V>> es = new HashSet<Map.Entry<PlayerActionNode,V>>();
//			for(Map.Entry<PlayerActionNode, V>[][] ea : entries) {
//				for(Map.Entry<PlayerActionNode, V>[] eaa : ea) {
//					for(Map.Entry<PlayerActionNode, V> e : eaa) {
//						if(e != null)
//							es.add(e);
//					}
//				}
//			}
//			return es;
//		}
//		
//		@Override
//		public V get(Object key) {
//			PlayerActionNode k = (PlayerActionNode) key;
//			Map.Entry<PlayerActionNode, V> e = entries[k.shape.ordinal()][k.y][k.x];
//			return e == null ? null : e.getValue();
//		}
//		
//		public V put(PlayerActionNode key, V value) {
//			V old = get(key);
//			entries[key.shape.ordinal()][key.y][key.x] = new AbstractMap.SimpleEntry(key, value);
//			return old;
//		}
//		
//		@Override
//		public V remove(Object key) {
//			PlayerActionNode k = (PlayerActionNode) key;
//			V ret = get(key);
//			entries[k.shape.ordinal()][k.y][k.x] = null;	
//			return ret;
//		}
//		
//		@Override
//		public boolean containsKey(Object key) {
//			return get(key) != null;
//		}
	}
	
	private Field startField;
	private Field endField;
	private PlayerActionType type;
	private Shape startShape;
	private Shape endShape;
	private int startX;
	private int startY;
	private int endX;
	private int endY;
	
	private PlayerActionNode startNode;
	private PlayerActionNode endNode;
	
	private boolean possible;
	
	private long timestamp = System.currentTimeMillis();
	
	public PlayerAction(Field field, PlayerActionType type) {
		this(field, type, false);
	}
	
	public PlayerAction(Field field, PlayerActionType type, boolean reverse) {
		if(field.getShape() == null)
			throw new IllegalArgumentException("null field shape");
		if(!reverse)
			compute(field, type);
		else
			computeReverse(field, type);
		
		possible = !(startShape == endShape && startX == endX && startY == endY);
		startNode = new PlayerActionNode(startShape, startX, startY);
		endNode = new PlayerActionNode(endShape, endX, endY);
	}
	
	public PlayerAction(EvilineEvent e) {
		this.endField = e.getField();
		this.type = PlayerActionType.fromEvent(e);
		this.endShape = e.getShape();
		this.endX = e.getX();
		this.endY = e.getY();
	}
	
	@Override
	public int hashCode() {
		return Arrays.<Object>asList(type, startShape, endShape, startX, endX, startY, endY).hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null)
			return false;
		if(obj == this)
			return true;
		if(!(obj instanceof PlayerAction))
			return false;
		PlayerAction opa = (PlayerAction) obj;
		return Arrays.<Object>asList(type, startShape, endShape, startX, endX, startY, endY).equals(
				Arrays.<Object>asList(opa.type, opa.startShape, opa.endShape, opa.startX, opa.endX, opa.startY, opa.endY));
	}
	
	private void compute(Field field, PlayerActionType type) {
		this.type = type;
		startField = field.copy();
		startX = field.shapeX;
		startY = field.shapeY;
		startShape = field.shape;
		endField = startField.copy();
		
		switch(type) {
		case DOWN_ONE:
			if(!endField.shape.intersects(endField.field, endField.shapeX, endField.shapeY + 1))
				endField.shapeY += 1;
			break;
		case ROTATE_LEFT:
			endField.rotateLeft();
			break;
		case ROTATE_RIGHT:
			endField.rotateRight();
			break;
		case SHIFT_LEFT:
			endField.shiftLeft();
			break;
		case SHIFT_RIGHT:
			endField.shiftRight();
			break;
		case DAS_LEFT:
			for(int i = 0; i < 10; i++)
				endField.shiftLeft();
			break;
		case DAS_RIGHT:
			for(int i = 0; i < 10; i++)
				endField.shiftRight();
			break;
		}
		
		endShape = endField.shape;
		endX = endField.shapeX;
		endY = endField.shapeY;
	}
	
	private void computeReverse(Field field, PlayerActionType type) {
		this.type = type;
		endField = field.copy();
		endX = field.shapeX;
		endY = field.shapeY;
		endShape = field.shape;
		startField = endField.copy();
		
		switch(type) {
		case DOWN_ONE:
			if(startField.shapeY == 0)
				break;
			if(!startField.shape.intersects(startField.field, startField.shapeX, startField.shapeY - 1))
				startField.shapeY -= 1;
			break;
		case ROTATE_LEFT:
			startField.reverseRotateLeft();
			break;
		case ROTATE_RIGHT:
			startField.reverseRotateRight();
			break;
		case SHIFT_LEFT:
			startField.shiftRight();
			break;
		case SHIFT_RIGHT:
			startField.shiftLeft();
			break;
		case DAS_LEFT:
			for(int i = 0; i < 10; i++)
				endField.shiftRight();
			break;
		case DAS_RIGHT:
			for(int i = 0; i < 10; i++)
				endField.shiftLeft();
			break;
		}
		
		startShape = startField.shape;
		startX = startField.shapeX;
		startY = startField.shapeY;
	}
	
	public Field getStartField() {
		return startField;
	}
	public Field getEndField() {
		return endField;
	}
	public PlayerActionType getType() {
		return type;
	}
	public Shape getStartShape() {
		return startShape;
	}
	public Shape getEndShape() {
		return endShape;
	}
	public int getStartX() {
		return startX;
	}
	public int getStartY() {
		return startY;
	}
	public int getEndX() {
		return endX;
	}
	public int getEndY() {
		return endY;
	}

	public boolean isPossible() {
		return possible;
	}
	
	@Override
	public String toString() {
		return type.toString() + " (" + startNode + " -> " + endNode + ")";
	}

	public PlayerActionNode getStartNode() {
		return startNode;
	}

	public PlayerActionNode getEndNode() {
		return endNode;
	}

	public long getTimestamp() {
		return timestamp;
	}
}
