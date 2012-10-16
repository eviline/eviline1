package org.eviline;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class AbstractTest {
	static {
		PropertyConfigurator.configure(AbstractTest.class.getResource("log4j.properties"));
	}
	
	protected Logger log = Logger.getLogger(getClass());
}
