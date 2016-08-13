package cn.edu.hfut.dmic.dm.example;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class DBUtil {  
    public static final String url = "jdbc:mysql://127.0.0.1/1bqs?characterEncoding=GBK";  
    public static final String name = "com.mysql.jdbc.Driver";  
    public static final String user = "root";  
    public static final String password = "root";  
  
    private  Connection conn = null;  
    private static DBUtil uniqueInstance = null;
  
    private  DBUtil() {  
        try {  
            Class.forName(name);//指定连接类型  
            conn = DriverManager.getConnection(url, user, password);//获取连接  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
    public synchronized   static DBUtil getInstance() {
        if (uniqueInstance == null) {
            uniqueInstance = new DBUtil();
        }
        try {
			if(uniqueInstance.conn==null||uniqueInstance.conn.isClosed()){
				uniqueInstance = new DBUtil();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
        return uniqueInstance;
     }
  
    public  void exesql(String sql) {  
    	  PreparedStatement pst = null;  
        try {  
            pst = uniqueInstance.conn.prepareStatement(sql);//准备执行语句  
            pst.execute(sql);
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
    public void close() {  
        	//uniqueInstance.conn.close();  
            //uniqueInstance=null;
    }  

}
