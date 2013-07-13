package org.eviline.ai2;

public interface AIKernel2 {
	public Decision2[] playerBest(Context2 context);
	public Decision2[] evilineBest(Context2 context);
}
