package org.eviline.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eviline.Block;
import org.eviline.Field;

public class FieldParser {
	public static Pattern BLOCK = Pattern.compile("[ IJLTSZOijltszoXxGg]");
	public static Pattern ROW = Pattern.compile("\\|" + BLOCK.pattern() + "{10}\\|");
	public static Pattern TERMINATOR = Pattern.compile("\\*");
	
	public static String EMPTY_ROW = "|          |";
	public static List<String> EMPTY_FIELD = Collections.nCopies(20, EMPTY_ROW);
	
	private static Block[] createRow(String row) {
		row = "XXXXXX" + row.toUpperCase().replace("|", "") + "XXXXXX";
		Block[] ret = new Block[row.length()];
		for(int i = 0; i < ret.length; i++) {
			try {
				ret[i] = Block.valueOf("" + row.charAt(i));
			} catch(IllegalArgumentException iae) {
			}
		}
		return ret;
	}
	
	protected Iterator<String> lines;
	
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
		return new Field();
	}
	
	public Field next() {
		List<String> rows = new ArrayList<String>();
		for(String line = lines.next(); lines.hasNext(); line = lines.next()) {
			if(TERMINATOR.matcher(line).find())
				break;
			Matcher rm = ROW.matcher(line);
			if(rm.find())
				rows.add(rm.group());
			else
				unrecognized(line);
		}
		while(rows.size() > 20)
			rows.remove(0);
		Field ret = newField();
		for(int y = Field.BUFFER + Field.HEIGHT - rows.size(); y < Field.BUFFER + Field.HEIGHT; y++) {
			ret.getField()[y] = createRow(rows.remove(0));
		}
		return ret;
	}
	
	protected boolean unrecognized(String line) {
		return false;
	}
}
