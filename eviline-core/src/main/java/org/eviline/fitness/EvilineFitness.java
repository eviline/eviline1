package org.eviline.fitness;

import org.eviline.Block;
import org.eviline.BlockType;
import org.eviline.Field;

public class EvilineFitness extends AbstractFitness {
	
	
	public EvilineFitness() {
		super(new double[] {
				1.4, // block height
				3.25, // row/col trans
				2, // impossibles
				1.75, // unlikelies
				50, // smoothness
				1 // line clears
		});
	}
	
	public static interface Weights {
		public static final int BLOCK_HEIGHT = 0;
		public static final int TRANSITION_EXP = 1;
		public static final int IMPOSSIBLE_POWER = 2;
		public static final int UNLIKELY_POWER = 3;
		public static final int SMOOTHNESS_MULT = 4;
		public static final int CLEARED_LINES = 5;
	}
	
	@Override
	protected double score(Field field) {
		return 0;
	}
	
	/**
	 * Fitness function used to score a board.  Higher score is worse for the player.
	 * @param field
	 * @return
	 */
	@Override
	public double score(Field before, Field field) {
		if(field.isGameOver())
			return Double.POSITIVE_INFINITY;
		Block[][] f = field.getField();
		double score = 0;
		int[] stackHeight = new int[field.getWidth()];
		int maxHeight = 0;
		
		int impossibles = 0;
		int unlikelies = 0;
		double columnTransitions = 0;
		int rowTransitions = 0;
		
		for(int x = Field.BUFFER; x < field.getWidth() + Field.BUFFER; x++) {
			for(int y = field.getHeight()  + Field.BUFFER - 1; y >= 2; y--) {
				int h = field.getHeight() + Field.BUFFER - y + 1;
				Block b = f[y][x];
				if(b.isSolid())
					stackHeight[x-Field.BUFFER] = h;
				
				if(f[y][x].isShape() ^ f[y+1][x].isShape())
					columnTransitions += 1;
				
				if(b.isShape()) {
					score += 15 + Math.pow(h * parameters[Weights.BLOCK_HEIGHT], 1 / (2.6 - h / 10.));
				}
				else if(b.isImpossible()) {
					impossibles++;
				}
				else if(b.isUnlikely()) {
					unlikelies++;
				}
			}
			maxHeight = Math.max(maxHeight, stackHeight[x - Field.BUFFER]);
		}
		
		for(int y = field.getHeight()  + Field.BUFFER - 1; y >= 2; y--) {
			for(int x = Field.BUFFER; x < field.getWidth() + Field.BUFFER-1; x++) {
				if(f[y][x].isShape() ^ f[y][x+1].isShape())
					rowTransitions++;
			}
		}
		
		score += Math.pow(columnTransitions + rowTransitions, parameters[Weights.TRANSITION_EXP]) * (5 + maxHeight);
		score += Math.pow(parameters[Weights.IMPOSSIBLE_POWER], impossibles) * (10 + maxHeight);
		score += Math.pow(parameters[Weights.UNLIKELY_POWER], unlikelies) * (10 + maxHeight);
		
		// Add in surface smoothness weight
		int sr = 0;
		for(int i = 1; i < stackHeight.length - 2; i++)
			sr += Math.pow(1 + Math.abs(stackHeight[i] - stackHeight[i+1]), 2) - 1;
		score += sr * parameters[Weights.SMOOTHNESS_MULT] * maxHeight;
		
		// Weigh the lines cleared heavily
		score -= Math.pow(field.getLines() * parameters[Weights.CLEARED_LINES], maxHeight);
		
//		score += Math.pow(maxHeight, 1/(2.6-maxHeight/10.));
		
		return score;
	}

}
