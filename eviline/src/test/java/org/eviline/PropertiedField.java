package org.eviline;

import java.util.Set;

import org.jruby.Ruby;
import org.jruby.RubyObject;
import org.jruby.embed.ScriptingContainer;
import org.jruby.javasupport.JavaObject;
import org.jruby.javasupport.JavaUtil;
import org.jruby.parser.EvalStaticScope;
import org.jruby.runtime.DynamicScope;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.runtime.scope.ManyVarsDynamicScope;

public class PropertiedField extends Field implements PropertySource {
	private static final long serialVersionUID = 0;
	
	protected PropertySource props = new BasicPropertySource();

	public Object evalRuby(String s) {
		ScriptingContainer cnt = new ScriptingContainer();
		Ruby ruby = cnt.getProvider().getRuntime();
		ThreadContext context = ruby.getCurrentContext();
		context.getCurrentFrame().setSelf(JavaUtil.convertJavaToRuby(ruby, this));
        DynamicScope currentScope = context.getCurrentScope();
        ManyVarsDynamicScope newScope = new ManyVarsDynamicScope(new EvalStaticScope(currentScope.getStaticScope()), currentScope);

        return ruby.evalScriptlet(s, newScope);
	}
	
	@Override
	public PropertiedField copy() {
		PropertiedField c = (PropertiedField) copyInto(new PropertiedField());
		for(String key : keys())
			c.put(key, get(key));
		return c;
	}
	
	@Override
	public boolean containsKey(String key) {
		return props.containsKey(key);
	}

	@Override
	public String get(String key) {
		return props.get(key);
	}

	@Override
	public String put(String key, String value) {
		return props.put(key, value);
	}

	@Override
	public Set<String> keys() {
		return props.keys();
	}

	public Boolean getBoolean(String key) {
		return get(key) == null ? null : Boolean.parseBoolean(get(key));
	}
	
	public Boolean putBoolean(String key, Boolean value) {
		Boolean ret = getBoolean(key);
		put(key, value == null ? null : value.toString());
		return ret;
	}
	
	public Integer getInt(String key) {
		return get(key) == null ? null : Integer.parseInt(get(key));
	}
	
	public Integer putInt(String key, Integer value) {
		Integer ret = getInt(key);
		put(key, value == null ? null : value.toString());
		return ret;
	}
	
	public Double getDouble(String key) {
		return get(key) == null ? null : Double.parseDouble(get(key));
	}
	
	public Double putDouble(String key, Double value) {
		Double ret = getDouble(key);
		put(key, value == null ? null : value.toString());
		return ret;
	}
}
