package org.eviline.ai;

public class AI {

	private static AIKernel instance;

	public static AIKernel getInstance() {
		if(instance == null)
			instance = new DefaultAIKernel();
		return instance;
	}

}
