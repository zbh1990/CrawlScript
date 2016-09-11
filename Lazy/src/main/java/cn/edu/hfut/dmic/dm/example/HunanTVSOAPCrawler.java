package cn.edu.hfut.dmic.dm.example;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
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
public class HunanTVSOAPCrawler extends BreadthCrawler {

	public static Map<String, String> t = new HashMap<String, String>();
	public Map<String, Vodinfo> infomap = new HashMap<String, Vodinfo>();
	public static DBUtil dbutil = DBUtil.getInstance();
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
		t.put("欧美", "15");
	}

	/**
	 * @param crawlPath
	 *            crawlPath is the path of the directory which maintains
	 *            information of this crawler
	 * @param autoParse
	 *            if autoParse is true,BreadthCrawler will auto extract links
	 *            which match regex rules from pag
	 */
	public HunanTVSOAPCrawler(String crawlPath, boolean autoParse, int id) {
		super(crawlPath, autoParse);
		/* start page */
		this.addSeed("http://list.mgtv.com/2/-1---------1---.html");// 电视剧

		// this.addSeed("http://list.youku.com/category/show/c_100_s_1_d_1_p_"+id+".html")

		/* fetch url like http://news.hfut.edu.cn/show-xxxxxxhtml */
		// http://www.hunantv.com/v/3/150215/f/1503499.html
		// http://www.hunantv.com/v/3/102123/f/1503553.html
		this.addRegex("http://www.hunantv.com/v/2.*html");
		/* do not fetch jpg|png|gif */
		// this.addRegex("-.*\\.(jpg|png|gif).*");
		/* do not fetch url contains # */
		// this.addRegex("-.*#.*");
	}

	@Override
	public void visit(Page page, CrawlDatums next) {
		/* if page is news page */

		if (page.matchUrl("http://list.mgtv.com/2/-1---------1---.*")) {
			Elements elements = page.select(".clearfix.ullist-ele").get(0).children();
			for (Element node : elements) {
				try {
					String key = node.select(".a-pic-play").attr("href");
					String img = StringUtils.substringBetween(node.childNodes().get(1).toString(), "data-original=\"",
							"\"");
					if (StringUtils.isBlank(img)) {
						continue;
					}
					Vodinfo v = new Vodinfo();
					v.setImg(img);
					v.setActeres(node.select(".a-pic-t2").html());
					v.setTitle(node.select(".a-pic-t1").text());
					v.setScore("");
					infomap.put(key, v);
				} catch (Exception e) {
					e.printStackTrace();
					break;
				}
			}

		}
		if (page.matchUrl("http://www.hunantv.com/v/2.*html")) {
			/* we use jsoup to parse page */
			String s = page.getUrl();
			Vodinfo v = infomap.get(s);
			if (v == null) {
				return;
			}

			String year = StringUtils.substringBetween(page.getHtml(), "year:", ",");
			v.setYear(year);
			String id = s.substring(s.lastIndexOf("/") + 1, s.lastIndexOf(".html"));
			v.setPlayer("hunantv");

			String playinfourl = "http://v.api.mgtv.com/list/tvlist?video_id=" + id+"&page=1&size=250";
			try {
				String playinfo = GetIpAddress.getInfo(playinfourl, 5000);
				JSONObject j = new JSONObject(playinfo);
				JSONArray infob = j.getJSONObject("data").getJSONArray("list");
				StringBuffer urllist = new StringBuffer();
				for (int i = 0; i < infob.length(); i++) {
					JSONObject t = (JSONObject) infob.get(i);
					try {
						String URL = "http://www.hunantv.com" + t.getString("url");
						String num = t.getString("t1")+"_"+t.getString("t2");
						if (StringUtil.isBlank(URL)) {
							break;
						}
						urllist.append(num + "$" + URL);
						urllist.append("#");
					} catch (Exception e) {
						break;
					}
				}
				v.setNeedpay(j.getJSONObject("data").getInt("count")+"集");
				String s_url = urllist.toString();
				s_url = s_url.substring(0, s_url.length() - 1);
				v.setUrl(s_url);
				// v.setHits(new
				// JSONObject(playinfo).getJSONObject("data").getInt("all"));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			/* extract title and content of news by css selector */

			// .item.short 0主演 1类型
			// .item.long 0导演 1地区
			String smalltype =StringUtils.substringBetween(page.getHtml(), "sub_type: \"", "\",");
			String director = "";
			String area = "大陆";
			v.setBigtype("12");

			v.setSmalltype(smalltype);

			v.setArea(area);
			// v.setYear(page.select(".pub").get(0).childNodes().get(0).toString());
			v.setDirector(director);
			v.setDesc(page.select(".meta-intro").outerHtml());
			v.setHits(999);
			//
			// Elements typenode = page.select(".crumbs>a");
			// String type
			try {
				dbutil.exesql(v);
				// createSQL(v);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws Exception {
		int i = 2;
		while (i > 0) {
			HunanTVSOAPCrawler crawler = new HunanTVSOAPCrawler("crawl", true, i);
			crawler.setThreads(5);
			crawler.setTopN(10);
			// crawler.setResumable(true);
			/* start crawl with depth of 4 */
			crawler.start(4);
			i--;
		}
	}

	public static void execute(int pagesize) throws Exception {
		int i = pagesize;
		while (i > 0) {
			HunanTVSOAPCrawler crawler = new HunanTVSOAPCrawler("crawl", true, i);
			crawler.setThreads(5);
			crawler.setTopN(100);
			// crawler.setResumable(true);
			/* start crawl with depth of 4 */
			crawler.start(4);
			i--;
		}
		DBUtil.getInstance().close();

	}



}