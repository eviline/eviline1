package org.tetrevil.srv.db;

import org.tetrevil.wobj.WebScore;

public interface ScoreMapper {
	public void insert(WebScore score);
	public WebScore highScore(WebScore params);
}
