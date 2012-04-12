package org.tetrevil.wobj;

import java.util.Date;

public class EndedGame {
	private Integer gameId;
	private Date ts;
	private Integer reasonId;
	public Integer getGameId() {
		return gameId;
	}
	public void setGameId(Integer gameId) {
		this.gameId = gameId;
	}
	public Date getTs() {
		return ts;
	}
	public void setTs(Date ts) {
		this.ts = ts;
	}
	public Integer getReasonId() {
		return reasonId;
	}
	public void setReasonId(Integer reasonId) {
		this.reasonId = reasonId;
	}
}
