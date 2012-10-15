package org.eviline.srv.db;

import org.eviline.wobj.WebScore;

public interface ScoreMapper {
	public void insert(WebScore score);
	public WebScore highScore(WebScore params);
}
