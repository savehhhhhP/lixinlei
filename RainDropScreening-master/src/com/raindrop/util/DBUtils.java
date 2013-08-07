package com.raindrop.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DBUtils {

	
	public static void getConnection() throws Exception{
		String driverName="";
//		不是BAE项目  不能使用这些API
//		String host = BaeEaeEnv.getBaeHeader(BaeEnv.BAE_ENV_SK);
		String host="";
		String port="";
		String username="";
		String password="";
		String dbUrl = "jdbc:mysql://";
		String serverName = host + ":" + port + "/";
		String databaseName = "yourDataBaseName";
		String connName = dbUrl + serverName + databaseName;
		String sql = "select * from test";
		
		Connection connection = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			Class.forName(driverName);
			connection = DriverManager.getConnection(connName, username,
					password);
			stmt = connection.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
//				id = rs.getString("id");
//				name = rs.getString("name");
			}
		}finally{
			if (connection != null) {
				connection.close();
			}		
		}
	}
	
	
	
}
