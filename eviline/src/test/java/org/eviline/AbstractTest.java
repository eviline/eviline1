package org.eviline;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;

public class AbstractTest {
	static {
		PropertyConfigurator.configure(AbstractTest.class.getResource("log4j.properties"));
	}
	
	protected Logger log = Logger.getLogger(getClass());
	
	@Before
	public void before() throws Exception {
		
	}
}
