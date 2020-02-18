package org.durcframework.autocode.generator;

import java.util.HashMap;
import java.util.Map;


public class DataBaseConfig {
	
	private static Map<String, String> jdbcUrlMap = new HashMap<String,String>();
	static {
		jdbcUrlMap.put("com.mysql.jdbc.Driver", "jdbc:mysql://%s:%s/%s?useUnicode=true&characterEncoding=UTF-8");
		jdbcUrlMap.put("com.mysql.cj.jdbc.Driver", "jdbc:mysql://%s:%s/%s?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=UTC");
		jdbcUrlMap.put("net.sourceforge.jtds.jdbc.Driver", "jdbc:jtds:sqlserver://%s:%s;databaseName=%s");
		jdbcUrlMap.put("oracle.jdbc.xa.client.OracleXADataSource", "jdbc:oracle:thin:@%s:%s:%s");
	}
	
	private String dbName;
	private String name;
	private String driverClass;
	private String ip;
	private int port;
	private String username;
	private String password;
	private String ignore;
	private String fix;
	
	public String getJdbcUrl() {
		String url = jdbcUrlMap.get(driverClass);
		return String.format(url, ip,port,dbName);
	}
	
	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getDriverClass() {
		return driverClass;
	}

	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}
	
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getIgnore() {
		return ignore;
	}

	public void setIgnore(String ignore) {
		this.ignore = ignore;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFix() {
		return fix;
	}

	public void setFix(String fix) {
		this.fix = fix;
	}

}
