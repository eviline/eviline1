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
import org.eviline.util.FieldParser.FieldFactory;
import org.eviline.util.FieldParser.LineHandler;

public class PropertiedFieldParser {
	protected static Pattern PROPERTY = Pattern.compile("(\\w+)=(.*)");
	protected static FieldFactory fieldFactory = new FieldFactory() {
		@Override
		public Field newField() {
			return new PropertiedField();
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
	
	public PropertiedFieldParser(FieldParser fparser) {
		this.parser = fparser;
		parser.fieldFactory = fieldFactory;
		parser.lineHandler = lineHandler;
	}
	
	public PropertiedFieldParser(Iterator<String> lines) {
		this(new FieldParser(lines));
	}
	
	public PropertiedFieldParser(Reader r) throws IOException {
		this(new FieldParser(r));
	}
	
	public PropertiedFieldParser(URL resource) throws IOException {
		this(new FieldParser(resource));
	}
	
	protected PropertySource fdprops;
	
	public PropertiedField next() throws ParseException {
		fdprops = new BasicPropertySource();
		PropertiedField field = (PropertiedField) parser.next();
		for(String key : fdprops.keys()) {
			field.put(key, fdprops.get(key));
		}
		return field;
	}
}
