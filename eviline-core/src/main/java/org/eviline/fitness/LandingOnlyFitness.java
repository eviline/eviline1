package org.eviline.fitness;

import org.eviline.Block;
import org.eviline.Field;

public class LandingOnlyFitness extends AbstractFitness {
	public static final int S_PARAM = 0;
	public static final int Z_PARAM = 1;
	public static final int J_PARAM = 2;
	public static final int L_PARAM = 3;
	public static final int O_PARAM = 4;
	public static final int T_PARAM = 5;
	public static final int I_PARAM = 6;

	public LandingOnlyFitness() {
		params = new double[] {
				0.2631856775205221, 0.8759698216888646, 0.5325937620257688, 0.7663549125268573, 1.2735935787349337, 0.11702156223274929, 0.6180057893684847 
		};
	}
	
	@Override
	protected double normalize(double score) {
		return score;
	}

	@Override
	public double score(Field field) {
		int S, Z, J, L, O, T, I;
		S = Z = J = L = O = T = I = 0;

		Block[][] f = field.getField();
		
		int garbageY = Field.HEIGHT;
		int garbageHeight;

		for(int x = 0; x < Field.WIDTH; x++) {
			for(int y = Field.HEIGHT - 1; y >= 0; y--) {
				if(y > garbageY)
					continue;
				if(field.getBlock(x+Field.BUFFER, y+Field.BUFFER) == Block.GARBAGE)
					garbageY = y;
			}
		}
		
		garbageHeight = Field.HEIGHT - garbageY;

		int[] heights = new int[Field.WIDTH];
		for(int x = 0; x < Field.WIDTH; x++) {
			int bx = x + Field.BUFFER;
			int y = -1;
			for(; y < Field.HEIGHT; y++) {
				int by = y + Field.BUFFER;
				if(f[by][bx] != null)
					break;
			}
			heights[x] = Field.HEIGHT - y;
		}
		
		for(int x = 0; x < heights.length; x++) {
			I++;
		}
		
		for(int x = 0; x < heights.length - 1; x++) {
			int delta1 = heights[x] - heights[x+1];
			
			if(delta1 == 2)
				L++;
			if(delta1 == 1) {
				S++;
				T++;
			}
			if(delta1 == 0) {
				J++;
				L++;
				O++;
			}
			if(delta1 == -1) {
				Z++;
				T++;
			}
			if(delta1 == -2)
				J++;
		}
		
		for(int x = 0; x < heights.length - 2; x++) {
			int delta1 = heights[x] - heights[x+1];
			int delta2 = heights[x+1] - heights[x+2];
			
			if(delta1 == 1 && delta2 == -1)
				T++;
			if(delta1 == 1 && delta2 == 0)
				Z++;
			if(delta1 == 0 && delta2 == -1)
				S++;
			if(delta1 == 0 && delta2 == 0) {
				T++;
				J++;
				L++;
			}
			if(delta1 == 0 && delta2 == 1)
				J++;
			if(delta1 == -1 && delta2 == 0)
				L++;
		}
		
		for(int x = 0; x < heights.length - 3; x++) {
			int delta1 = heights[x] - heights[x+1];
			int delta2 = heights[x+1] - heights[x+2];
			int delta3 = heights[x+2] - heights[x+3];
			
			if(delta1 == 0 && delta2 == 0 && delta3 == 0)
				I++;
			
		}
		
		double goodness = 
				Math.pow(params[S_PARAM], S)
				+ Math.pow(params[Z_PARAM], Z)
				+ Math.pow(params[J_PARAM], J)
				+ Math.pow(params[L_PARAM], L)
				+ Math.pow(params[O_PARAM], O)
				+ Math.pow(params[T_PARAM], T)
				+ Math.pow(params[I_PARAM], I)
				;
		
		return -goodness;
	}

	@Override
	public void paintImpossibles(Field field) {
	}

	@Override
	public void paintUnlikelies(Field field) {
	}

	@Override
	public void unpaintUnlikelies(Field field) {
	}

	@Override
	public void unpaintImpossibles(Field field) {
	}


}
