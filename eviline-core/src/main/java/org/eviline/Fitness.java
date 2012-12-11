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
		int maxHeight = 0;
		for(int x = Field.BUFFER; x < Field.WIDTH + Field.BUFFER; x++) {
			double holes = 0;
			double overhangs = 0;
			for(int y = Field.HEIGHT  + Field.BUFFER - 1; y >= Field.BUFFER; y--) {
				int h = Field.HEIGHT + Field.BUFFER - y + 1;
				double ph = Math.pow(1.2, h);
				double mph = (h + 4) * ph;
				Block b = f[y][x];
				if(b != null)
					stackHeight[x-Field.BUFFER] = h;
				if(b != null && b != Block.X && b != Block.G) {
					score += 15 + 5 * h * h;
					score += 10 * holes * h;
					score += 8 * overhangs * h;
//					if(f[y+1][x] != null && f[y+1][x] != Block.X && f[y+1][x] != Block.G && overhangs >= 0.3) {
//						overhangs -= 0.3;
//					}
					overhangs = 0;
					if(f[y+1][x] != null && f[y+1][x] != Block.X && f[y+1][x] != Block.G && holes >= 0.3) {
						holes -= 0.3;
					}
				}
				else if(b == Block.X) {
					holes++;
					score += 150;
				}
				else if(b == Block.G) {
					overhangs++;
					score += 150;
				}
				else if(b == null) {
					overhangs++;
//					if(f[y+1][x] == Block.G) {
//						score += 125 * overhangs;
//					}
//					score += 15 * (holes + 1);
//					holes += 0.5;
//					if(f[y][x-1] != null && f[y][x+1] != null && f[y+1][x] == null)
//						score += h;
				}
			}
			maxHeight = Math.max(maxHeight, stackHeight[x - Field.BUFFER]);
//			int w = x - Field.BUFFER;
//			if(w > 0 && stackHeight[w] == stackHeight[w-1])
//				score -= stackHeight[w];
		}
		
		// Add in surface smoothness weight
		int sr = 0;
//		sr += Math.max(stackHeight[1] - stackHeight[0], 0);
		for(int i = 1; i < stackHeight.length - 3; i++)
			sr += Math.abs(stackHeight[i] - stackHeight[i+1]);
//		sr += Math.max(stackHeight[stackHeight.length - 2] - stackHeight[stackHeight.length - 1], 0);
		
		score += sr * 10;
		
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
