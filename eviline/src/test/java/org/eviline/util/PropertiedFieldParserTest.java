package org.eviline.util;

import org.eviline.PropertiedField;
import org.junit.Assert;
import org.junit.Test;

public class PropertiedFieldParserTest {
	@Test
	public void parse() throws Exception {
		PropertiedFieldParser pfp = new PropertiedFieldParser(PropertiedFieldParserTest.class.getResource("PropertiedFieldParserTest.txt"));
		PropertiedField pf = pfp.next();
		Assert.assertEquals("test", pf.get("name"));
	}
}
