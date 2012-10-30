package org.eviline.util;

import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.text.ParseException;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eviline.BasicPropertySource;
import org.eviline.Field;
import org.eviline.PropertiedField;
import org.eviline.PropertySource;
import org.eviline.TestableField;
import org.eviline.util.FieldParser.FieldFactory;
import org.eviline.util.FieldParser.LineHandler;

public class TestableFieldParser {
	protected static Pattern PROPERTY = Pattern.compile("(\\w+):(.*)", Pattern.DOTALL);
	protected static FieldFactory fieldFactory = new FieldFactory() {
		@Override
		public Field newField() {
			return new TestableField();
		}
	};
	
	protected LineHandler lineHandler = new LineHandler() {
		@Override
		public boolean handleLine(String line) {
			Matcher m;
			if((m = PROPERTY.matcher(line)).matches())
				fdprops.put(m.group(1), m.group(2));
			else
				return false;
			return true;
		}
	};
	
	protected FieldParser parser;
	
	public TestableFieldParser(FieldParser fparser) {
		this.parser = fparser;
		parser.fieldFactory = fieldFactory;
		parser.lineHandler = lineHandler;
	}
	
	public TestableFieldParser(Iterator<String> lines) {
		this(new FieldParser(lines));
	}
	
	public TestableFieldParser(Reader r) throws IOException {
		this(new FieldParser(r));
	}
	
	public TestableFieldParser(URL resource) throws IOException {
		this(new FieldParser(resource));
	}
	
	protected PropertySource fdprops;
	
	public TestableField next() throws ParseException {
		fdprops = new BasicPropertySource();
		TestableField field = (TestableField) parser.next();
		for(String key : fdprops.keys()) {
			field.put(key, fdprops.get(key));
		}
		return field;
	}
}
