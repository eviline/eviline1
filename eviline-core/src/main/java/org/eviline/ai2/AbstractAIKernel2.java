package org.eviline.ai2;

import org.eviline.ShapeType;

public abstract class AbstractAIKernel2 implements AIKernel2 {

	@Override
	public Decision2[] evilineBest(Context2 context) {
		Decision2 worst = new Decision2().withScore(Double.NEGATIVE_INFINITY);

		ShapeType[] searchSpace;
		if(context.getKnownNext().length > 0)
			searchSpace = new ShapeType[] {context.getKnownNext()[0]};
		else
			searchSpace = ShapeType.values();
		
		for(ShapeType type : searchSpace) {
			
		}
	}

}
