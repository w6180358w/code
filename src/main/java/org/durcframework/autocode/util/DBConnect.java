package org.durcframework.autocode.util;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.durcframework.autocode.entity.DataSourceConfig;

public class DBConnect {
	public static Connection getConnection(DataSourceConfig config) throws ClassNotFoundException, SQLException
			 {
		//清空其他的驱动
		String[] drivers = new String[]{"jdbc:mysql:","jdbc:jtds:sqlserver:","jdbc:oracle:thin:"};
		Map<String,Driver> driMap = new HashMap<>();
		for (String dri : drivers) {
			try {
				Driver d = DriverManager.getDriver(dri);
				DriverManager.deregisterDriver(d);
				driMap.put(dri, d);
				System.out.println("注销"+dri);
			} catch (Exception e) {
			}
		}
		
		for (String dri : drivers) {
			if(config.getJdbcUrl().indexOf(dri)>-1) {
				DriverManager.registerDriver(driMap.get(dri));
				System.out.println("注册"+dri);
				break;
			}
		}
		
		
		Enumeration<Driver> dris = DriverManager.getDrivers();
		while(dris.hasMoreElements()) {
			System.out.println(dris.nextElement().getClass().getName());
		}
		return DriverManager.getConnection(config.getJdbcUrl(),
				config.getUsername(), config.getPassword());
	}
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		DBConnect.getConnection(null);
	}
	/**
	 * 测试连接,返回错误信息,无返回内容说明连接成功
	 * 
	 * @param config
	 * @return 返回错误信息,无返回内容说明连接成功
	 */
	public static String testConnection(DataSourceConfig dataSourceConfig) {
		Connection con = null;
		String ret = null;
		try {
			con = DBConnect.getConnection(dataSourceConfig);
			// 不为空说明连接成功
			if (con == null) {
				ret = dataSourceConfig.getDbName() + "连接失败";
			}
		} catch (ClassNotFoundException e) {
			ret = dataSourceConfig.getDbName() + "连接失败" + "<br>错误信息:"
					+ "找不到驱动" + dataSourceConfig.getDriverClass();
		} catch (SQLException e) {
			ret = dataSourceConfig.getDbName() + "连接失败" + "<br>错误信息:"
					+ e.getMessage();
			e.printStackTrace();
		} finally {
			if (con != null) {
				try {
					con.close(); // 关闭连接,该连接无实际用处
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return ret;
	}
}
