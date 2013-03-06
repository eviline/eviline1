package org.eviline;

import java.awt.Color;

/**
 * A block on the playing {@link Field}.  There are two block types for each {@link ShapeType}: active and inactive.
 * Additionally, there is a block type for the outer border around the field.  Empty areas in the {@link Field}
 * are stored as nulls.<p>
 * 
 * A block is active if it is a part of the currently active {@link Shape} in the field.  Once a {@link Shape} locks
 * on the field its active blocks become inactive blocks and are stored.
 * @author robin
 *
 */
public enum Block {
	/*
	 * Inactive block types
	 */
	I,
	T,
	S,
	Z,
	O,
	J,
	L,
	/*
	 * Active block types
	 */
	IA,
	TA,
	SA,
	ZA,
	OA,
	JA,
	LA,
	
	/**
	 * Field border
	 */
	X,
	
	/**
	 * Ghost
	 */
	G,
	
	
	/**
	 * iMpossible block
	 */
	M,
	/**
	 * Unlikely block
	 */
	U,
	
	;
	
	/**
	 * Returns whether this is an active or inactive block
	 * @return
	 */
	public boolean isActive() {
		switch(this) {
		case I: case T: case S: case Z: case O: case J: case L:
			return false;
		case IA: case TA: case SA: case ZA: case OA: case JA: case LA:
			return true;
		case X: return false; // Field border is considered inactive
		case G: return true;
		case M: return false;
		case U: return false;
		}
		throw new InternalError("Impossible switch fall-through");
	}

	/**
	 * Returns the active version of the current block
	 * @return The active version of the current block
	 */
	public Block active() {
		switch(this) {
		case I: case IA: return IA;
		case T: case TA: return TA;
		case S: case SA: return SA;
		case Z: case ZA: return ZA;
		case O: case OA: return OA;
		case J: case JA: return JA;
		case L: case LA: return LA;
		case X: return X;
		case G: return G;
		case M: return M;
		case U: return U;
		}
		throw new InternalError("Impossible switch fall-through");
	}

	/**
	 * Returns the inactive version of the current block
	 * @return The inactive version of the current block
	 */
	public Block inactive() {
		switch(this) {
		case I: case IA: return I;
		case T: case TA: return T;
		case S: case SA: return S;
		case Z: case ZA: return Z;
		case O: case OA: return O;
		case J: case JA: return J;
		case L: case LA: return L;
		case X: return X;
		case G: return G;
		case M: return M;
		case U: return U;
		}
		throw new InternalError("Impossible switch fall-through");
	}

	public Color color() {
		switch(this) {
		case I:
			return IA.color();//.darker();
		case IA:
			return new Color(0, 159, 218);
		case J: 
			return JA.color();//.darker();
		case JA:
			return new Color(0, 101, 189);
		case L: 
			return LA.color();//.darker();
		case LA:
			return new Color(255, 121, 0);
		case O: 
			return OA.color();//.darker();
		case OA:
			return new Color(254, 203, 0);
		case S: 
			return SA.color();//.darker();
		case SA:
			return new Color(105, 190, 40);
		case T: 
			return TA.color();//.darker();
		case TA:
			return new Color(149, 45, 152);
		case Z: 
			return ZA.color();//.darker();
		case ZA:
			return new Color(237, 41, 57);
		case X: return Color.DARK_GRAY;
		case G: return Color.WHITE;
		case M: return X.color();
		case U: return X.color();
		}
		throw new InternalError("Impossible switch fall-through");
	}
}
