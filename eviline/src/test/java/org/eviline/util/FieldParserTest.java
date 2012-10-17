package org.eviline.util;

import java.util.NoSuchElementException;

import org.junit.Test;

public class FieldParserTest extends AbstractUtilTest {
	@Test
	public void parseTestFields() throws Exception {
		FieldParser fp = new FieldParser(FieldParserTest.class.getResource("FieldParserTest.txt"));
		log.trace(fp.next());
	}
}
