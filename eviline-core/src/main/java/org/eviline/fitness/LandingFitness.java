package org.eviline.fitness;

import org.eviline.Block;
import org.eviline.BlockType;
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
	public static final int PIT_PARAM = 11;
	public static final int HORIZONTAL_HOLES_PARAM = 12;

	public LandingFitness() {
		super(new double[] {
//				0.02779301043939461, 0.9420139338269574, 0.3751151426471481, 0.13186249481739487, 1.225531289710602, 0.710819777297161, 0.8415507978811427, 2.495147273719192, 1.4071618232569005, 0.27405431576057404, 0.573644020964927, 1.190461320283978
				0.2631856775205221, 0.8759698216888646, 0.5325937620257688, 0.7663549125268573, 1.2735935787349337, 0.11702156223274929, 0.6180057893684847, 
					2.8859244803463627, 
					1.8558738669091565, 
					1.9361504351528638, 
					0.8292012819817409, 
					0.9298931613127354,
					8
/*
				2.5, 
				2.5, 
				4.1, 
				4.1, 
				3.2, 
				4.1, 
				2.9,
				//
				7.817908195934679, 9.42147235655372, 9.741261438133863, 1.6975817931514334,
				5
*/
		});
	}

	@Override
	public double score(Field field) {
		int S, Z, J, L, O, T, I;
		S = Z = J = L = O = T = I = 0;

		double height, roughness, holes, well, pit, horizontalHoles;
		height = roughness = holes = well = pit = horizontalHoles = 0;
		
		Block[][] f = field.getField();
		
		int garbageY = field.getHeight();
		int garbageHeight;

		for(int x = 0; x < field.getWidth(); x++) {
			for(int y = field.getHeight() - 1; y >= 0; y--) {
				if(y > garbageY)
					continue;
				if(field.getBlock(x+Field.BUFFER, y+Field.BUFFER).isGarbage())
					garbageY = y;
			}
		}
		
		garbageHeight = field.getHeight() - garbageY;

		int[] heights = new int[field.getWidth()];
		for(int x = 0; x < field.getWidth(); x++) {
			int bx = x + Field.BUFFER;
			int y = -1;
			for(; y < field.getHeight(); y++) {
				int by = y + Field.BUFFER;
				if(f[by][bx] != null)
					break;
			}
			heights[x] = field.getHeight() - y;
			int hy = y;
			int hc = 0;
			for(; y < field.getHeight(); y++) {
				int by = y + Field.BUFFER;
				if(f[by][bx] == null)
					holes += field.getHeight() + (heights[x] - garbageHeight) - hc++;
			}
		}
		
		for(int y = 0; y < field.getHeight(); y++) {
			int by = y + Field.BUFFER;
			for(int x = 0; x < field.getWidth() - 1; x++) {
				int bx = x + Field.BUFFER;
				if((f[by][bx] == null) != (f[by][bx + 1] == null))
					horizontalHoles += 1;
			}
		}
		
		for(int x = 0; x < heights.length; x++) {
			I++;
			height += heights[x] - (field.getHeight() - garbageY);
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
			if(delta1 > 0 && delta2 < 0)
				pit += Math.pow(Math.min(delta1, -delta2), 2);
			if(delta1 < 0 && delta2 > 0)
				pit += Math.pow(Math.min(-delta1, delta2), 2);
		}
		
		for(int x = 0; x < heights.length - 3; x++) {
			int delta1 = heights[x] - heights[x+1];
			int delta2 = heights[x+1] - heights[x+2];
			int delta3 = heights[x+2] - heights[x+3];
			
			if(delta1 == 0 && delta2 == 0 && delta3 == 0)
				I++;
			
		}
		
		
		
		well += Math.pow(heights[heights.length - 2] + 1 - (field.getHeight() - garbageY), 2);
		well += Math.pow(heights[heights.length - 1] + 1 - (field.getHeight() - garbageY), 2);
		
		
		double goodness = 
				Math.pow(parameters[S_PARAM], S)
				+ Math.pow(parameters[Z_PARAM], Z)
				+ Math.pow(parameters[J_PARAM], J)
				+ Math.pow(parameters[L_PARAM], L)
				+ Math.pow(parameters[O_PARAM], O)
				+ Math.pow(parameters[T_PARAM], T)
				+ Math.pow(parameters[I_PARAM], I)
				- Math.pow(parameters[HEIGHT_PARAM], height)
				- Math.pow(parameters[ROUGHNESS_PARAM], roughness)
				- Math.pow(parameters[HOLES_PARAM], holes)
				- Math.pow(parameters[WELL_PARAM], well)
				- Math.pow(parameters[PIT_PARAM], pit)
				- Math.pow(parameters[HORIZONTAL_HOLES_PARAM], horizontalHoles)
				;
		
		return -goodness;
	}

}
