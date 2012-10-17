package org.eviline.util;

import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eviline.BasicPropertySource;
import org.eviline.Field;
import org.eviline.PropertiedField;
import org.eviline.PropertySource;

public class PropertiedFieldParser {
	protected class FieldPropertyParser extends FieldParser {
		protected Pattern PROPERTY = Pattern.compile("(\\w+)=(.*)");

		protected FieldPropertyParser(Iterator<String> lines) {
			super(lines);
		}

		protected FieldPropertyParser(Reader r) throws IOException {
			super(r);
		}

		protected FieldPropertyParser(URL resource) throws IOException {
			super(resource);
		}
		

		@Override
		protected boolean unrecognized(String line) {
			Matcher m = PROPERTY.matcher(line);
			if(m.find()) {
				fdprops.put(m.group(1), m.group(2));
			} else
				return super.unrecognized(line);
			return true;
		}
		
		@Override
		protected Field newField() {
			return new PropertiedField();
		}
	}
	
	protected FieldPropertyParser fdparser;
	
	public PropertiedFieldParser(Iterator<String> lines) {
		fdparser = new FieldPropertyParser(lines);
	}
	
	public PropertiedFieldParser(Reader r) throws IOException {
		fdparser = new FieldPropertyParser(r);
	}
	
	public PropertiedFieldParser(URL resource) throws IOException {
		fdparser = new FieldPropertyParser(resource);
	}
	
	protected PropertySource fdprops;
	
	public PropertiedField next() {
		fdprops = new BasicPropertySource();
		PropertiedField field = (PropertiedField) fdparser.next();
		for(String key : fdprops.keys()) {
			field.put(key, fdprops.get(key));
		}
		return field;
	}
}
