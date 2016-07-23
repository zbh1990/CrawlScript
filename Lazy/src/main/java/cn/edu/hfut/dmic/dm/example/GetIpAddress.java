package cn.edu.hfut.dmic.dm.example;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GetIpAddress {
    private static Logger logger = LoggerFactory.getLogger(GetIpAddress.class);


    public static String getInfo(String srcUrl, int timeoutMils) throws IOException {

        URL url = new URL(srcUrl);
        HttpURLConnection httpconn = (HttpURLConnection) url.openConnection();
        httpconn.setReadTimeout(timeoutMils);

        InputStreamReader inputReader = new InputStreamReader(httpconn.getInputStream());
        BufferedReader bufReader = new BufferedReader(inputReader);

        String tmpLine = "";
        StringBuffer contentBuffer = new StringBuffer();

        while ((tmpLine = bufReader.readLine()) != null) {
            contentBuffer.append(tmpLine);
        }

        bufReader.close();
        httpconn.disconnect();
        return contentBuffer.toString();
    }
    
    public static String getInfo(String srcUrl, int timeoutMils, String Para, String headerStr) throws IOException {
        URL url = new URL(srcUrl);
        HttpURLConnection httpconn = (HttpURLConnection) url.openConnection();
        if(!StringUtils.isBlank(headerStr) && !StringUtils.isBlank(Para)){
            httpconn.addRequestProperty(Para, headerStr);
        }
        httpconn.setReadTimeout(timeoutMils);

        InputStreamReader inputReader = new InputStreamReader(httpconn.getInputStream());
        BufferedReader bufReader = new BufferedReader(inputReader);

        String tmpLine = "";
        StringBuffer contentBuffer = new StringBuffer();

        while ((tmpLine = bufReader.readLine()) != null) {
            contentBuffer.append(tmpLine);
        }

        bufReader.close();
        httpconn.disconnect();
        return contentBuffer.toString();
    }

    public static BufferedReader getReader(String srcUrl, int timeoutMils) throws IOException {
        URL url = new URL(srcUrl);
        HttpURLConnection httpconn = (HttpURLConnection) url.openConnection();
        httpconn.setReadTimeout(timeoutMils);

        InputStreamReader inputReader = new InputStreamReader(httpconn.getInputStream());
        BufferedReader bufReader = new BufferedReader(inputReader);
        return bufReader;
    }

 // POST
    public static String getInfoByPost(String urlStr, String xml, int timeoutMils) {
        DataInputStream input = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] xmlData = xml.getBytes();
        try {
//        	Properties props = System.getProperties();  
//            props.setProperty("proxySet", "true");  
//            props.setProperty("http.proxyHost", "114.253.250.56");  
//            props.setProperty("http.proxyPort","8888");
            URL url = new URL(urlStr);
            URLConnection urlCon = url.openConnection();
            urlCon.setDoOutput(true);
            urlCon.setDoInput(true);
            urlCon.setUseCaches(false);
            urlCon.setConnectTimeout(timeoutMils);
            urlCon.setReadTimeout(timeoutMils);

            urlCon.setRequestProperty("Content-Type", "text/xml");  
            urlCon.setRequestProperty("Content-length", String.valueOf(xmlData.length));
            DataOutputStream printout = new DataOutputStream(urlCon.getOutputStream());
            printout.write(xmlData);
            printout.flush();
            printout.close();
            input = new DataInputStream(urlCon.getInputStream());
            byte[] bufferByte = new byte[256];

            int i = -1;
            while ((i = input.read(bufferByte)) > -1) {
                out.write(bufferByte, 0, i);
                out.flush();
            }
        } catch (Exception e) {
            logger.error("连接错误：", e);
        }
        return out.toString();
    }
    
    // POST
    public static String getInfoByPost2(String urlStr, String xml, int timeoutMils, boolean isXml) {
        DataInputStream input = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] xmlData = xml.getBytes();
        try {
            URL url = new URL(urlStr);
            URLConnection urlCon = url.openConnection();
            urlCon.setDoOutput(true);
            urlCon.setDoInput(true);
            urlCon.setUseCaches(false);
            urlCon.setConnectTimeout(timeoutMils);
            urlCon.setReadTimeout(timeoutMils);

            if(isXml){
                urlCon.setRequestProperty("Content-Type", "text/xml");    	
            }
            urlCon.setRequestProperty("Content-length", String.valueOf(xmlData.length));
            DataOutputStream printout = new DataOutputStream(urlCon.getOutputStream());
            printout.write(xmlData);
            printout.flush();
            printout.close();
            input = new DataInputStream(urlCon.getInputStream());
            byte[] bufferByte = new byte[256];

            int i = -1;
            while ((i = input.read(bufferByte)) > -1) {
                out.write(bufferByte, 0, i);
                out.flush();
            }
        } catch (Exception e) {
            logger.error("连接错误：", e);
        }
        return out.toString();
    }
    
    public static String getInfoByJsonPost(String urlStr, String jsonString, int timeoutMils) {
        DataInputStream input = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] jsonData = jsonString.getBytes();
        try {
            URL url = new URL(urlStr);
            HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
            urlCon.setDoOutput(true);
            urlCon.setDoInput(true);
            urlCon.setRequestMethod("POST");
            
            urlCon.setUseCaches(false);
            urlCon.setConnectTimeout(timeoutMils);
            urlCon.setReadTimeout(timeoutMils);
            urlCon.setRequestProperty("Content-Type", "application/json");
            urlCon.setRequestProperty("Content-length", String.valueOf(jsonData.length));
            DataOutputStream printout = new DataOutputStream(urlCon.getOutputStream());
            printout.write(jsonData);
            printout.flush();
            printout.close();
            input = new DataInputStream(urlCon.getInputStream());
            byte[] bufferByte = new byte[256];

            int i = -1;
            while ((i = input.read(bufferByte)) > -1) {
                out.write(bufferByte, 0, i);
                out.flush();
            }
        } catch (Exception e) {
            logger.error("连接错误：", e);
        }
        return out.toString();
    }
    
    public static String getInfoByJsonPost(String urlStr, Map<String,Object> paramMap, int timeoutMils) {
        DataInputStream input = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        StringBuffer params = new StringBuffer();
        for (Iterator<Entry<String, Object>> iter = paramMap.entrySet().iterator(); iter.hasNext();){
        	Entry<String, Object> element = iter.next();
            params.append(element.getKey().toString());
            params.append("=");
            params.append(element.getValue().toString());
            params.append("&");
        }

        if (params.length() > 0){
            params = params.deleteCharAt(params.length() - 1);
        }
        
        try {
            URL url = new URL(urlStr);
            HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
            urlCon.setDoOutput(true);
            urlCon.setDoInput(true);
            urlCon.setRequestMethod("POST");
            
            urlCon.setUseCaches(false);
            urlCon.setConnectTimeout(timeoutMils);
            urlCon.setReadTimeout(timeoutMils);
            urlCon.setRequestProperty("Content-Type", "application/Json");
            urlCon.setRequestProperty("Content-length", String.valueOf(params.length()));
            DataOutputStream printout = new DataOutputStream(urlCon.getOutputStream());
            printout.write(params.toString().getBytes());
            printout.flush();
            printout.close();
            input = new DataInputStream(urlCon.getInputStream());
            byte[] bufferByte = new byte[256];
            int i = -1;
            while ((i = input.read(bufferByte)) > -1) {
                out.write(bufferByte, 0, i);
                out.flush();
            }
        } catch (Exception e) {
            logger.error("连接错误：", e);
        }
        return out.toString();
    }
    
    public static Map<String,String> getInfoByMapAndPost(String urlStr, Map<String,String> paramMap, int timeoutMils) {
    	Map<String,String> resultMap=new HashMap<String,String>();
    	List<NameValuePair> paramList = new ArrayList <NameValuePair>();  
    	for(Entry<String, String> param:paramMap.entrySet()){
    		paramList.add(new BasicNameValuePair(param.getKey(),param.getValue()));
    	}
    	
    	HttpPost post=new HttpPost(urlStr);
    	try{
    		UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(paramList, "UTF-8");
            urlEncodedFormEntity.setContentType("application/x-www-form-urlencoded; charset=UTF-8");
            
            post.setEntity(urlEncodedFormEntity);
     	    HttpResponse resultResponse = new DefaultHttpClient().execute(post);
     	    if(resultResponse.getStatusLine()==null){
     	    	resultMap.put("code", "-101");
     	    	return resultMap;
     	    }
     	    resultMap.put("code", resultResponse.getStatusLine().getStatusCode()+"");
     	    resultMap.put("msg", EntityUtils.toString(resultResponse.getEntity(),"utf-8"));
     	  
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		try {
				post.clone();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
    	}
    	return resultMap;
    }

	public static String getMobileInfoByPost(String urlStr, String xml, int timeoutMils, String accept, String contentType, String orginalClientIp) {
        DataInputStream input = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] xmlData = xml.getBytes();
        try {
//        	Properties props = System.getProperties();  
//            props.setProperty("proxySet", "true");  
//            props.setProperty("http.proxyHost", "114.253.250.56");  
//            props.setProperty("http.proxyPort","8888");
        	if(StringUtils.isBlank(orginalClientIp)){
        		orginalClientIp = "114.253.250.56";
        	}
        	
            URL url = new URL(urlStr);
            URLConnection urlCon = url.openConnection();
            urlCon.setDoOutput(true);
            urlCon.setDoInput(true);
            urlCon.setUseCaches(false);
            urlCon.setConnectTimeout(timeoutMils);
            urlCon.setReadTimeout(timeoutMils);

            urlCon.setRequestProperty("Content-Type", contentType);
            urlCon.setRequestProperty("Content-Length", String.valueOf(xmlData.length));
            urlCon.setRequestProperty("Accept", accept);
            urlCon.setRequestProperty("Connection", "Keep-Alive");
//            urlCon.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; U; Android 2.3.7; en-us; Nexus One Build/FRF91) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1");            
            urlCon.setRequestProperty("X-Forwarded-For", orginalClientIp);//这个优先级最高
//			urlCon.setRequestProperty("X-Real-IP", orginalClientIp);
            DataOutputStream printout = new DataOutputStream(urlCon.getOutputStream());
            printout.write(xmlData);
            printout.flush();
            printout.close();
            input = new DataInputStream(urlCon.getInputStream());
            byte[] bufferByte = new byte[256];

            int i = -1;
            while ((i = input.read(bufferByte)) > -1) {
                out.write(bufferByte, 0, i);
                out.flush();
            }
        } catch (Exception e) {
            logger.error("连接错误：", e);
        }
        return out.toString();
	}
	
	public static String getMobileLoginInfoByPost2(String url, String param, int timeoutMils, String acceptEncoding, String contentType, String orginalClientIp) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
        	if(StringUtils.isBlank(orginalClientIp)){
        		orginalClientIp = "114.253.250.56";
        	}
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("Accept-Encoding", acceptEncoding);
            conn.setRequestProperty("Content-Type", contentType);
            conn.setRequestProperty("Content-Length", String.valueOf(param.getBytes().length));
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("X-Forwarded-For", orginalClientIp);//这个优先级最高
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setConnectTimeout(timeoutMils);
            conn.setReadTimeout(timeoutMils);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param.getBytes());
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            logger.error("发送 POST 请求出现异常！" + e.getMessage(), e);
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
            	logger.error(ex.getMessage(), ex);
            }
        }
        return result;
    }   
	
	public static String getMobileLoginInfoByPost(String urlStr, String param, int timeoutMils, String acceptEncoding, String contentType, String orginalClientIp) {
        DataInputStream input = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] xmlData = param.getBytes();
        try {
        	if(StringUtil.isBlank(orginalClientIp)){
        		orginalClientIp = "114.253.250.56";
        	}
        	
            URL url = new URL(urlStr);
            URLConnection urlCon = url.openConnection();
            urlCon.setDoOutput(true);
            urlCon.setDoInput(true);
            urlCon.setUseCaches(false);
            urlCon.setConnectTimeout(timeoutMils);
            urlCon.setReadTimeout(timeoutMils);

            urlCon.setRequestProperty("Content-Type", contentType);
            urlCon.setRequestProperty("Content-Length", String.valueOf(xmlData.length));
            urlCon.setRequestProperty("Accept-Encoding", acceptEncoding);
            urlCon.setRequestProperty("Connection", "Keep-Alive");
            urlCon.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; U; Android 2.3.7; en-us; Nexus One Build/FRF91) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1");
//            urlCon.setRequestProperty("X-Forwarded-For", orginalClientIp);//这个优先级最高
            DataOutputStream printout = new DataOutputStream(urlCon.getOutputStream());
            printout.write(xmlData);
            printout.flush();
            printout.close();
            input = new DataInputStream(urlCon.getInputStream());
            byte[] bufferByte = new byte[256];

            int i = -1;
            while ((i = input.read(bufferByte)) > -1) {
                out.write(bufferByte, 0, i);
                out.flush();
            }
        } catch (Exception e) {
            logger.error("连接错误：", e);
        }
        return out.toString();
    } 	
	

}
