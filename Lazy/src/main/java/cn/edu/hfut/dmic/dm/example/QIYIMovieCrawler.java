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
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;
import cn.edu.hfut.dmic.webcollector.util.FileUtils;

/**
 * Crawling news from hfut news
 *
 * @author hu
 */
public class QIYIMovieCrawler extends BreadthCrawler {

	public static Map<String, String> t = new HashMap<String, String>();
	public static DBUtil dbutil =DBUtil.getInstance();
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
	public QIYIMovieCrawler(String crawlPath, boolean autoParse, int id) {
		super(crawlPath, autoParse);
		/* start page */
		this.addSeed("http://www.iqiyi.com/lib/dianying/%2C%2C_4_"+id+".html");// 电影

		this.addRegex("http://www.iqiyi.com/lib/m_.*html.*");
	}

	@Override
	public void visit(Page page, CrawlDatums next) {

		if (page.matchUrl("http://www.iqiyi.com/lib/m_.*html.*")) {
			/* we use jsoup to parse page */
			try {
				Vodinfo v = new Vodinfo();
				String img = StringUtils.substringBetween( page.select(".result_pic>a>img").toString(), "src=\"", "\"");
				v.setImg(img);
				v.setTitle( page.select(".main_title>a").text());

				
				
				Elements elements = page.select(".topic_item.clearfix");
				String bigtype = "10";
				v.setYear("90");
				for (Element element : elements) {
					if (element.html().indexOf("上映时间") > 0) {
						String year = StringUtils.substringBetween( element.toString(), "上映时间：", "-");
						if(StringUtils.isNotBlank(year))
						v.setYear( StringUtils.substringBetween( element.toString(), "上映时间：", "-"));
					}
					if (element.html().indexOf("导演") > 0) {
						v.setDirector(element.childNodes().get(1).childNodes().get(1).attr("title"));
					}
					if (element.html().indexOf("主演") > 0) {
						String acteres=element.text();
						v.setActeres(acteres);
					}
					if (element.html().indexOf("地区") > 0) {
						String area = element.childNodes().get(1).childNodes().get(1).childNodes().get(1).attr("title");
						v.setArea(area);
					

					}
					if (element.html().indexOf("看点") > 0) {
						Elements temp = element.select(".look_point");
						List<Node> node = temp.get(0).childNodes();
						String smalltype="";
						for(int i=1;i<node.size();i++){
							String t_smalltypes = node.get(i).attr("title");
							if(StringUtils.isNotBlank(t.get(t_smalltypes)))
							bigtype = t.get(t_smalltypes);
							smalltype+=t_smalltypes;
						}
						v.setBigtype(bigtype);
						v.setSmalltype(smalltype);
					}
				}
				
				v.setPlayer("qiyi");
				v.setHits(9999);
				v.setDesc(page.select(".mod-body.introduce-info>p").toString());
				
				 CloseableHttpClient httpclient = HttpClients.createDefault(); 
				 String url = v.getImg();
				 String[] names  = url.split("/");
				 String name = names[names.length-1];
				 HttpGet get = new HttpGet(url);
				 CloseableHttpResponse response = httpclient.execute(get);
				 String path = "upload/vod/"+name;
				FileUtils.writeFile(new File("/home/2kys/"+path), EntityUtils.toByteArray(response.getEntity()));
				v.setImg(path);
				String playurl = page.select(".search-btn-large.search-btn-green").attr("href");
				v.setUrl(playurl.split("#")[0]);

				dbutil.exesql(v);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void execute(int pagesize) throws Exception {
		int i = pagesize;
		while (i > 0) {
			QIYIMovieCrawler crawler = new QIYIMovieCrawler("crawl", true, i);
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
			QIYIMovieCrawler crawler = new QIYIMovieCrawler("crawl", true, i);
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