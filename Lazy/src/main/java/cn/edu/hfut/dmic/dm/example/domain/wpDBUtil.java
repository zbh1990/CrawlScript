package cn.edu.hfut.dmic.dm.example.domain;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;

import cn.edu.hfut.dmic.dm.example.domain.Vodinfo;

public class wpDBUtil {
	// public static final String url =
	// "jdbc:mysql://216.189.156.242/1bqs?characterEncoding=GBK";

	public static final String url = "jdbc:mysql://23.88.160.93/fulihub?characterEncoding=GBK";
	 //public static final String password = "19656234587_xc";
	//public static final String password = "root";
	public static final String password = "zcy_zcy1993";
	//public static final String url = "jdbc:mysql://174.139.170.106/1bqs?characterEncoding=GBK";
	public static final String name = "com.mysql.jdbc.Driver";
	public static final String user = "root";
	

	private Connection conn = null;
	private static wpDBUtil uniqueInstance = null;

	private wpDBUtil() {
		try {
			Class.forName(name);// 指定连接类型
			conn = DriverManager.getConnection(url, user, password);// 获取连接
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized static wpDBUtil getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new wpDBUtil();
		}
		try {
			if (uniqueInstance.conn == null || uniqueInstance.conn.isClosed()) {
				uniqueInstance = new wpDBUtil();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return uniqueInstance;
	}

	public void exesql(String sql) {
		PreparedStatement pst = null;
		try {
			pst = uniqueInstance.conn.prepareStatement(sql);// 准备执行语句
			pst.execute(sql);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(sql);
		}
	}

	public void exesql(Vodinfo v) {
		PreparedStatement pst = null;
		boolean isexist = false;
		try {
			pst.execute(v.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void close() {
		// uniqueInstance.conn.close();
		// uniqueInstance=null;
	}

}
