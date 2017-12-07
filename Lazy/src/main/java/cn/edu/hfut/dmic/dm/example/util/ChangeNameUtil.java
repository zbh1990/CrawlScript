package cn.edu.hfut.dmic.dm.example.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChangeNameUtil {
	// public static final String url =
	// "jdbc:mysql://216.189.156.242/1bqs?characterEncoding=GBK";

	public static final String url = "jdbc:mysql://216.189.156.242/1bqs?characterEncoding=GBK";
	public static final String password = "19656234587_xc";
	//public static final String password = "zhizhuys";
	//public static final String password = "zcy1993";
	//public static final String url = "jdbc:mysql://174.139.170.106/1bqs?characterEncoding=GBK";
	public static final String name = "com.mysql.jdbc.Driver";
	public static final String user = "root";
	

	private Connection conn = null;
	private static ChangeNameUtil uniqueInstance = null;

	private ChangeNameUtil() {
		try {
			Class.forName(name);// 指定连接类型
			conn = DriverManager.getConnection(url, user, password);// 获取连接
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized static ChangeNameUtil getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new ChangeNameUtil();
		}
		try {
			if (uniqueInstance.conn == null || uniqueInstance.conn.isClosed()) {
				uniqueInstance = new ChangeNameUtil();
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
		}
	}

	public void exesql() {
		PreparedStatement pst = null;
		List<String> sql =new ArrayList<String>();
		try {
			String selectsql = "SELECT d_name FROM `mac_vod` where d_enname='niuyueheibang'";
			pst = uniqueInstance.conn.prepareStatement(selectsql);// 准备执行语句
			ResultSet result = pst.executeQuery(selectsql);
			if (result != null) {
				int i =1;
				while (result.next()) {
					String d_name = result.getString("d_name");
					System.out.println(d_name+":"+ChineseToEnglish.getPingYin(d_name)+":"+i);
					String a = " UPDATE  mac_vod set d_enname='"+ChineseToEnglish.getPingYin(d_name)+"'"+"where d_name='"+d_name+"'";
					i++;
					sql.add(a);
				}
			}
			for(String b :sql){
				pst.execute(b);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void close() {
		// uniqueInstance.conn.close();
		// uniqueInstance=null;
	}
	public static void main(String[] args) {
		ChangeNameUtil.getInstance().exesql();
	}

}
