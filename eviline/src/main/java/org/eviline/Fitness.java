package org.eviline;

public class Fitness {
	
	public static double scoreWithPaint(Field field) {
		paintImpossibles(field);
		double ret = score(field);
		unpaintImpossibles(field);
		return ret;
	}
	
	/**
	 * Fitness function used to score a board.  Higher score is worse for the player.
	 * @param field
	 * @return
	 */
	public static double score(Field field) {
		if(field.isGameOver())
			return Double.POSITIVE_INFINITY;
		Block[][] f = field.getField();
		paintUnlikelies(f);
		double score = 0;
		int[] stackHeight = new int[Field.WIDTH];
		for(int x = Field.BUFFER; x < Field.WIDTH + Field.BUFFER; x++) {
			double holes = 0;
			for(int y = Field.HEIGHT  + Field.BUFFER - 1; y >= Field.BUFFER; y--) {
				int h = Field.HEIGHT + Field.BUFFER - y;
				Block b = f[y][x];
				if(b != null)
					stackHeight[x-Field.BUFFER] = h;
				if(b != null && b != Block.X && b != Block.G)
					score += 25 + 2 * h;
				else if(b == Block.X) {
					score += 25 * (holes + 1);
					holes++;
				}
				else if(b == Block.G) {
					score += 15 * (holes + 1);
					holes += 0.5;
				}
				else if(b == null) {
//					score += 15 * (holes + 1);
//					holes += 0.5;
//					if(f[y][x-1] != null && f[y][x+1] != null && f[y+1][x] == null)
//						score += h;
				}
			}
//			int w = x - Field.BUFFER;
//			if(w > 0 && stackHeight[w] == stackHeight[w-1])
//				score -= stackHeight[w];
		}
//		// Add in surface smoothness weight
//		int sr = Math.max(stackHeight[1] - stackHeight[0], 0);
//		for(int i = 1; i < stackHeight.length - 2; i++)
//			sr += Math.abs(stackHeight[i] - stackHeight[i+1]);
//		sr += Math.max(stackHeight[stackHeight.length - 2] - stackHeight[stackHeight.length - 1], 0);
//		
//		score += sr * 10;
		
		score -= field.getLines() * 250;
		unpaintUnlikelies(field);
		return score;
	}

	public static void paintImpossibles(Field field) {
		paintImpossibles(field.getField());
	}
	
	public static void paintImpossibles(Block[][] f) {
		for(int y = 1; y < Field.BUFFER + Field.HEIGHT; y++) {
			for(int x = Field.BUFFER; x < Field.BUFFER + Field.WIDTH; x++) {
				if(f[y][x] == null)
					f[y][x] = Block.X;
			}
			for(int x = Field.BUFFER; x < Field.BUFFER + Field.WIDTH; x++) {
				if((f[y-1][x] == null || f[y][x-1] == null || f[y][x+1] == null) && f[y][x] == Block.X)
					f[y][x] = null;
			}
			for(int x = Field.BUFFER + Field.WIDTH - 1; x >= Field.BUFFER; x--) {
				if((f[y-1][x] == null || f[y][x-1] == null || f[y][x+1] == null) && f[y][x] == Block.X)
					f[y][x] = null;
			}
		}
	}

	public static void paintUnlikelies(Field field) {
		paintUnlikelies(field.getField());
	}
	
	public static void paintUnlikelies(Block[][] f) {
		for(int y = 1; y < Field.BUFFER + Field.HEIGHT; y++) {
			for(int x = Field.BUFFER; x < Field.BUFFER + Field.WIDTH; x++) {
				if(f[y][x] == null && f[y][x-1] != null && f[y][x+1] != null)
					f[y][x] = Block.G;
			}
			for(int x = Field.BUFFER; x < Field.BUFFER + Field.WIDTH; x++) {
				if(f[y][x] != null)
					continue;
				if(f[y-1][x] == Block.G || f[y][x-1] == Block.G)
					f[y][x] = Block.G;
			}
			for(int x = Field.BUFFER + Field.WIDTH - 1; x >= Field.BUFFER; x--) {
				if(f[y][x] != null)
					continue;
				if(f[y-1][x] == Block.G || f[y][x+1] == Block.G)
					f[y][x] = Block.G;
			}
			for(int x = Field.BUFFER; x < Field.BUFFER + Field.WIDTH; x++) {
				if(f[y][x] != Block.G)
					continue;
				if(f[y][x+1] == null || f[y][x-1] == null)
					f[y][x] = null;
			}
			for(int x = Field.BUFFER + Field.WIDTH - 1; x >= Field.BUFFER; x--) {
				if(f[y][x] != Block.G)
					continue;
				if(f[y][x+1] == null || f[y][x-1] == null)
					f[y][x] = null;
			}
		}
	}
	
	public static void unpaintUnlikelies(Field field) {
		Block[][] f = field.getField();
		for(int y = 1; y < Field.BUFFER + Field.HEIGHT; y++) {
			for(int x = Field.BUFFER; x < Field.BUFFER + Field.WIDTH; x++) {
				if(f[y][x] == Block.G)
					f[y][x] = null;
			}
		}
	}
	
	public static void unpaintImpossibles(Field field) {
		Block[][] f = field.getField();
		for(int y = 1; y < Field.BUFFER + Field.HEIGHT; y++) {
			for(int x = Field.BUFFER; x < Field.BUFFER + Field.WIDTH; x++) {
				if(f[y][x] == Block.X)
					f[y][x] = null;
			}
		}
	}
}