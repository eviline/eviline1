package org.eviline.swing;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

/**
 * {@link Document} that permits only numerals
 * @author robin
 *
 */
public class IntegerDocument extends PlainDocument {
	private static final long serialVersionUID = 0;
	@Override
	public void insertString(int offs, String str, AttributeSet a)
			throws BadLocationException {
		if(!str.matches("[0-9]+"))
			return;
		super.insertString(offs, str, a);
	}

}
