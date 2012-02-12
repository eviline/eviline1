package org.tetrevil.swing;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class IntegerDocument extends PlainDocument {
	@Override
	public void insertString(int offs, String str, AttributeSet a)
			throws BadLocationException {
		if(!str.matches("[0-9]+"))
			return;
		super.insertString(offs, str, a);
	}

}
