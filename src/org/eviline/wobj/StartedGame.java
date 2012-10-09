package org.eviline.wobj;

import java.util.Date;

public class StartedGame {
	private Integer gameId;
	private String name;
	private Integer standalone;
	private Date ts;
	private Integer depth;
	private Double rfactor;
	private Integer distribution;
	private String randomizer;
	private Integer adaptive;
	public Integer getGameId() {
		return gameId;
	}
	public void setGameId(Integer gameId) {
		this.gameId = gameId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getStandalone() {
		return standalone;
	}
	public void setStandalone(Integer standalone) {
		this.standalone = standalone;
	}
	public Date getTs() {
		return ts;
	}
	public void setTs(Date ts) {
		this.ts = ts;
	}
	public Integer getDepth() {
		return depth;
	}
	public void setDepth(Integer depth) {
		this.depth = depth;
	}
	public Double getRfactor() {
		return rfactor;
	}
	public void setRfactor(Double rfactor) {
		this.rfactor = rfactor;
	}
	public Integer getDistribution() {
		return distribution;
	}
	public void setDistribution(Integer distribution) {
		this.distribution = distribution;
	}
	public String getRandomizer() {
		return randomizer;
	}
	public void setRandomizer(String randomizer) {
		this.randomizer = randomizer;
	}
	public Integer getAdaptive() {
		return adaptive;
	}
	public void setAdaptive(Integer adaptive) {
		this.adaptive = adaptive;
	}
}
