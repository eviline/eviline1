package org.eviline.fitness;

import org.eviline.Block;
import org.eviline.BlockType;
import org.eviline.Field;

public class FitnessUtils {
	
	public static Field paintedImpossible(Field field) {
		field = field.clone();
		Block[][] f = field.getField();
		
		for(int y = 1; y < Field.BUFFER + field.getHeight(); y++) {
			for(int x = Field.BUFFER; x < Field.BUFFER + field.getWidth(); x++) {
				if(f[y][x].getType() == BlockType.EMPTY)
					f[y][x] = Block.getImpossible();
			}
			for(int x = Field.BUFFER; x < Field.BUFFER + field.getWidth(); x++) {
				if(
						(f[y-1][x].isEmpty() 
							|| f[y][x-1].isEmpty() 
							|| f[y][x+1].isEmpty()) 
						&& f[y][x].isImpossible())
					f[y][x] = Block.getEmpty();
			}
			for(int x = Field.BUFFER + field.getWidth() - 1; x >= Field.BUFFER; x--) {
				if((f[y-1][x].isEmpty() || f[y][x-1].isEmpty() || f[y][x+1].isEmpty()) && f[y][x].isImpossible())
					f[y][x] = Block.getEmpty();
			}
		}
		
		return field;
	}
	
	private FitnessUtils() {}
}
