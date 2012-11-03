package org.eviline;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class Version {
	public static String getVersion() {
		URL url = Version.class.getResource("version.properties");
		try {
			Properties p = new Properties();
			InputStream in = url.openStream();
			try {
				p.load(in);
			} finally {
				in.close();
			}
			return p.getProperty("project.version");
		} catch(IOException ioe) {
			return "0.0.0-ERROR";
		}
	}
}
