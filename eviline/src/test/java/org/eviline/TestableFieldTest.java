package org.eviline;

import org.junit.Test;


public class TestableFieldTest extends AbstractTest {

	@Test
	public void testEvalRuby() throws Exception {
		TestableField pf = new TestableField();
		pf.setLines(10);
		log.trace(pf.evalRuby("lines"));
	}
	
}
