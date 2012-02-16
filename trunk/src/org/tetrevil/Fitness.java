package org.tetrevil;

public class Fitness {

	public static double score(Field field) {
		if(field.isGameOver())
			return Double.POSITIVE_INFINITY;
		Block[][] f = field.getField();
		double score = 0;
		for(int x = Field.BUFFER; x < Field.WIDTH + Field.BUFFER; x++) {
			for(int y = Field.HEIGHT  + Field.BUFFER - 1; y >= Field.BUFFER; y--) {
				if(f[y][x] != null && f[y][x] != Block.X)
					score += 15 * Field.HEIGHT + Field.BUFFER - y;
				else if(f[y][x] == Block.X)
					score += Math.pow(Field.HEIGHT + Field.BUFFER - y, 1.5);
			}
		}
		return score;
	}

}
