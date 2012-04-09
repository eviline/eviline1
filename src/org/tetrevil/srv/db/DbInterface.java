package org.tetrevil.srv.db;

import java.util.Properties;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class DbInterface {
	private static SqlSessionFactory factory;
	public static SqlSessionFactory getFactory() {
		if(factory == null) {
			try {
				Properties p = new Properties();
				p.load(DbInterface.class.getResourceAsStream("mybatis.properties"));
				factory = new SqlSessionFactoryBuilder().build(DbInterface.class.getResourceAsStream("mybatis.xml"), p);
			} catch(Exception ex) {
				throw new RuntimeException(ex);
			}
		}
		return factory;
	}
}
