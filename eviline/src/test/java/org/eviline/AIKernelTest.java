package org.eviline;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.eviline.AIKernel.Decision;
import org.eviline.AIKernel.QueueContext;
import org.eviline.util.TestableFieldParser;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class AIKernelTest extends AbstractTest {
	protected static List<TestableField> fields = new ArrayList<TestableField>();
	
	@BeforeClass
	public static void beforeClass() throws Exception {
		TestableFieldParser pfp = new TestableFieldParser(AIKernelTest.class.getResource("AIKernelTest.txt"));
		while(true) {
			try {
				fields.add(pfp.next());
			} catch(NoSuchElementException nsee) {
				break;
			}
		}
	}
	
	protected static TestableField fieldNamed(String name) {
		for(TestableField pf : fields) {
			if(name.equals(pf.get("name")))
				return pf;
		}
		throw new IllegalArgumentException();
	}
	
	protected static ShapeType[] sequence(TestableField pf) {
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
		TestableField pf = fieldNamed("quadruple");
		QueueContext qc = new QueueContext(pf, sequence(pf));
		Decision best = AIKernel.getInstance().bestFor(qc);
		TestableField df = pf.copy();
		best.deepest().field.copyInto(df);
		Assert.assertTrue((Boolean) df.evalRuby(df.get("assert")));
	}
}
