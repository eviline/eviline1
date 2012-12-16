package org.eviline.fitness;

import org.eviline.Block;
import org.eviline.Field;

public class ElTetrisFitness extends Fitness {
	
	private static final double LANDING_HEIGHT_WEIGHT = -4.500158825082766;
	private static final double ROWS_ELIMINATED_WEIGHT = 3.4181268101392694;
	private static final double ROW_TRANSITIONS_WEIGHT = -3.2178882868487753;
	private static final double COLUMN_TRANSITIONS_WEIGHT = -9.348695305445199;
	private static final double NUMBER_OF_HOLES_WEIGHT = -7.899265427351652;
	private static final double WELL_SUMS_WEIGHT = -3.3855972247263626;
	

	@Override
	protected double normalize(double score) {
		return score * 100;
	}
	
	@Override
	public double scoreWithPaint(Field field) {
		return super.scoreWithPaint(field);
	}

	@Override
	public double score(Field field) {
		field = field.copy();
		Block[][] f = field.getField();
		
		int landingHeight;
		int rowsEliminated;
		int rowTransitions;
		int columnTransitions;
		int numberOfHoles;
		int wellSums;
		
		// Approximate landing height
		landingHeight = Field.BUFFER + Field.HEIGHT - field.getShapeY();
		
		// Approximate rows eliminated
		rowsEliminated = field.getLines();
		
		// row transitions
		rowTransitions = 0;
		for(int y = Field.BUFFER; y < Field.BUFFER + Field.HEIGHT; y++) {
			for(int x = Field.BUFFER; x < Field.BUFFER + Field.WIDTH - 1; x++) {
				if(f[y][x] == null && f[y][x+1] != null || f[y][x] != null && f[y][x+1] == null)
					rowTransitions++;
			}
		}
		
		// column transitions
		columnTransitions = 0;
		for(int y = Field.BUFFER; y < Field.BUFFER + Field.HEIGHT - 1; y++) {
			for(int x = Field.BUFFER; x < Field.BUFFER + Field.WIDTH; x++) {
				columnTransitions++;
			}
		}
		
		// number of holes
		numberOfHoles = 0;
		for(int x = Field.BUFFER; x < Field.BUFFER + Field.WIDTH; x++) {
			boolean hole = false;
			for(int y = Field.BUFFER; y < Field.BUFFER + Field.HEIGHT; y++) {
				if(f[y][x] != null)
					hole = true;
				else if(hole)
					numberOfHoles++;
			}
		}
		
		// well sums
		wellSums = 0;
		for(int x = Field.BUFFER; x < Field.BUFFER + Field.WIDTH; x++) {
			for(int y = Field.BUFFER; y < Field.BUFFER + Field.HEIGHT; y++) {
				if(f[y][x-1] != null && f[y][x] == null && f[y][x+1] != null)
					wellSums++;
			}
		}
		
		double score =
				landingHeight * LANDING_HEIGHT_WEIGHT
				+ rowsEliminated * ROWS_ELIMINATED_WEIGHT
				+ rowTransitions * ROW_TRANSITIONS_WEIGHT
				+ columnTransitions * COLUMN_TRANSITIONS_WEIGHT
				+ numberOfHoles * NUMBER_OF_HOLES_WEIGHT
				+ wellSums * WELL_SUMS_WEIGHT;
		
		return -score; // an eviline fitness function gives high scores to bad values, not good ones
	}

	@Override
	public void paintImpossibles(Field field) {
	}

	@Override
	public void paintImpossibles(Block[][] f) {
	}

	@Override
	public void paintUnlikelies(Field field) {
	}

	@Override
	public void paintUnlikelies(Block[][] f) {
	}

	@Override
	public void unpaintUnlikelies(Field field) {
	}

	@Override
	public void unpaintImpossibles(Field field) {
	}

}
