package cn.edu.hfut.dmic.dm.example;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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
public class LetvTVCrawler extends BreadthCrawler {

	public static Map<String, String> t = new HashMap<String, String>();
	public static List<Vodinfo> result = new ArrayList<Vodinfo>();
	public static Map<String,String> imgmap=new HashMap<String,String>();
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
		t.put("国产", "12");
		t.put("港台", "13");
		t.put("日韩", "14");
		t.put("欧美", "14");
	}

	/**
	 * @param crawlPath
	 *            crawlPath is the path of the directory which maintains
	 *            information of this crawler
	 * @param autoParse
	 *            if autoParse is true,BreadthCrawler will auto extract links
	 *            which match regex rules from pag
	 */
	public LetvTVCrawler(String crawlPath, boolean autoParse, int id) {
		super(crawlPath, autoParse);
		/* start page */
		this.addSeed("http://list.le.com/listn/c2_t-1_a-1_y-1_s1_md_o20_d1_p"+id+".html");// 电影
		this.addRegex("http://www.letv.com/tv/.*html");
	}

	@Override
	public void visit(Page page, CrawlDatums next) {
		if(page.matchUrl("http://list.le.com/listn/c2_.*html")){
			Elements elements = page.select(".hd_pic");
			for (Element node : elements) {
				try{
					String key = node.childNodes().get(1).attr("href");
					String value = StringUtils.substringBetween( node.childNodes().get(1).toString(), "src=\"", "\"");
					imgmap.put(key, value);
				}catch(Exception e){
					e.printStackTrace();
					break;
				}
			}
			
		}
		
		if (page.matchUrl("http://www.letv.com/tv/.*html")) {
			try {
				/* we use jsoup to parse page */
				Document doc = page.getDoc();
				Vodinfo v = new Vodinfo();

				/* extract title and content of news by css selector */
				Elements elements = page.select(".w120");
				StringBuffer urllist = new StringBuffer();
				int i=0;
				for (Element node : elements) {
					try{
						String URL = node.childNodes().get(2).childNodes().get(1).childNodes().get(1).attr("href");
						String num = node.childNodes().get(2).childNodes().get(1).childNodes().get(1).childNodes()
								.get(0).outerHtml();
						i++;
						if (StringUtil.isBlank(URL)) {
						continue;
					}
					urllist.append(num + "$" + URL);
					urllist.append("#");
					}catch(Exception e){
						break;
					}
				}
				String s_url = urllist.toString();
				s_url = s_url.substring(0, s_url.length() - 1);
				v.setUrl(s_url);
				
				String img = imgmap.get(page.getUrl());
				if(StringUtils.isBlank(img)){
					img = StringUtils.substringBetween(elements.get(0).childNodes().toString(), "src=\"", "\"");
				}

				String title = page.select(".textInfo>dt").text();
				String director = "";
				// infolist.get(5) 主演
				// infolist.get(9) 地区
				// infolist.get(11) 类型
				// infolist.get(13) year
				// infolist.get(17) decs
				String bigtype = "12";
				String smalltype = "";
				;
				// String title = page.select(".name").text();
				// String img =
				// page.select(".thumb").get(0).childNodes().get(0).attr("src");
				v.setBigtype(bigtype);
				v.setSmalltype(smalltype);
				v.setTitle(title);
				//v.setImg(img);
				v.setHits(999);
				v.setScore("9");
				v.setArea("国产");
				v.setYear(page.select(".p4>a").text());
				v.setActeres("");
				v.setDirector(director);
				// v.setScore(page.select(".num").get(0).childNode(0).outerHtml());
				v.setDesc(page.select(".p7").toString());
				//v.setVclass(",172,");																	// s_url.length()
																								// -
																								// 1);
				String imglide = page.select(".showPic>a>img").get(0).attr("src");
				v.setImglide(imglide);
				v.setImg(img);
				v.setPlayer("letv");
				v.setNeedpay("第"+i+"集");

				dbutil.exesql(v.toString());
				// createSQL(v);
			} catch (Exception e) {
				System.out.println("url："+page.getUrl()+"error");
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws Exception {
		int i = 2;
		while (i > 0) {
			LetvTVCrawler crawler = new LetvTVCrawler("crawl", true, i);
			crawler.setThreads(5);
			crawler.setTopN(100);
			// crawler.setResumable(true);
			/* start crawl with depth of 4 */
			crawler.start(4);
			i--;
		}
	}
	
	public static void execute(int  pagesize) throws Exception {
		int i = pagesize;
		while (i > 0) {
			LetvTVCrawler crawler = new LetvTVCrawler("crawl", true, i);
			crawler.setThreads(5);
			crawler.setTopN(10);
			// crawler.setResumable(true);
			/* start crawl with depth of 4 */
			crawler.start(4);
			i--;
		}
		
	}


}