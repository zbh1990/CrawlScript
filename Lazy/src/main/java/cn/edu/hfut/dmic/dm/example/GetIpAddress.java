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

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;

public class GetIpAddress {

	public static String getInfo(String srcUrl, int timeoutMils) throws IOException {

		HttpClient httpClient = new HttpClient();
		httpClient.getHostConfiguration().setProxy("120.26.119.149", 14201);
		
		GetMethod get = new GetMethod(srcUrl);
		get.addRequestHeader("Referer","http://www.easyplayer.site/");
				/*Cache-Control: max-age=0
				Upgrade-Insecure-Requests: 1*/

		get.addRequestHeader("Host","apis.web.pptv.com");
		get.addRequestHeader("Connection","keep-alive");
		get.addRequestHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36");
		get.addRequestHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		get.addRequestHeader("Accept-Language","en-US,en;q=0.8,zh-CN;q=0.6,zh;q=0.4");
		httpClient.executeMethod(get);
		return get.getResponseBodyAsString();
	}

}
