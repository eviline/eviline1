package org.eviline;

import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public enum ShapeTypeIcon {
	I,
	L,
	O,
	Z,
	T,
	J,
	S,
	;
	
	private ImageIcon icon;
	
	private ShapeTypeIcon() {
		icon = new ImageIcon(ShapeType.class.getResource("images/" + name() + ".png"));
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
	}
	
	public static ShapeTypeIcon forType(ShapeType type) {
		switch(type) {
		case I: return I;
		case L: return L;
		case O: return O;
		case Z: return Z;
		case T: return T;
		case J: return J;
		case S: return S;
		}
		throw new InternalError("Fell through to default when all enums covered");
	}

	public Icon icon() {
		return icon;
	}

}
