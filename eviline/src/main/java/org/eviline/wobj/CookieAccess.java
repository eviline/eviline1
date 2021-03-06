package org.eviline.wobj;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.TreeMap;

import netscape.javascript.JSObject;

import org.eviline.runner.MainApplet;

public class CookieAccess {
	public static Map<String, String> get(MainApplet applet) {
		try {
			String data = "";
			JSObject myBrowser = JSObject.getWindow(applet);
			JSObject myDocument = (JSObject) myBrowser.getMember("document");

			String myCookie = (String) myDocument.getMember("cookie");

			if (myCookie.length() > 0) {
				String[] cookies = myCookie.split(";");
				for (String cookie : cookies) {
					int pos = cookie.indexOf("=");
					if (cookie.substring(0, pos).trim().equals("c")) {
						data = cookie.substring(pos + 1);
						break;
					}
				}
			}
			if("".equals(data))
				return new TreeMap<String, String>();
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			for(int i = 0; i < data.length(); i += 2) {
				bout.write(Integer.parseInt(data.substring(i, i+2), 16));
			}
			return (Map<String, String>) new ObjectInputStream(new ByteArrayInputStream(bout.toByteArray())).readObject();
		} catch(Exception ex) {
//			JOptionPane.showMessageDialog(applet, ex.toString());
			ex.printStackTrace();
			return new TreeMap<String, String>();
		}
	}

	public static void set(MainApplet applet, Map<String, String> cookie) {
		try {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bout);
			out.writeObject(cookie);
			out.close();

			StringBuilder sb = new StringBuilder();
			for(byte b : bout.toByteArray()) {
				sb.append(String.format("%02x", b));
			}
			String value = sb.toString();
			JSObject win = JSObject.getWindow(applet);
			JSObject doc = (JSObject) win.getMember("document");
			String data = "c=" + value + "; path=/; expires=Thu, 31-Dec-2019 12:00:00 GMT";
			doc.setMember("cookie", data);
		} catch(Exception ex) {
//			JOptionPane.showMessageDialog(applet, ex.toString());
			ex.printStackTrace();
		}
	}
}
