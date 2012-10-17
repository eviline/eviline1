package org.eviline;

import org.jruby.Ruby;
import org.jruby.embed.ScriptingContainer;
import org.jruby.javasupport.JavaUtil;
import org.jruby.parser.EvalStaticScope;
import org.jruby.runtime.DynamicScope;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.runtime.scope.ManyVarsDynamicScope;

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
		ScriptingContainer cnt = new ScriptingContainer();
		Ruby ruby = cnt.getProvider().getRuntime();
		ThreadContext context = ruby.getCurrentContext();
		context.getCurrentFrame().setSelf(JavaUtil.convertJavaToRuby(ruby, this));
        DynamicScope currentScope = context.getCurrentScope();
        ManyVarsDynamicScope newScope = new ManyVarsDynamicScope(new EvalStaticScope(currentScope.getStaticScope()), currentScope);

        IRubyObject jret = ruby.evalScriptlet(s, newScope);
        return JavaUtil.convertRubyToJava(jret);
	}
	

}
