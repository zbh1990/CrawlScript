package cn.edu.hfut.dmic.dm.example;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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

/**
 * Crawling news from hfut news
 *
 * @author hu
 */
public class hanjuTVCrawler extends BreadthCrawler {

	public static Map<String, String> t = new HashMap<String, String>();
	public List<Vodinfo> result = new ArrayList<Vodinfo>();
	public static DBUtil dbutil =DBUtil.getInstance();
	public Map<String,Vodinfo> infomap=new HashMap<String,Vodinfo>();
	static {
		t.put("电影", "1");
		t.put("电视剧", "2");
		t.put("综艺", "3");
		t.put("动漫", "4");
		t.put("动作", "5");
		t.put("喜剧", "6");
		t.put("爱情", "7");
		t.put("科幻", "8");
		t.put("恐怖", "9");
		t.put("剧情", "10");
		t.put("战争", "11");
		t.put("国产剧", "12");
		t.put("港台剧", "13");
		t.put("日韩剧", "14");
		t.put("欧美剧", "14");
	}

	/**
	 * @param crawlPath
	 *            crawlPath is the path of the directory which maintains
	 *            information of this crawler
	 * @param autoParse
	 *            if autoParse is true,BreadthCrawler will auto extract links
	 *            which match regex rules from pag
	 */
	public hanjuTVCrawler(String crawlPath, boolean autoParse, int id,String year) {
		super(crawlPath, autoParse);
		/* start page */
		String url = "http://www.y3600.com/hanju/"+year+"/index.html";
		if(id>1){
			url = "http://www.y3600.com/hanju/"+year+"/index_"+id+".html";
		}
		this.addSeed(url);// 电视剧

		// this.addSeed("http://list.youku.com/category/show/c_100_s_1_d_1_p_"+id+".html")

		/* fetch url like http://news.hfut.edu.cn/show-xxxxxxhtml */
		// http://www.hunantv.com/v/3/150215/f/1503499.html
		// http://www.hunantv.com/v/3/102123/f/1503553.html
		this.addRegex(".*/hanju/"+year+"/.*html");
		/* do not fetch jpg|png|gif */
		// this.addRegex("-.*\\.(jpg|png|gif).*");
		/* do not fetch url contains # */
		// this.addRegex("-.*#.*");
	}

