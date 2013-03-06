package org.eviline.fitness;

import org.eviline.Block;
import org.eviline.Field;

public class EvilineFitness extends AbstractFitness {
	
	
	public EvilineFitness() {}
	
	public static interface Weights {
		public static final int BLOCK_HEIGHT = 0;
		public static final int TRANSITION_EXP = 1;
		public static final int IMPOSSIBLE_POWER = 2;
		public static final int UNLIKELY_POWER = 3;
		public static final int SMOOTHNESS_MULT = 4;
		public static final int CLEARED_LINES = 5;
	}
	
	private double[] params = new double[] {
			1.4, // block height
			3.25, // row/col trans
			2, // impossibles
			1.75, // unlikelies
			50, // smoothness
			1 // line clears
	};
	
	@Override
	public double[] getParams() {
		return params;
	}
	
	@Override
	protected double normalize(double score) {
		return score;
	}
	
	@Override
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
	@Override
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
					score += 15 + Math.pow(h * params[Weights.BLOCK_HEIGHT], 1 / (2.6 - h / 10.));
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
		
		score += Math.pow(columnTransitions + rowTransitions, params[Weights.TRANSITION_EXP]) * (5 + maxHeight);
		score += Math.pow(params[Weights.IMPOSSIBLE_POWER], impossibles) * (10 + maxHeight);
		score += Math.pow(params[Weights.UNLIKELY_POWER], unlikelies) * (10 + maxHeight);
		
		// Add in surface smoothness weight
		int sr = 0;
		for(int i = 1; i < stackHeight.length - 2; i++)
			sr += Math.pow(1 + Math.abs(stackHeight[i] - stackHeight[i+1]), 2) - 1;
		score += sr * params[Weights.SMOOTHNESS_MULT] * maxHeight;
		
		// Weigh the lines cleared heavily
		score -= Math.pow(field.getLines() * params[Weights.CLEARED_LINES], maxHeight);
		
//		score += Math.pow(maxHeight, 1/(2.6-maxHeight/10.));
		
		unpaintUnlikelies(field);
		return score;
	}

	@Override
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

	@Override
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
	
	@Override
	public void unpaintUnlikelies(Field field) {
		Block[][] f = field.getField();
		for(int y = 1; y < Field.BUFFER + Field.HEIGHT; y++) {
			for(int x = Field.BUFFER; x < Field.BUFFER + Field.WIDTH; x++) {
				if(f[y][x] == Block.G)
					f[y][x] = null;
			}
		}
	}
	
	@Override
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
