package org.eviline.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eviline.BlockType;
import org.eviline.Field;

public class FieldParser {
	public static interface LineHandler {
		public boolean handleLine(String line);
	}
	public static interface FieldFactory {
		public Field newField();
	}
	
	public static Pattern BLOCK = Pattern.compile("[ IJLTSZOijltszoXxGg]");
	public static Pattern ROW = Pattern.compile("\\|" + BLOCK.pattern() + "{10}\\|");
	public static Pattern TERMINATOR = Pattern.compile("\\*\\*\\*\\*");
	public static Pattern CONTINUED = Pattern.compile("(.*)\\\\$");
	public static Pattern MULTILINE_BEGIN = Pattern.compile("<<<<");
	public static Pattern MULTILINE_END = Pattern.compile(">>>>");
	public static Pattern BLANK = Pattern.compile("^[\\s\\n]*$");
	
	public static String EMPTY_ROW = "|          |";
	public static List<String> EMPTY_FIELD = Collections.nCopies(20, EMPTY_ROW);
	
	private static BlockType[] createRow(String row) {
		row = "XXXXXX" + row.toUpperCase().replace("|", "") + "XXXXXX";
		BlockType[] ret = new BlockType[row.length()];
		for(int i = 0; i < ret.length; i++) {
			try {
				ret[i] = BlockType.valueOf("" + row.charAt(i));
			} catch(IllegalArgumentException iae) {
			}
		}
		return ret;
	}
	
	public Iterator<String> lines;
	
	public LineHandler lineHandler;
	public FieldFactory fieldFactory;
	
	public FieldParser(Iterator<String> lines) {
		this.lines = lines;
	}
	
	public FieldParser(Reader r) throws IOException {
		List<String> lines = new ArrayList<String>();
		BufferedReader br = new BufferedReader(r);
		for(String line = br.readLine(); line != null; line = br.readLine()) {
			lines.add(line);
		}
		this.lines = lines.iterator();
	}
	
	public FieldParser(URL resource) throws IOException {
		this(new InputStreamReader(resource.openStream()));
	}
	
	protected Field newField() {
		if(fieldFactory != null)
			return fieldFactory.newField();
		return new Field();
	}
	
	public Field next() throws ParseException {
		boolean blank = true;
		List<String> rows = new ArrayList<String>();
		for(String line = lines.next(); lines.hasNext(); line = lines.next()) {
			Matcher m;
			while((m = CONTINUED.matcher(line)).matches())
				line = line.substring(0, line.length() - 1) + lines.next();
			if((m = MULTILINE_BEGIN.matcher(line)).find()) {
				line = line.substring(0, m.start()) + line.substring(m.end());
				while(!(m = MULTILINE_END.matcher(line)).find())
					line = line + "\n" + lines.next();
				line = line.substring(0, m.start()) + line.substring(m.end());
			}
			if(!(m = BLANK.matcher(line)).matches())
				blank = false;
			if(lineHandler != null && lineHandler.handleLine(line))
				;
			else if(TERMINATOR.matcher(line).matches())
				break;
			else if((m = ROW.matcher(line)).matches())
				rows.add(m.group());
			else if((m = BLANK.matcher(line)).matches())
				;
			else 
				throw new ParseException(line, 0);
		}
		if(blank)
			throw new NoSuchElementException();
		while(rows.size() > 20)
			rows.remove(0);
		Field ret = newField();
		for(int y = Field.BUFFER + Field.HEIGHT - rows.size(); y < Field.BUFFER + Field.HEIGHT; y++) {
			ret.getField()[y] = createRow(rows.remove(0));
		}
		return ret;
	}
}
