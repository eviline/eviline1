package org.eviline;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class Version {
	public static String getVersion() {
		URL url = Version.class.getClassLoader().getResource("META-INF/maven/org.eviline/eviline/pom.properties");
		if(url == null)
			return "0.0.0-DEVEL";
		try {
			Properties p = new Properties();
			InputStream in = url.openStream();
			try {
				p.load(in);
			} finally {
				in.close();
			}
			return p.getProperty("version");
		} catch(IOException ioe) {
			return "0.0.0-ERROR";
		}
	}
}
