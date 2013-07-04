package org.eviline.fitness;

import org.eviline.Block;
import org.eviline.Field;

public class LandingFitness extends AbstractFitness {
	
	public static final int S_PARAM = 0;
	public static final int Z_PARAM = 1;
	public static final int J_PARAM = 2;
	public static final int L_PARAM = 3;
	public static final int O_PARAM = 4;
	public static final int T_PARAM = 5;
	public static final int I_PARAM = 6;
	public static final int HEIGHT_PARAM = 7;
	public static final int ROUGHNESS_PARAM = 8;
	public static final int HOLES_PARAM = 9;
	public static final int WELL_PARAM = 10;

	public LandingFitness() {
		params = new double[] {
				2.7792122510491297, 3.9936223744526878, 2.481105099024047, 2.9020435073415127, 3.4107156551893136, 3.9439388271046623, 2.1680167486401123, 2.8436487205533245, 2.4190790896043466, 4.126016334038107, 5.309484497477328
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

		double height, roughness, holes, well;
		height = roughness = holes = well = 0;
		
		Block[][] f = field.getField();
		
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
			int hy = y;
			int hc = 0;
			for(; y < Field.HEIGHT; y++) {
				int by = y + Field.BUFFER;
				if(f[by][bx] == null)
					holes += Field.HEIGHT + (y - hy) - hc++;
			}
		}
		
		for(int x = 0; x < heights.length; x++) {
			I++;
			height += heights[x];
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

			if(x < heights.length - 3)
				roughness += Math.pow(Math.abs(delta1), 2);
			else if(x > heights.length - 3)
				roughness += Math.pow(Math.abs(delta1), 3);
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
		
		
		
		well += Math.pow(heights[heights.length - 2], 2);
		well += Math.pow(heights[heights.length - 1], 2);
		
		
		double goodness = 
				Math.pow(params[S_PARAM], S)
				+ Math.pow(params[Z_PARAM], Z)
				+ Math.pow(params[J_PARAM], J)
				+ Math.pow(params[L_PARAM], L)
				+ Math.pow(params[O_PARAM], O)
				+ Math.pow(params[T_PARAM], T)
				+ Math.pow(params[I_PARAM], I)
				- Math.pow(params[HEIGHT_PARAM], height)
				- Math.pow(params[ROUGHNESS_PARAM], roughness)
				- Math.pow(params[HOLES_PARAM], holes)
				- Math.pow(params[WELL_PARAM], well)
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