	@Override
	public void visit(Page page, CrawlDatums next) {
		if(page.matchUrl("http://www.y3600.com/hanju/2016/index.*")||page.matchUrl("http://www.y3600.com/hanju/2015/index.*")){
			Elements elements = page.select(".m-ddone.w1230").get(0).children();
			for (Element node : elements) {
				try{
					String key = "http://www.y3600.com"+node.select(".img.playico").attr("href");
					String img = StringUtils.substringBetween( node.select(".img.playico").toString(), "src=\"", "\"");
					if(StringUtils.isBlank(img)){
						continue;
					}
					Vodinfo v = new Vodinfo();
					v.setImg(img);
					v.setActeres( node.select(".zyy").text());
					v.setTitle( node.select(".tit").get(1).text());
					v.setNeedpay(node.select(".tit").get(0).text());
					infomap.put(key, v);
				}catch(Exception e){
					e.printStackTrace();
					break;
				}
			}
			
		}

		if (page.matchUrl("http://www.y3600.com/hanju/2016/.*.html")||page.matchUrl("http://www.y3600.com/hanju/2015/.*.html")) {
			//if (page.matchUrl("http://www.y3600.com/hanju/2016/898.html")) {
			/* we use jsoup to parse page */
			try {
				Vodinfo v = infomap.get(page.getUrl());
				if(v==null){
					return;
				}

				/* extract title and content of news by css selector */
				if(page.getHtml().indexOf("ck_yk('")<0){
					return;
				}
				String smalltype = "剧情";
				v.setBigtype("14");
				v.setSmalltype(smalltype);
				v.setArea("韩国");
				v.setYear("2016");
				v.setDirector("");
				v.setScore("10");
				 CloseableHttpClient httpclient = HttpClients.createDefault(); 
				 String url = v.getImg();
				 String[] names  = url.split("/");
				 String name = names[names.length-1];
				 HttpGet get = new HttpGet(url);
				 CloseableHttpResponse response = httpclient.execute(get);
				 String path = "upload/vod/"+name;
				FileUtils.writeFile(new File("/home/2kys/"+path), EntityUtils.toByteArray(response.getEntity()));
				v.setImg(path);
				v.setPlayer("youkuyun");
				v.setHits(9999);
				//v.setNeedpay("爱奇艺vip");
				String info = page.select(".intro").toString();
				v.setDesc(info.substring(info.indexOf("剧情简介")));
				//
				// Elements typenode = page.select(".crumbs>a");
				// String type
				Elements pnodes= page.select(".sort>ul");
						Elements nodes=new Elements();
				if(page.select(".sort>ul")==null||page.select(".sort>ul").size()==0||nodes.text().indexOf("ck_yk('")<0){
					Elements temp=page.select("#playlist>div>ul>li>a");
					for(Element node :	temp){
						if(node.toString().indexOf("ck_yk('")>-1){
							nodes.add(node);
						}
					}
				}else{
					nodes= pnodes.get(0).children();
				}
				 
				int currentindex = 0;
				StringBuffer urllist = new StringBuffer();
				for (int i=0;i<nodes.size();i++) {
					Element node = nodes.get(i);
					String URL = StringUtils.substringBetween(node.toString(), "ck_yk('", "==")+"==";
					String num =node.text();
					if (StringUtil.isBlank(URL)) {
						break;
					}
					urllist.append(num + "$" + URL);
					urllist.append("#");
				}
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
			hanjuTVCrawler crawler = new hanjuTVCrawler("crawl", true, i,"2016");
			crawler.setThreads(5);
			crawler.setTopN(10);
			// crawler.setResumable(true);
			/* start crawl with depth of 4 */
			crawler.start(4);
			i--;
		}
		 i = pagesize;
		while (i > 0) {
			hanjuTVCrawler crawler = new hanjuTVCrawler("crawl", true, i,"2015");
			crawler.setThreads(5);
			crawler.setTopN(10);
			// crawler.setResumable(true);
			/* start crawl with depth of 4 */
			crawler.start(4);
			i--;
		}
	}
	public static void main(String[] args) throws Exception {
		int i = 2;
		while (i > 0) {
			hanjuTVCrawler crawler = new hanjuTVCrawler("crawl", true, i,"2016");
			crawler.setThreads(5);
			crawler.setTopN(100);
			// crawler.setResumable(true);
			/* start crawl with depth of 4 */
			crawler.start(4);
			i--;
		}
	}
	public static void main1(String[] args) throws ClientProtocolException, IOException {
		 CloseableHttpClient httpclient = HttpClients.createDefault(); 
		 String url ="http://img.y3600.com/d/file/p/2016/07/26/small41dff436019e3b080941ce73da074ee9.jpg";
		 String[] names  = url.split("/");
		 String name = names[names.length-1];
		 System.out.println(name);
		 HttpGet get = new HttpGet(url);
		 get.setHeader("Referer","http://easyplayer.site/?m=vod-detail-id-22033.html");
		 CloseableHttpResponse response = httpclient.execute(get);
		 String date = DateUtils.formatDate(new Date(),"yyyy-MM-dd");
		 System.out.println(date);
		 String path = "upload/vod/"+date+"/"+name;
		//FileUtils.writeFile(new File("/home/2kys/"+path), EntityUtils.toByteArray(response.getEntity()));
		FileUtils.writeFile(new File("d:/test.jpg"), EntityUtils.toByteArray(response.getEntity()));
		 System.out.println(response.getEntity());
		
	}


}