package org.eviline.nullpo;

import java.util.Arrays;

import org.apache.log4j.Logger;

import mu.nu.nullpo.game.component.Block;
import mu.nu.nullpo.game.component.Piece;
import mu.nu.nullpo.game.event.EventReceiver;
import mu.nu.nullpo.game.play.GameEngine;
import mu.nu.nullpo.game.play.GameManager;
import mu.nu.nullpo.game.subsystem.mode.MarathonMode;
import mu.nu.nullpo.util.GeneralUtil;

public class TNMode extends MarathonMode {
	private static final Logger log = Logger.getLogger(TNMode.class);

	protected boolean waiting = false;
	
	protected EventReceiver receiver;
	
	protected Integer lastScoreX;
	protected double[] lastScore;
	
	@Override
	public String getName() {
		return "EVILINE";
	}

	@Override
	public void playerInit(GameEngine engine, int playerID) {
		super.playerInit(engine, playerID);
		receiver = engine.owner.receiver;
		engine.ruleopt = new TNRuleOptions(engine.ruleopt);
		engine.randomizer = GeneralUtil.loadRandomizer(engine.ruleopt.strRandomizer);
		engine.wallkick = GeneralUtil.loadWallkick(engine.ruleopt.strWallkick);
		lastScoreX = null;
		lastScore = null;
	}
	
	@Override
	public boolean onSetting(GameEngine engine, int playerID) {
		boolean ret = super.onSetting(engine, playerID);
		((TNRandomizer) engine.randomizer).setEngine(engine);
		engine.nextPieceArraySize = 1;
		if(engine.nextPieceArrayID != null)
			engine.nextPieceArrayID = Arrays.copyOf(engine.nextPieceArrayID, 1);
		if(engine.nextPieceArrayObject != null)
			engine.nextPieceArrayObject = Arrays.copyOf(engine.nextPieceArrayObject, 1);
		return ret;
	}
	
	public static Piece newPiece(int id) {
		if(id != Piece.PIECE_NONE)
			return new Piece(id);
		Piece p = new Piece();
//		p.id = Piece.PIECE_NONE;
		p.dataX = new int[0][0];
		p.dataY = new int[0][0];
		return p;
	}
	
	@Override
	public boolean onMove(GameEngine engine, int playerID) {
		
		if(lastScoreX == null || lastScoreX != engine.nowPieceX) {
			lastScoreX = engine.nowPieceX;
			lastScore = null;
		}
		
		if(!waiting)
			return super.onMove(engine, playerID);
		
		int next = engine.randomizer.next();
		if(next == -1)
			return true;
		
		regenerate(engine);
//		engine.statARE();
		waiting = false;
		
		return true;
	}

	public void regenerate(GameEngine engine) {
		int next = engine.randomizer.next();
		
		engine.nextPieceArrayID[0] = next;
		
		try {
			for(int i = 0; i < engine.nextPieceArrayObject.length; i++) {
				engine.nextPieceArrayObject[i] = newPiece(engine.nextPieceArrayID[i]);
				engine.nextPieceArrayObject[i].direction = engine.ruleopt.pieceDefaultDirection[engine.nextPieceArrayObject[i].id];
				if(engine.nextPieceArrayObject[i].direction >= Piece.DIRECTION_COUNT) {
					engine.nextPieceArrayObject[i].direction = engine.random.nextInt(Piece.DIRECTION_COUNT);
				}
				engine.nextPieceArrayObject[i].connectBlocks = engine.connectBlocks;
				engine.nextPieceArrayObject[i].setColor(engine.ruleopt.pieceColor[engine.nextPieceArrayObject[i].id]);
				engine.nextPieceArrayObject[i].setSkin(engine.getSkin());
				engine.nextPieceArrayObject[i].updateConnectData();
				engine.nextPieceArrayObject[i].setAttribute(Block.BLOCK_ATTRIBUTE_VISIBLE, true);
				engine.nextPieceArrayObject[i].setAttribute(Block.BLOCK_ATTRIBUTE_BONE, engine.bone);
			}
			if (engine.randomBlockColor)
			{
				if (engine.blockColors.length < engine.numColors || engine.numColors < 1)
					engine.numColors = engine.blockColors.length;
				for(int i = 0; i < engine.nextPieceArrayObject.length; i++) {
					int size = engine.nextPieceArrayObject[i].getMaxBlock();
					int[] colors = new int[size];
					for (int j = 0; j < size; j++)
						colors[j] = engine.blockColors[engine.random.nextInt(engine.numColors)];
					engine.nextPieceArrayObject[i].setColor(colors);
					engine.nextPieceArrayObject[i].updateConnectData();
				}
			}
		} catch(RuntimeException re) {
		}
	}
	
	@Override
	public void pieceLocked(GameEngine engine, int playerID, int lines) {
		((TNRandomizer) engine.randomizer).regenerate = true;

		regenerate(engine);
		
		waiting = true;
		lastScoreX = null;
		lastScore = null;
		
		super.pieceLocked(engine, playerID, lines);
	}
	
	@Override
	public void renderLast(GameEngine engine, int playerID) {
		super.renderLast(engine, playerID);
		if(lastScore == null)
			lastScore = ((TNRandomizer) engine.randomizer).score();
		receiver.drawScoreFont(engine, playerID, 0, 17, ((TNRandomizer) engine.randomizer).getName(), EventReceiver.COLOR_BLUE);
		String score = String.format("%1.0f(%s%1.0f)", lastScore[0], lastScore[1] > 0 ? "+" : "", lastScore[1]).toUpperCase();
		receiver.drawScoreFont(engine, playerID, 0, 18, score, EventReceiver.COLOR_WHITE);
	}

	@Override
	public boolean onLineClear(GameEngine engine, int playerID) {
		 int lines = engine.statistics.lines;
		 ((TNRandomizer) engine.randomizer).field.setLines(lines);
		 return super.onLineClear(engine, playerID);
	}
}