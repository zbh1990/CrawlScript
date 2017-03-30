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
public class IQIYITVMovieCrawler extends BreadthCrawler {

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
	public IQIYITVMovieCrawler(String crawlPath, boolean autoParse, int id) {
		super(crawlPath, autoParse);
		/* start page */
		this.addSeed("http://list.iqiyi.com/www/1/2-----------1980_1989--8-"+id+"-1-iqiyi--.html");// 电视剧

		// this.addSeed("http://list.youku.com/category/show/c_100_s_1_d_1_p_"+id+".html")

		/* fetch url like http://news.hfut.edu.cn/show-xxxxxxhtml */
		// http://www.hunantv.com/v/3/150215/f/1503499.html
		// http://www.hunantv.com/v/3/102123/f/1503553.html
		this.addRegex("http://www.iqiyi.com/v_.*html.*");
		/* do not fetch jpg|png|gif */
		// this.addRegex("-.*\\.(jpg|png|gif).*");
		/* do not fetch url contains # */
		// this.addRegex("-.*#.*");
	}

	@Override
	public void visit(Page page, CrawlDatums next) {
		if(page.matchUrl("http://list.iqiyi.com/www/1/.*")){
			Elements elements = page.select(".site-piclist.site-piclist-180236.site-piclist-auto").get(0).children();
			for (Element node : elements) {
				try{
					String key = node.select(".site-piclist_pic_link").attr("href");
					String img = StringUtils.substringBetween( node.select(".site-piclist_pic_link").toString(), "src=\"", "\"");
					if(StringUtils.isBlank(img)){
						continue;
					}
					Vodinfo v = new Vodinfo();
					v.setImg(img);
					v.setActeres( node.select(".role_info").text());
					v.setTitle( StringUtils.substringBetween( node.select(".site-piclist_pic_link").toString(), "title=\"", "\""));
					v.setNeedpay(node.select(".site-piclist_pic_link").text());
					infomap.put(key, v);
				}catch(Exception e){
					e.printStackTrace();
					break;
				}
			}
			
		}

		if (page.matchUrl("http://www.iqiyi.com/.*html.*")) {
			/* we use jsoup to parse page */
			try {
				Vodinfo v = infomap.get(page.getUrl());
				if(v==null){
					return;
				}

				/* extract title and content of news by css selector */

			/*	t.put("动漫", "4");
				t.put("动作", "5");
				t.put("喜剧", "6");
				t.put("爱情", "7");
				t.put("科幻", "8");
				t.put("恐怖", "9");
				t.put("剧情", "10");
				t.put("战争", "11");*/
				
				String bigtype =page.select("#data-vpointlist>a").text();
				System.out.println(page.getHtml());
				System.out.println(bigtype);
				if(StringUtils.isBlank(bigtype)){
					return ;
				}
				v.setBigtype("10");
				v.setSmalltype(page.select("#datainfo-director-list").text());
				v.setArea(page.select("#thirdPartyTagList").text());
				v.setYear("2016");
				//System.out.println(page.select("#datainfo-director-list").text());
				v.setDirector(page.select("#datainfo-director-list").text());
				v.setScore("10");
				//v.setImglide("http://pic1.qiyipic.com/common/lego/20160705/9184be135ca144a0999a55ac26392e8c.jpg");
				v.setPlayer("qiyi");
				v.setHits(9999);
				//v.setNeedpay("爱奇艺vip");
				v.setDesc(page.select("#datainfo-desc-text").toString());
				//
				// Elements typenode = page.select(".crumbs>a");
				// String type
				 CloseableHttpClient httpclient = HttpClients.createDefault(); 
				 String url = v.getImg();
				 String[] names  = url.split("/");
				 String name = names[names.length-1];
				 HttpGet get = new HttpGet(url);
				 //get.setHeader("Referer","http://easyplayer.site/?m=vod-detail-id-22033.html");
				 CloseableHttpResponse response = httpclient.execute(get);
				 String date = DateUtils.formatDate(new Date(),"yyyy-MM-dd");
				 String path = "upload/vod/"+name;
				FileUtils.writeFile(new File("/home/2kys/"+path), EntityUtils.toByteArray(response.getEntity()));
				v.setImg(path);
				v.setUrl(page.getUrl().split("#")[0]);

				dbutil.exesql(v);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void execute(int pagesize) throws Exception {
		int i = pagesize;
		while (i > 0) {
			IQIYITVMovieCrawler crawler = new IQIYITVMovieCrawler("crawl", true, i);
			crawler.setThreads(50);
			crawler.setTopN(100);
			// crawler.setResumable(true);
			/* start crawl with depth of 4 */
			crawler.start(4);
			i--;
		}
	}
	public static void main(String[] args) throws Exception {
		int i = 10;
		while (i > 0) {
			IQIYITVMovieCrawler crawler = new IQIYITVMovieCrawler("crawl", true, i);
			crawler.setThreads(5);
			crawler.setTopN(10);
			// crawler.setResumable(true);
			/* start crawl with depth of 4 */
			crawler.start(4);
			i--;
		}
	}
	public static void main1(String[] args) throws ClientProtocolException, IOException {
		 CloseableHttpClient httpclient = HttpClients.createDefault(); 
		 String url ="http://pic8.qiyipic.com/image/20160720/0d/ab/a_100028281_m_601_195_260.jpg";
		 String[] names  = url.split("/");
		 String name = names[names.length-1];
		 System.out.println(name);
		 HttpGet get = new HttpGet(url);
		 //get.setHeader("Referer","http://easyplayer.site/?m=vod-detail-id-22033.html");
		 CloseableHttpResponse response = httpclient.execute(get);
		 String date = DateUtils.formatDate(new Date(),"yyyy-MM-dd");
		 System.out.println(date);
		 String path = "upload/vod/"+date+"/"+name;
		FileUtils.writeFile(new File("/home/2kys/"+path), EntityUtils.toByteArray(response.getEntity()));
		//FileUtils.writeFile(new File("d:/test.jpg"), EntityUtils.toByteArray(response.getEntity()));
		 System.out.println();
		
	}


}