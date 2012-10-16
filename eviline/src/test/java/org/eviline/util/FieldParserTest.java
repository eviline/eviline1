package org.eviline.util;

import java.io.InputStreamReader;
import java.util.NoSuchElementException;

import org.eviline.Field;
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
