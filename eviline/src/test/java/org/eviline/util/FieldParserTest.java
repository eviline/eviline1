package org.eviline.util;

import java.util.NoSuchElementException;

import org.junit.Test;

public class FieldParserTest extends AbstractUtilTest {
	@Test
	public void parseTestFields() throws Exception {
		FieldParser fp = new FieldParser(FieldParserTest.class.getResource("FieldParserTest.txt"));
		while(true) {
			try {
				log.trace(fp.next());
			} catch(NoSuchElementException nsee) {
				break;
			}
		}
	}
}
