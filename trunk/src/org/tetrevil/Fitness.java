package org.tetrevil;

public class Fitness {
	/**
	 * Fitness function used to score a board.  Higher score is worse for the player.
	 * @param field
	 * @return
	 */
	public static double score(Field field) {
		if(field.isGameOver())
			return Double.POSITIVE_INFINITY;
		Block[][] f = field.getField();
		double score = 0;
		for(int x = Field.BUFFER; x < Field.WIDTH + Field.BUFFER; x++) {
			for(int y = Field.HEIGHT  + Field.BUFFER - 1; y >= Field.BUFFER; y--) {
				int h = Field.HEIGHT + Field.BUFFER - y;
				if(f[y][x] != null && f[y][x] != Block.X)
					score += 15 * h;
				else if(f[y][x] == Block.X)
					score += Math.pow(h, 1.5);
				else if(f[y][x] == null) {
					if(f[y][x-1] != null && f[y][x+1] != null)
						score += h;
				}
			}
		}
		return score;
	}

}
