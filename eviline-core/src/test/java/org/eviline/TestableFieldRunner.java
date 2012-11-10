package org.eviline;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.eviline.util.TestableFieldParser;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;

public class TestableFieldRunner extends ParentRunner<TestableField> {

	@Retention(RetentionPolicy.RUNTIME)
	public @interface FieldResource {
		public String value();
	}
	
	protected Class<?> testClass;
	protected List<TestableField> children = new ArrayList<TestableField>();
	
	public TestableFieldRunner(Class<?> testClass)
			throws InitializationError {
		super(testClass);
		this.testClass = testClass;
		String resource = testClass.getAnnotation(FieldResource.class).value();
		try {
			TestableFieldParser tfp = new TestableFieldParser(testClass.getResource(resource));
			while(true) {
				try {
					children.add(tfp.next());
				} catch(NoSuchElementException nsee) {
					break;
				}
			}
		} catch(Exception ex) {
			throw new InitializationError(ex);
		}
	}

	@Override
	protected List<TestableField> getChildren() {
		return children;
	}

	@Override
	protected Description describeChild(TestableField child) {
		return Description.createTestDescription(testClass, child.get("name"));
	}

	@Override
	protected void runChild(TestableField child, RunNotifier notifier) {
		Description d = describeChild(child);
		notifier.fireTestStarted(d);
		try {
			child.test();
		} catch(Throwable t) {
			notifier.fireTestFailure(new Failure(d, t));
		} finally {
			notifier.fireTestFinished(d);
		}
	}

}
