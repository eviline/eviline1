package org.eviline.fitness;

import org.eviline.BlockType;
import org.eviline.Field;

public class EvilineFitness2 extends DefaultFitness {
	private static final int expectedXCount = Field.WIDTH * Field.BUFFER;
	
	private static int countRow(BlockType[] row) {
		int c = Field.WIDTH;
		for(BlockType b : row)
			if(b == null)
				c--;
		return c;
	}
	
	private static int countField(BlockType[][] f) {
		int c = 0;
		for(BlockType[] row : f) {
			c += countRow(row);
		}
		c -= expectedXCount;
		return c;
	}
	
	private static int countImpossiblesRow(BlockType[] row) {
		int c = 0;
		for(BlockType b : row) {
			if(b == BlockType.M)
				c++;
		}
		return c;
	}
	
	private static int countImpossibles(BlockType[][] f) {
		int c = 0;
		for(BlockType[] row : f) {
			c += countImpossiblesRow(row);
		}
		return 700 * c;
	}
	
	private static boolean surfaceEmpty(BlockType b) {
		return b == BlockType.M || b == null;
	}
	
	private static int countVerticalSurfacesRow(BlockType[] row) {
		int c = 0;
		for(int i = 1; i < row.length; i++) {
			if(surfaceEmpty(row[i-1]) ^ surfaceEmpty(row[i]) && row[i-1] != BlockType.X && row[i] != BlockType.X)
				c++;
		}
		return c;
	}
	
	private static int countVerticalSurfaces(BlockType[][] f) {
		int c = 0;
		for(BlockType[] row : f) {
			c += countVerticalSurfacesRow(row);
		}
		return c;
	}
	
	private static int countHorizSurfacesRow(BlockType[] above, BlockType[] below) {
		int c = 0;
		for(int i = 0; i < above.length; i++) {
			if(surfaceEmpty(above[i]) ^ surfaceEmpty(below[i]))
				c++;
		}
		return c;
	}
	
	private static int countHorizSurfaces(BlockType[][] f) {
		int c = 0;
		BlockType[] above = new BlockType[Field.WIDTH + 2 * Field.BUFFER];
		for(BlockType[] below : f) {
			c += countHorizSurfacesRow(above, below);
			above = below;
		}
		return c - (Field.WIDTH + 2 * Field.BUFFER);
	}
	
	private static int countSurfaces(BlockType[][] f) {
		int vs = countVerticalSurfaces(f);
		int hs = countHorizSurfaces(f);
		return 100 * (int) Math.pow(vs + hs, 3);
	}
	
	private static int maxHeight(BlockType[][] f) {
		int h = Field.HEIGHT + Field.BUFFER;
		for(BlockType[] row : f) {
			for(BlockType b : row) {
				if(b == null || b == BlockType.X || b == BlockType.M)
					continue;
				return h * h * h;
			}
			h--;
		}
		return 0;
	}
	
	private static int impossibleDepth(BlockType[][] f) {
		int tdepth = 0;
		for(int x = 0; x < Field.WIDTH + 2 * Field.BUFFER; x++) {
			int cdepth = 0;
			for(int y = 0; y < Field.HEIGHT + Field.BUFFER; y++) {
				if(f[y][x] == null)
					continue;
				cdepth++;
				if(f[y][x] == BlockType.M) {
					tdepth += 1000 * Math.log(cdepth + 3) / Math.log(1.75);
					break;
				}
			}
		}
		return tdepth;
	}
	
	@Override
	public double score(Field field) {
		BlockType[][] f = field.getField();
		int cf = countField(f);
		int ci = countImpossibles(f);
		int cs = countSurfaces(f);
		int mh = maxHeight(f);
		int id = impossibleDepth(f);
		return cf + ci + cs + mh + id;
	}
	
	public void paintImpossibles(Field field) {
		paintImpossibles(field.getField());
	}
	
	public void paintImpossibles(BlockType[][] f) {
		for(int y = 1; y < Field.BUFFER + Field.HEIGHT; y++) {
			for(int x = Field.BUFFER; x < Field.BUFFER + Field.WIDTH; x++) {
				if(f[y][x] == null)
					f[y][x] = BlockType.M;
			}
			for(int x = Field.BUFFER; x < Field.BUFFER + Field.WIDTH; x++) {
				if((f[y-1][x] == null || f[y][x-1] == null || f[y][x+1] == null) && f[y][x] == BlockType.M)
					f[y][x] = null;
			}
			for(int x = Field.BUFFER + Field.WIDTH - 1; x >= Field.BUFFER; x--) {
				if((f[y-1][x] == null || f[y][x-1] == null || f[y][x+1] == null) && f[y][x] == BlockType.M)
					f[y][x] = null;
			}
		}
	}


}
