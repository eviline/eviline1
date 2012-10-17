package org.eviline;

import org.junit.Test;


public class PropertiedFieldTest extends AbstractTest {

	@Test
	public void testEvalRuby() throws Exception {
		PropertiedField pf = new PropertiedField();
		pf.setLines(10);
		log.trace(pf.evalRuby("lines"));
	}
	
}
