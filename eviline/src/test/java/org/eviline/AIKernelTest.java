package org.eviline;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.eviline.AIKernel.Decision;
import org.eviline.AIKernel.QueueContext;
import org.eviline.util.PropertiedFieldParser;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class AIKernelTest extends AbstractTest {
	protected static List<PropertiedField> fields = new ArrayList<PropertiedField>();
	
	@BeforeClass
	public static void beforeClass() throws Exception {
		PropertiedFieldParser pfp = new PropertiedFieldParser(AIKernelTest.class.getResource("AIKernelTest.txt"));
		while(true) {
			try {
				fields.add(pfp.next());
			} catch(NoSuchElementException nsee) {
				break;
			}
		}
	}
	
	protected static PropertiedField fieldNamed(String name) {
		for(PropertiedField pf : fields) {
			if(name.equals(pf.get("name")))
				return pf;
		}
		throw new IllegalArgumentException();
	}
	
	protected static ShapeType[] sequence(PropertiedField pf) {
		char[] chars = pf.get("sequence").toCharArray();
		ShapeType[] ret = new ShapeType[chars.length];
		for(int i = 0; i < chars.length; i++)
			ret[i] = ShapeType.valueOf("" + chars[i]);
		return ret;
	}
	
	@Test
	public void bestForQueueClearsPossibleLine() {
		ShapeType[] queue = new ShapeType[] { ShapeType.I, ShapeType.L, ShapeType.J };
		Field empty = new Field();
		Decision best = AIKernel.getInstance().bestFor(new QueueContext(empty, queue));
		Decision result = best.deepest();
		Assert.assertEquals(1, result.field.lines - empty.lines);
	}
	
	@Test
	public void quadruple() throws Exception {
		PropertiedField pf = fieldNamed("quadruple");
		QueueContext qc = new QueueContext(pf, sequence(pf));
		Decision best = AIKernel.getInstance().bestFor(qc);
		PropertiedField deepest = pf.copy();
		best.deepest().field.copyInto(deepest);
		log.trace(deepest);
		Assert.assertTrue((Boolean) deepest.evalRuby(pf.get("assert")));
	}
}
