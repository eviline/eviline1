package org.eviline;

import org.eviline.ai.AI;
import org.eviline.ai.AIKernel;
import org.eviline.ai.QueueContext;
import org.jruby.Ruby;
import org.jruby.embed.ScriptingContainer;
import org.jruby.javasupport.JavaUtil;
import org.jruby.parser.EvalStaticScope;
import org.jruby.runtime.DynamicScope;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.runtime.scope.ManyVarsDynamicScope;
import org.junit.Assert;

public class TestableField extends PropertiedField {
	private static final long serialVersionUID = 0;
	
	public TestableField() {}
	
	public TestableField(Field f) {
		f.copyInto(this);
	}
	
	@Override
	public TestableField newInstance() {
		return new TestableField();
	}
	
	@Override
	public TestableField copy() {
		return (TestableField) super.copy();
	}
	
	public Object evalRuby(String s) {
		return evalRuby(this, s);
	}
	
	public Object evalRuby(Object self, String s) {
		ScriptingContainer cnt = new ScriptingContainer();
		Ruby ruby = cnt.getProvider().getRuntime();
		ThreadContext context = ruby.getCurrentContext();
		context.getCurrentFrame().setSelf(JavaUtil.convertJavaToRuby(ruby, self));
        DynamicScope currentScope = context.getCurrentScope();
        ManyVarsDynamicScope newScope = new ManyVarsDynamicScope(new EvalStaticScope(currentScope.getStaticScope()), currentScope);

        IRubyObject jret = ruby.evalScriptlet(s, newScope);
        return JavaUtil.convertRubyToJava(jret);
	}
	
	public void assertTrue(boolean condition) {
		Assert.assertTrue(condition);
	}
	
	public void assertTrue(String message, boolean condition) {
		Assert.assertTrue(message, condition);
	}
	
	public void test() {
		String c;
		Object v;
		
		if((c = get("precondition")) != null)
			if((v = precondition()) == null || Boolean.FALSE.equals(v))
				Assert.fail("Failed precondition:" + c);
		
		v = body();
		
		Object postContext = v;
		
		if((c = get("postcondition")) != null)
			if((v = postcondition(v)) == null || Boolean.FALSE.equals(v)) {
				System.out.println("For context object, " + postContext + ", assertion failed!");
				Assert.fail("Failed postcondition:" + c);
			}
	}
	
	public Object precondition() {
		String cond = get("precondition");
		Object val = evalRuby(this, cond);
		return val;
	}
	
	public Object body() {
		String b = get("body");
		if(b == null)
			return this;
		return evalRuby(this, b);
	}
	
	public Object postcondition(Object bodyValue) {
		String cond = get("postcondition");
		Object val = evalRuby(bodyValue, cond);
		return val;
	}
	
	public QueueContext sequence() {
		char[] chars = get("sequence").toCharArray();
		ShapeType[] ret = new ShapeType[chars.length];
		for(int i = 0; i < chars.length; i++)
			ret[i] = ShapeType.valueOf("" + chars[i]);
		return new QueueContext(AI.getInstance(), this, ret);
	}
	
	public AIKernel getAi() {
		return AI.getInstance();
	}
}
