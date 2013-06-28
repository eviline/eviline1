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
		params = new double[11];
		params[S_PARAM] = 3;
		params[Z_PARAM] = 3;
		params[J_PARAM] = 3;
		params[L_PARAM] = 3;
		params[O_PARAM] = 3;
		params[T_PARAM] = 3;
		params[I_PARAM] = 3;
		params[HEIGHT_PARAM] = 3;
		params[ROUGHNESS_PARAM] = 5;
		params[HOLES_PARAM] = 200;
		params[WELL_PARAM] = 10;
	}
	
	@Override
	protected double normalize(double score) {
		return score;
	}

	@Override
	public double score(Field field) {
		int S, Z, J, L, O, T, I, height, roughness, holes, well;
		S = Z = J = L = O = T = I = height = roughness = holes = well = 0;
		
		Block[][] f = field.getField();
		
		int[] heights = new int[Field.WIDTH];
		for(int x = 0; x < Field.WIDTH; x++) {
			int y = -1;
			for(; y < Field.HEIGHT; y++) {
				if(f[y + Field.BUFFER][x + Field.BUFFER] != null)
					break;
			}
			heights[x] = Field.HEIGHT - y;
			for(; y < Field.HEIGHT; y++) {
				if(f[y + Field.BUFFER][x + Field.BUFFER] == null)
					holes++;
			}
		}
		
		for(int x = 0; x < heights.length; x++) {
			I++;
			height += heights[x];
			if(heights[x] > Field.HEIGHT)
				height += 200;
		}
		
		for(int x = 0; x < heights.length - 1; x++) {
			int delta = heights[x] - heights[x+1];
			
			if(delta == 2)
				L++;
			if(delta == 1) {
				S++;
				T++;
			}
			if(delta == 0) {
				J++;
				L++;
				O++;
			}
			if(delta == -1) {
				Z++;
				T++;
			}
			if(delta == -2)
				J++;
		}
		
		roughness += Field.HEIGHT - heights[0];
		
		for(int x = 0; x < heights.length - 2; x++) {
			int delta1 = heights[x] - heights[x+1];
			int delta2 = heights[x+1] - heights[x+2];
			
			if(delta1 == 1 && delta2 == -1)
				T++;
			if(delta1 == 1 && delta2 == 0)
				Z++;
			if(delta1 == 0 && delta2 == -1)
				S++;
			if(delta1 == 0 && delta2 == 0)
				T++;
			if(delta1 == 0 && delta2 == 1)
				J++;
			if(delta1 == -1 && delta2 == 0)
				L++;
			roughness += Math.abs(delta1);
		}
		
		well += heights[heights.length - 1];
		
		for(int x = 0; x < heights.length - 3; x++) {
			int delta1 = heights[x] - heights[x+1];
			int delta2 = heights[x+1] - heights[x+2];
			int delta3 = heights[x+2] - heights[x+3];
			
			if(delta1 == 0 && delta2 == 0 && delta3 == 0)
				I++;
		}
		
		double goodness = 
				params[S_PARAM] * S
				+ params[Z_PARAM] * Z
				+ params[J_PARAM] * J
				+ params[L_PARAM] * L
				+ params[O_PARAM] * O
				+ params[T_PARAM] * T
				+ params[I_PARAM] * I
				- params[HEIGHT_PARAM] * height
				- params[ROUGHNESS_PARAM] * roughness
				- params[HOLES_PARAM] * holes
				- params[WELL_PARAM] * well;
		
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
