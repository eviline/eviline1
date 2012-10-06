package org.tetrevil;

import static org.tetrevil.Shape.I_DOWN;
import static org.tetrevil.Shape.I_LEFT;
import static org.tetrevil.Shape.I_UP;
import static org.tetrevil.Shape.J_DOWN;
import static org.tetrevil.Shape.J_LEFT;
import static org.tetrevil.Shape.J_RIGHT;
import static org.tetrevil.Shape.J_UP;
import static org.tetrevil.Shape.L_DOWN;
import static org.tetrevil.Shape.L_LEFT;
import static org.tetrevil.Shape.L_RIGHT;
import static org.tetrevil.Shape.L_UP;
import static org.tetrevil.Shape.O_UP;
import static org.tetrevil.Shape.S_DOWN;
import static org.tetrevil.Shape.S_LEFT;
import static org.tetrevil.Shape.S_UP;
import static org.tetrevil.Shape.T_DOWN;
import static org.tetrevil.Shape.T_LEFT;
import static org.tetrevil.Shape.T_RIGHT;
import static org.tetrevil.Shape.T_UP;
import static org.tetrevil.Shape.Z_DOWN;
import static org.tetrevil.Shape.Z_LEFT;
import static org.tetrevil.Shape.Z_UP;

import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Possible types of {@link Shape}s.
 * @author robin
 *
 */
public enum ShapeType {
	I,
	L,
	O,
	Z,
	T,
	J,
	S,
	;
	
	private ImageIcon icon;
	private Block inactive;
	
	private ShapeType() {
		icon = new ImageIcon(ShapeType.class.getResource("images/shapetype/" + name() + ".png"));
		BufferedImage buf = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
		buf.getGraphics().drawImage(icon.getImage(), 0, 0, null);
		Block b = Block.valueOf(name());
		for(int x = 0; x < buf.getWidth(); x++) {
			for(int y = 0; y < buf.getHeight(); y++) {
				int rgb = buf.getRGB(x, y);
				if(rgb != 0) {
					if((rgb & 0xFFFFFF) == 0)
						buf.setRGB(x, y, b.color().darker().getRGB());
					else
						buf.setRGB(x, y, b.color().getRGB());
				}
			}
		}
		icon = new ImageIcon(buf);
		inactive = b;
	}
	
	
	/**
	 * Returns the {@link Shape}s for this {@link ShapeType} that are distinct.
	 * @return
	 */
	public Shape[] orientations() {
		switch(this) {
		case O: return new Shape[] { O_UP };
		case S: return new Shape[] { S_DOWN, S_LEFT};
		case Z: return new Shape[] { Z_DOWN, Z_LEFT};
		case J: return new Shape[] { J_DOWN, J_LEFT, J_RIGHT, J_UP };
		case L: return new Shape[] { L_DOWN, L_LEFT, L_RIGHT, L_UP };
		case I: return new Shape[] { I_DOWN, I_LEFT};
		case T: return new Shape[] { T_DOWN, T_LEFT, T_RIGHT, T_UP };
		}
		throw new InternalError("Fell through to default when all enums covered");
	}
	
	/**
	 * Returns the shape used to start a new round with this shape type
	 * @return
	 */
	public Shape starter() {
		switch(this) {
		case O: return O_UP;
		case S: return S_UP;
		case Z: return Z_UP;
		case T: return T_UP;
		case I: return I_UP;
		case J: return J_UP;
		case L: return L_UP;
		}
		throw new InternalError("Fell through to default when all enums covered");
	}
	
	/**
	 * Returns the Y offset of a shape of this type when starting
	 * @return
	 */
	public int starterY() {
		switch(this) {
		case O: return 1;
		case S: return 1;
		case Z: return 1;
		case I: return 1;
		case T: return 1;
		case J: return 1;
		case L: return 1;
		}
		throw new InternalError("Fell through to default when all enums covered");
	}

	/**
	 * Returns the X offset of a shape of this type when starting
	 * @return
	 */
	public int starterX() {
		switch(this) {
		case O: return 1;
		case S: return 0;
		case Z: return 0;
		case I: return 0;
		case T: return 0;
		case J: return 0;
		case L: return 0;
		}
		throw new InternalError("Fell through to default when all enums covered");
	}
	
	/**
	 * Returns the inactive block associated with this shape type
	 * @return
	 */
	public Block inactive() {
		return inactive;
	}
	
	public Shape up() {
		switch(this) {
		case O: return O_UP;
		case S: return S_UP;
		case Z: return Z_UP;
		case I: return I_UP;
		case T: return T_UP;
		case J: return J_UP;
		case L: return L_UP;
		}
		throw new InternalError("Fell through to default when all enums covered");
	}
	
	public Shape right() {
		return up().rotateRight();
	}
	
	public Shape left() {
		return up().rotateLeft();
	}
	
	public Shape down() {
		return up().rotateRight().rotateRight();
	}
	
	public Icon icon() {
		return icon;
	}
}
