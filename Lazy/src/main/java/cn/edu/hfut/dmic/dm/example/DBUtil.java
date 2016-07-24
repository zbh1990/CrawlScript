package cn.edu.hfut.dmic.dm.example;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class DBUtil {  
    public static final String url = "jdbc:mysql://114.55.10.138/1bqs?characterEncoding=GBK";  
    public static final String name = "com.mysql.jdbc.Driver";  
    public static final String user = "root";  
    public static final String password = "zcy1993";  
  
    public static Connection conn = null;  
   
  
    public  DBUtil() {  
        try {  
            Class.forName(name);//指定连接类型  
            conn = DriverManager.getConnection(url, user, password);//获取连接  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
  
    public static void exesql(String sql) {  
    	  PreparedStatement pst = null;  
        try {  
            pst = conn.prepareStatement(sql);//准备执行语句  
            pst.execute(sql);
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
    public void close() {  
        try {  
            this.conn.close();  
        } catch (SQLException e) {  
            e.printStackTrace();  
        }  
    }  

}
