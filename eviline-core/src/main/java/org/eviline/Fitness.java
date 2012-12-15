package org.eviline;

public class Fitness {
	
	private static Fitness defaultInstance;
	
	public static Fitness getDefaultInstance() {
		if(defaultInstance == null)
			defaultInstance = new Fitness();
		return defaultInstance;
	}
	
	public static void setDefaultInstance(Fitness instance) {
		Fitness.defaultInstance = instance;
	}
	
	public Fitness() {}
	
	public double scoreWithPaint(Field field) {
		paintImpossibles(field);
		double ret = score(field);
		unpaintImpossibles(field);
		return ret;
	}
	
	protected boolean isSolid(Block b) {
		return b != null && b != Block.G && b != Block.X;
	}
	
	/**
	 * Fitness function used to score a board.  Higher score is worse for the player.
	 * @param field
	 * @return
	 */
	public double score(Field field) {
		if(field.isGameOver())
			return Double.POSITIVE_INFINITY;
		Block[][] f = field.getField();
		paintUnlikelies(f);
		double score = 0;
		int[] stackHeight = new int[Field.WIDTH];
		int maxHeight = 0;
		
		int impossibles = 0;
		int unlikelies = 0;
		int columnTransitions = 0;
		int rowTransitions = 0;
		
		for(int x = Field.BUFFER; x < Field.WIDTH + Field.BUFFER; x++) {
			for(int y = Field.HEIGHT  + Field.BUFFER - 1; y >= 2; y--) {
				int h = Field.HEIGHT + Field.BUFFER - y + 1;
				Block b = f[y][x];
				if(b != null)
					stackHeight[x-Field.BUFFER] = h;
				
				if(isSolid(f[y][x]) ^ isSolid(f[y+1][x]))
					columnTransitions++;
				
				if(b != null && b != Block.X && b != Block.G) {
					score += 15 + Math.pow(1 + h / 20., h);
				}
				else if(b == Block.X) {
					impossibles++;
				}
				else if(b == Block.G) {
					unlikelies++;
				}
			}
			maxHeight = Math.max(maxHeight, stackHeight[x - Field.BUFFER]);
		}
		
		for(int y = Field.HEIGHT  + Field.BUFFER - 1; y >= 2; y--) {
			for(int x = Field.BUFFER; x < Field.WIDTH + Field.BUFFER-1; x++) {
				if(isSolid(f[y][x]) ^ isSolid(f[y][x+1]))
					rowTransitions++;
			}
		}
		
		score += Math.pow(columnTransitions + rowTransitions, 3) * (10 + maxHeight);
		score += Math.pow(2, impossibles) * (10 + maxHeight);
		score += Math.pow(1.75, unlikelies) * (10 + maxHeight);
		
		// Add in surface smoothness weight
		int sr = 0;
		for(int i = 0; i < stackHeight.length - 2; i++)
			sr += Math.pow(1 + Math.abs(stackHeight[i] - stackHeight[i+1]), 2) - 1;
		score += sr * 10 * maxHeight;
		
		// Weigh the lines cleared heavily
		score -= Math.pow(field.lines, maxHeight);
		
		unpaintUnlikelies(field);
		return score;
	}

	public void paintImpossibles(Field field) {
		paintImpossibles(field.getField());
	}
	
	public void paintImpossibles(Block[][] f) {
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

	public void paintUnlikelies(Field field) {
		paintUnlikelies(field.getField());
	}
	
	public void paintUnlikelies(Block[][] f) {
		for(int y = 1; y < Field.BUFFER + Field.HEIGHT; y++) {
			for(int x = Field.BUFFER; x < Field.BUFFER + Field.WIDTH; x++) {
				if(f[y][x] == null && f[y][x-1] != null && f[y][x+1] != null) {
					f[y][x] = Block.G;
				}
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
	
	public void unpaintUnlikelies(Field field) {
		Block[][] f = field.getField();
		for(int y = 1; y < Field.BUFFER + Field.HEIGHT; y++) {
			for(int x = Field.BUFFER; x < Field.BUFFER + Field.WIDTH; x++) {
				if(f[y][x] == Block.G)
					f[y][x] = null;
			}
		}
	}
	
	public void unpaintImpossibles(Field field) {
		Block[][] f = field.getField();
		for(int y = 1; y < Field.BUFFER + Field.HEIGHT; y++) {
			for(int x = Field.BUFFER; x < Field.BUFFER + Field.WIDTH; x++) {
				if(f[y][x] == Block.X)
					f[y][x] = null;
			}
		}
	}
}
