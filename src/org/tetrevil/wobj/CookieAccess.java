package org.tetrevil.wobj;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import javax.swing.JOptionPane;

import netscape.javascript.JSObject;

import org.tetrevil.runner.MainApplet;

public class CookieAccess {
	public static String get(MainApplet applet, String name, String defaultValue) {
		String data = "";
		JSObject myBrowser = JSObject.getWindow(applet);
		JSObject myDocument = (JSObject) myBrowser.getMember("document");

		String myCookie = (String) myDocument.getMember("cookie");
		if (myCookie.length() > 0) {
		    String[] cookies = myCookie.split(";");
		    for (String cookie : cookies) {
		        int pos = cookie.indexOf("=");
		        if (cookie.substring(0, pos).trim().equals(name)) {
		                data = cookie.substring(pos + 1);
		                break;
		        }
		    }
		}
		if("".equals(data))
			return defaultValue;
		try {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			for(int i = 0; i < data.length(); i += 2) {
				bout.write(Byte.parseByte(data.substring(i, i+2), 16));
			}
			return new String(bout.toByteArray());
		} catch(Exception ex) {
			return defaultValue;
		}
	}
	
	public static void set(MainApplet applet, String name, String value) {
		StringBuilder sb = new StringBuilder();
		for(byte b : value.getBytes()) {
			sb.append(String.format("%02x", b));
		}
		value = sb.toString();
		JSObject win = JSObject.getWindow(applet);
		JSObject doc = (JSObject) win.getMember("document");
		String data = name + "=" + value + "; path=/; expires=Thu, 31-Dec-2019 12:00:00 GMT";
		doc.setMember("cookie", data);
	}
}
