package cn.edu.hfut.dmic.dm.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;

public class DBUtil {
	// public static final String url =
	// "jdbc:mysql://216.189.156.242/1bqs?characterEncoding=GBK";

	public static final String url = "jdbc:mysql://127.0.0.1/1bqs?characterEncoding=GBK";
	public static final String name = "com.mysql.jdbc.Driver";
	public static final String user = "root";
	public static final String password = "root";

	private Connection conn = null;
	private static DBUtil uniqueInstance = null;

	private DBUtil() {
		try {
			Class.forName(name);// 指定连接类型
			conn = DriverManager.getConnection(url, user, password);// 获取连接
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized static DBUtil getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new DBUtil();
		}
		try {
			if (uniqueInstance.conn == null || uniqueInstance.conn.isClosed()) {
				uniqueInstance = new DBUtil();
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

	public void exesql(Vodinfo v) {
		PreparedStatement pst = null;
		try {
			String selectsql = "select * from mac_vod where d_name = \'" + v.getTitle() + "\'";
			pst = uniqueInstance.conn.prepareStatement(selectsql);// 准备执行语句
			ResultSet result = pst.executeQuery(selectsql);
			if (result != null) {
				String d_playurl = null;
				String d_playfrom = null;
				while (result.next()) {
					d_playurl = result.getString("d_playurl");
					d_playfrom = result.getString("d_playfrom");
				}
				if(v.getUrl().equals(d_playurl)){
					return;
				}
				if (StringUtils.isNotBlank(d_playfrom) && d_playfrom.indexOf("$$$") > -1) {
					String[] d_playurls = d_playurl.split("\\$\\$\\$");
					String[] d_playfroms = d_playfrom.split("\\$\\$\\$");
					for (int i = 0; i < d_playfroms.length; i++) {
						if (d_playfroms[i].equals(v.getPlayer())) {// 判断是否同一个平台，youku，tudou
							if (v.getUrl().equals(d_playurls[i])) {// 判断是否同一个url,url有无变化
								return;
							} else {
								d_playurls[i] = v.getUrl();
								break;
							}
						}
					}
					String newplayer = "";
					String newurl = "";
					for (int i = 0; i < d_playfroms.length; i++) {// 组织新的playform和url
						newplayer = newplayer + d_playfroms[i] + "$$$";
						newurl = newurl + d_playurls[i] + "$$$";
					}
					v.setUrl(newurl);
					v.setPlayer(newplayer);
				}
				if (StringUtils.isNotBlank(d_playfrom) && d_playfrom.indexOf("$$$") < 0&&!d_playfrom.equals(v.getPlayer())) {
					String newplayer = "";
					String newurl = "";
					newplayer = d_playfrom + "$$$" + v.getPlayer();
					newurl = d_playurl + "$$$" + v.getUrl();
					v.setUrl(newurl);
					v.setPlayer(newplayer);
				}
			}
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
