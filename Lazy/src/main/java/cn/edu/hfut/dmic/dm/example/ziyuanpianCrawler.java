package cn.edu.hfut.dmic.dm.example;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.DateUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.edu.hfut.dmic.dm.example.domain.Vodinfo;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;
import cn.edu.hfut.dmic.webcollector.util.FileUtils;
import cn.edu.hfut.dmic.webcollector.util.RegexRule;

/**
 * Crawling news from hfut news
 *
 * @author hu
 */
public class ziyuanpianCrawler extends BreadthCrawler {

	public static Map<String, String> t = new HashMap<String, String>();
	public List<Vodinfo> result = new ArrayList<Vodinfo>();
	public static DBUtil dbutil =DBUtil.getInstance();
	public Map<String,Vodinfo> infomap=new HashMap<String,Vodinfo>();
	static {
		t.put("电影", "1");
		t.put("电视剧", "2");
		t.put("综艺片", "3");
		t.put("动漫", "4");
		t.put("动画片", "4");
		t.put("动作片", "5");
		t.put("喜剧片", "6");
		t.put("爱情片", "7");
		t.put("科幻片", "8");
		t.put("恐怖片", "9");
		t.put("剧情片", "10");
		t.put("伦理片", "10");
		t.put("微电影", "10");
		t.put("战争片", "11");
		t.put("国产剧", "12");
		t.put("内地剧", "12");
		t.put("港台剧", "13");
		t.put("日韩剧", "14");
		t.put("欧美剧", "15");
		t.put("新马泰", "15");
		t.put("其它", "15");
	}

	/**
	 * @param crawlPath
	 *            crawlPath is the path of the directory which maintains
	 *            information of this crawler
	 * @param autoParse
	 *            if autoParse is true,BreadthCrawler will auto extract links
	 *            which match regex rules from pag
	 */
	public ziyuanpianCrawler(String crawlPath, boolean autoParse, int id,String year) {
		super(crawlPath, autoParse);
		/* start page */
		String url = "http://www.ziyuanpian.com/list/?0-"+id+".html";
		this.addSeed(url);// 

		// this.addSeed("http://list.youku.com/category/show/c_100_s_1_d_1_p_"+id+".html")

		/* fetch url like http://news.hfut.edu.cn/show-xxxxxxhtml */
		// http://www.hunantv.com/v/3/150215/f/1503499.html
		// http://www.hunantv.com/v/3/102123/f/1503553.html
		this.addRegex("http://www.ziyuanpian.com/detail.*");
		/* do not fetch jpg|png|gif */
		// this.addRegex("-.*\\.(jpg|png|gif).*");
		/* do not fetch url contains # */
		// this.addRegex("-.*#.*");
	}

	@Override
	public void visit(Page page, CrawlDatums next) {

		if (page.matchUrl("http://www.ziyuanpian.com/detail.*")) {
		//if (page.getUrl().equals("http://www.ziyuanpian.com/detail/?1863.html")) {
			/* we use jsoup to parse page */
			//System.out.println(page.getHtml());
			 StringBuffer urllist = new StringBuffer();
			try {
				Vodinfo v = new Vodinfo();
				  Elements as = page.select(".bt>table>tbody>tr>td");
				  for (int i=0;i<as.size()-1;i++) {
					  Element a = as.get(i);
			            String text = a.childNode(1).outerHtml();
			                urllist.append(text);
							urllist.append("#");
			        }
				  if(StringUtils.isBlank(urllist.toString())){
					  return;
				  }
				  String path = page.select(".img>img").attr("src");
				  
				  
				  /*	0 影片名称：柏林谍影第一季
					1影片备注：更新至03集
					2影片演员：理查德·阿米蒂奇 / 理查德·迪兰 / 米歇尔·佛贝丝 / 瑞斯·伊凡斯
					3影片导演：吉塞佩·卡波通蒂  
					4影片类型：欧美剧
					5影片地区：欧美
					6更新时间：2016-11-16 10:41:04
					7影片状态：连载第3集
					8影片语言：英语
					9上映日期：2016
					 10讲述中情局的柏林分部出现了情报被泄露的情况， Richard Armitage饰演的丹尼尔奉命调查，但他越深入地查下去，就发现水越深，很多人的命运可能被永久改变..
*/
				  
				  Elements infos = page.select("html>body>table>tbody>tr>td>table>tbody>tr>td");
				  		v.setTitle(infos.get(0).select("font").text());
				  		v.setActeres(infos.get(2).select("font").text());
				  		v.setDirector(infos.get(3).select("font").text());
				  		String bigtypes = infos.get(4).select("font").text();
						Iterator it = t.keySet().iterator();
						String bigtype = "";
						while (it.hasNext()) {
							String key = (String) it.next();
							if (bigtypes.indexOf(key) > -1) {
								bigtype = t.get(key);
								break;
							}
						}
						if(StringUtil.isBlank(bigtype)){
							System.out.println(bigtypes);
						}
						v.setBigtype(bigtype);
						v.setSmalltype("");
						v.setArea(infos.get(5).select("font").text());
						v.setYear(infos.get(9).select("font").text());
						v.setNeedpay(infos.get(1).select("font").text());
						
						v.setScore("7");
						v.setImg(path);
						v.setPlayer("ykyun");
						v.setHits(1000);
					  System.out.println(infos.get(4).select("font").text());
			
				  
				  
			
				//v.setNeedpay("爱奇艺vip");
				String info = page.select(".intro").select("font").text();
				v.setDesc(info);
			
				String s_url = urllist.toString();
				s_url = s_url.substring(0, s_url.length() - 1);
				v.setUrl(s_url);

				dbutil.exesql(v);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void execute(int pagesize) throws Exception {
		int i = pagesize;
		while (i > 0) {
			ziyuanpianCrawler crawler = new ziyuanpianCrawler("crawl", true, i,"2016");
			crawler.setThreads(50);
			crawler.setTopN(100);
			// crawler.setResumable(true);
			/* start crawl with depth of 4 */
			crawler.start(4);
			i--;
		}
	}
	public static void main(String[] args) throws Exception {
		int i = 1;
		while (i > 0) {
			ziyuanpianCrawler crawler = new ziyuanpianCrawler("crawl", true, i,"2016");
			crawler.setThreads(50);
			crawler.setTopN(100);
			// crawler.setResumable(true);
			/* start crawl with depth of 4 */
			crawler.start(4);
			i--;
		}
	}
	public static void main1(String[] args) throws ClientProtocolException, IOException {
		 CloseableHttpClient httpclient = HttpClients.createDefault(); 
		 String url ="http://www.ziyuanpian.com/detail/?2266.html";
		 String[] names  = url.split("/");
		 String name = names[names.length-1];
		 System.out.println(name);
		 HttpGet get = new HttpGet(url);
		 //get.setHeader("Referer","http://easyplayer.site/?m=vod-detail-id-22033.html");
		 CloseableHttpResponse response = httpclient.execute(get);
		 String date = DateUtils.formatDate(new Date(),"yyyy-MM-dd");
		 System.out.println(date);
		 String path = "upload/vod/"+date+"/"+name;
		//FileUtils.writeFile(new File("/home/2kys/"+path), EntityUtils.toByteArray(response.getEntity()));
		//FileUtils.writeFile(new File("d:/test.jpg"), EntityUtils.toByteArray(response.getEntity()));
		 System.out.println(EntityUtils.toString(response.getEntity()));
		
	}


}