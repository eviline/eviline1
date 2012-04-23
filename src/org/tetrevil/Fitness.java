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
		int[] stackHeight = new int[Field.WIDTH];
		for(int x = Field.BUFFER; x < Field.WIDTH + Field.BUFFER; x++) {
			int holes = 0;
			for(int y = Field.HEIGHT  + Field.BUFFER - 1; y >= Field.BUFFER; y--) {
				int h = Field.HEIGHT + Field.BUFFER - y;
				if(f[y][x] != null)
					stackHeight[x-Field.BUFFER] = h;
				if(f[y][x] != null && f[y][x] != Block.X)
					score += 25 * h * (holes + 1);
				else if(f[y][x] == Block.X) {
					score += Math.pow(h, 1.5);
					holes++;
				}
				else if(f[y][x] == null) {
					if(f[y][x-1] != null && f[y][x+1] != null && f[y+1][x] == null)
						score += h;
				}
			}
			int w = x - Field.BUFFER;
			if(w > 0 && stackHeight[w] == stackHeight[w-1])
				score -= stackHeight[w];
		}
//		// Add in surface smoothness weight
//		int sr = Math.max(stackHeight[1] - stackHeight[0], 0);
//		for(int i = 1; i < stackHeight.length - 2; i++)
//			sr += Math.abs(stackHeight[i] - stackHeight[i+1]);
//		sr += Math.max(stackHeight[stackHeight.length - 2] - stackHeight[stackHeight.length - 1], 0);
//		
//		score += sr * 10;
		
		return score;
	}

}
