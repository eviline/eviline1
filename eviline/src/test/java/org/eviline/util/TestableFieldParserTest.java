package org.eviline.util;

import org.eviline.PropertiedField;
import org.junit.Assert;
import org.junit.Test;

public class TestableFieldParserTest {
	@Test
	public void parse() throws Exception {
		TestableFieldParser pfp = new TestableFieldParser(TestableFieldParserTest.class.getResource("TestableFieldParserTest.txt"));
		PropertiedField pf = pfp.next();
		Assert.assertEquals("test", pf.get("name"));
	}
}
