package org.eviline;

import org.apache.log4j.Logger;
import org.eviline.AIKernel.Decision;
import org.eviline.AIKernel.QueueContext;
import org.junit.Assert;
import org.junit.Test;

public class AIKernelTest extends AbstractTest {
	private static final Logger log = Logger.getLogger(AIKernelTest.class);
	
	@Test
	public void bestForQueueClearsPossibleLine() {
		ShapeType[] queue = new ShapeType[] { ShapeType.I, ShapeType.L, ShapeType.J };
		Field empty = new Field();
		Decision best = AIKernel.getInstance().bestFor(new QueueContext(empty, queue));
		Decision result = best.deepest();
		Assert.assertEquals(1, result.field.lines - empty.lines);
	}
}
