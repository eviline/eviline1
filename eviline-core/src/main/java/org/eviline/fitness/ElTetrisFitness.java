package org.eviline.fitness;

import org.eviline.Block;
import org.eviline.BlockType;
import org.eviline.Field;

public class ElTetrisFitness extends AbstractFitness {
	
	private static final double LANDING_HEIGHT_WEIGHT = -4.500158825082766;
	private static final double ROWS_ELIMINATED_WEIGHT = 3.4181268101392694;
	private static final double ROW_TRANSITIONS_WEIGHT = -3.2178882868487753;
	private static final double COLUMN_TRANSITIONS_WEIGHT = -9.348695305445199;
	private static final double NUMBER_OF_HOLES_WEIGHT = -7.899265427351652;
	private static final double WELL_SUMS_WEIGHT = -3.3855972247263626;
	
	public ElTetrisFitness() {
		super(new double[0]);
	}

	@Override
	protected double score(Field field) {
		field = field.clone();
		Block[][] f = field.getField();
		
		int landingHeight;
		int rowsEliminated;
		int rowTransitions;
		int columnTransitions;
		int numberOfHoles;
		int wellSums;
		
		// Approximate landing height
		landingHeight = Field.BUFFER + field.getHeight() - field.getShapeY();
		
		// Approximate rows eliminated
		rowsEliminated = field.getLines();
		
		// row transitions
		rowTransitions = 0;
		for(int y = Field.BUFFER; y < Field.BUFFER + field.getHeight(); y++) {
			for(int x = Field.BUFFER; x < Field.BUFFER + field.getWidth() - 1; x++) {
				if(f[y][x].isEmpty() && f[y][x+1].isEmpty() || f[y][x] != null && f[y][x+1] == null)
					rowTransitions++;
			}
		}
		
		// column transitions
		columnTransitions = 0;
		for(int y = Field.BUFFER; y < Field.BUFFER + field.getHeight() - 1; y++) {
			for(int x = Field.BUFFER; x < Field.BUFFER + field.getHeight(); x++) {
				columnTransitions++;
			}
		}
		
		// number of holes
		numberOfHoles = 0;
		for(int x = Field.BUFFER; x < Field.BUFFER + field.getHeight(); x++) {
			boolean hole = false;
			for(int y = Field.BUFFER; y < Field.BUFFER + field.getHeight(); y++) {
				if(f[y][x] != null)
					hole = true;
				else if(hole)
					numberOfHoles++;
			}
		}
		
		// well sums
		wellSums = 0;
		for(int x = Field.BUFFER; x < Field.BUFFER + field.getWidth(); x++) {
			for(int y = Field.BUFFER; y < Field.BUFFER + field.getHeight(); y++) {
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
}
