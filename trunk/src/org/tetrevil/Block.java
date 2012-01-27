package org.tetrevil;

public enum Block {
	I,
	T,
	S,
	Z,
	O,
	J,
	L,
	IA,
	TA,
	SA,
	ZA,
	OA,
	JA,
	LA,
	;
	
	public boolean isActive() {
		switch(this) {
		case I: case T: case S: case Z: case O: case J: case L:
			return false;
		case IA: case TA: case SA: case ZA: case OA: case JA: case LA:
			return true;
		}
		throw new InternalError("Impossible switch fall-through");
	}

	public Block active() {
		switch(this) {
		case I: case IA: return IA;
		case T: case TA: return TA;
		case S: case SA: return SA;
		case Z: case ZA: return ZA;
		case O: case OA: return OA;
		case J: case JA: return JA;
		case L: case LA: return LA;
		}
		throw new InternalError("Impossible switch fall-through");
	}

	public Block inactive() {
		switch(this) {
		case I: case IA: return I;
		case T: case TA: return T;
		case S: case SA: return S;
		case Z: case ZA: return Z;
		case O: case OA: return O;
		case J: case JA: return J;
		case L: case LA: return L;
		}
		throw new InternalError("Impossible switch fall-through");
	}

}
