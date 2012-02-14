package org.tetrevil;

public class Fitness {

	public static double score(Field field) {
		if(field.isGameOver())
			return Double.POSITIVE_INFINITY;
		Block[][] f = field.getField();
		double score = 0;
		for(int x = Field.BUFFER; x < Field.WIDTH + Field.BUFFER; x++) {
			int bonus = 0;
			for(int y = Field.HEIGHT  + Field.BUFFER - 1; y >= Field.BUFFER; y--) {
				if(f[y][x] != null && f[y][x] != Block.X)
					score += Field.HEIGHT + Field.BUFFER - y + bonus * 10;
				else
					bonus++;
			}
		}
		return score;
	}

}
