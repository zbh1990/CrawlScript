package cn.edu.hfut.dmic.dm.example;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
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
public class youkuDMCrawler extends BreadthCrawler {

	public static Map<String, String> t = new HashMap<String, String>();
	public Map<String, Vodinfo> infomap = new HashMap<String, Vodinfo>();
	public static DBUtil dbutil = DBUtil.getInstance();

	static {
		t.put("电影", "1");
		t.put("电视剧", "2");
		t.put("综艺", "3");
		t.put("动漫", "4");
		t.put("动作片", "5");
		t.put("喜剧片", "6");
		t.put("爱情片", "7");
		t.put("科幻片", "8");
		t.put("恐怖片", "9");
		t.put("剧情片", "10");
		t.put("战争片", "11");
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
	public youkuDMCrawler(String crawlPath, boolean autoParse, int id) {
		super(crawlPath, autoParse);
		/* start page */
		//
		this.addSeed("http://list.youku.com/category/show/c_100_a_%E6%97%A5%E6%9C%AC_s_1_d_1_p_" + id + ".html");// 电视剧
		// this.addSeed("http://list.youku.com/category/show/c_100_s_1_d_1_p_"+id+".html")

		/* fetch url like http://news.hfut.edu.cn/show-xxxxxxhtml */
		this.addRegex("http://v.youku.com/v_show/id.*html");
		/* do not fetch jpg|png|gif */
		// this.addRegex("-.*\\.(jpg|png|gif).*");
		/* do not fetch url contains # */
		// this.addRegex("-.*#.*");
	}

	@Override
	public void visit(Page page, CrawlDatums next) {
		/* if page is news page */
		if (page.matchUrl("http://v.youku.com/v_show/id.*html")) {
			/* we use jsoup to parse page */

			/* extract title and content of news by css selector */
			/*
			 * Elements nodes = page.select(".item>a"); String title =
			 * page.select(".title>a").text();
			 */
			String nexturl = page.select(".desc-link").attr("href");
			if(StringUtils.isBlank(nexturl)){
				nexturl = page.select(".title>a").attr("href");
			}
			/**
			 * var videoId = '127671152'; var showid="19791";
			 */
			String episodeLast = StringUtils.substringBetween(page.getHtml(), "episodeLast = ", ";");
			if(StringUtils.isBlank(episodeLast)){
				episodeLast = StringUtils.substringBetween(page.getHtml(), "episodeLast:\"", "\"");
			}
/*			String vid = StringUtils.substringBetween(page.getHtml(), "videoId:\"", "\",");
			String showid = StringUtils.substringBetween(page.getHtml(), "showid:\"", "\",");*/
			String vid = StringUtils.substringBetween(page.getHtml(),"videoId = '", "';");
			if(StringUtils.isBlank(vid)){
				vid = StringUtils.substringBetween(page.getHtml(), "videoId:\"", "\",");
			}
			String showid = StringUtils.substringBetween(page.getHtml(),"var showid=\"", "\";");
			if(StringUtils.isBlank(showid)){
				showid = StringUtils.substringBetween(page.getHtml(), "showid:\"", "\",");
			}
			
			if (StringUtils.isNotBlank(episodeLast)) {
				try {
					int i = Integer.parseInt(episodeLast) / 100 + 1;
					Vodinfo v = new Vodinfo();
					v.setUrl(vid + "_" + showid + "_" + i);
					infomap.put(nexturl, v);
				} catch (Exception e) {

				}
			}

			next.add(nexturl);
			this.setRegexRule(null);
			System.out.println("nexturl:" + nexturl);
		}
		// http://www.youku.com/show_page/id
		// http://www.youku.com/show_page/id_zcc001f06962411de83b1.html
		if (page.matchUrl("http://www.youku.com/show_page/id.*html")) {
			try {
				/* we use jsoup to parse page */
				String url = page.getUrl();
				Vodinfo v = infomap.get(url);

				/* extract title and content of news by css selector */

				String bigtype = page.select(".type").get(0).childNodes().get(0).childNode(0).outerHtml();
				String smalltype = page.select(".type").get(1).attr("title");
				String title = page.select(".name").text();
				String img = page.select(".thumb").get(0).childNodes().get(0).attr("src");
				v.setBigtype("4");
				v.setSmalltype(smalltype);
				v.setTitle(title);
				v.setImg(img);
				v.setArea(page.select(".area>a").text());
				v.setYear(page.select(".pub").get(0).childNodes().get(0).toString());
				v.setActeres(page.select(".actor").get(0).attr("title"));
				v.setDirector(page.select(".director").get(0).attr("title"));
				v.setScore(page.select(".num").get(0).childNode(0).outerHtml());
				v.setImglide("");
				v.setPlayer("youku");
				v.setHits(999);
				if (page.select(".short").get(0).childNodes().size() > 1) {
					v.setDesc(page.select(".short").get(0).childNode(1).outerHtml());
				}
				//
				// Elements typenode = page.select(".crumbs>a");
				// String type

				String result = "";
				String[] params = v.getUrl().split("_");
				int pagenum = Integer.parseInt(params[2]);
				for (int n = 1; n <= pagenum; n++) {
					// http://v.youku.com/page/playlist/pm_3_vid_435315672_showid_19461_page_1
					String playurl = "http://v.youku.com/v_vpofficiallistv5/id_"+params[0]+"_showid_"+params[1]+"_page_"+n+"?__rt=1&__ro=listitem_page"+n;// http://v.youku.com/v_vpofficiallistv5/id_127671152_showid_19791_page_2?__rt=1&__ro=listitem_page2
					//String playurl = "http://v.youku.com/page/playlist/pm_3_vid_" + params[0] + "_showid_" + params[1]+ "_page_" + n;
					String temp = GetIpAddress.getInfo(playurl, 3000);
					if(temp.indexOf("稍等")>-1){
						System.out.println(11);
						 playurl = "http://v.youku.com/page/playlist/pm_3_vid_" + params[0] + "_showid_" + params[1]+ "_page_" + n;
						 temp = GetIpAddress.getInfo(playurl, 3000);
						 String date = new JSONObject(temp).get("html")+"";
							result += date;
					}
					
				
						result += temp;
					
				}
				// 创建 Pattern 对象
				Pattern r = Pattern.compile("<li .+?>[\\s\\S]+?</li>");

				// 现在创建 matcher 对象
				Matcher m = r.matcher(result);

				StringBuffer urllist = new StringBuffer();
				int i = 0;
				while (m.find()) {
					String node = m.group();
					String URL = StringUtils.substringBetween(node, "href=\"", "\"");
					String num = StringUtils.substringBetween(node, "title=\"", "\"");
					i++;
					if (StringUtil.isBlank(URL)) {
						continue;
					}

					urllist.append(num + "$" + URL);
					urllist.append("#");
				}
				String s_url = urllist.toString();
				s_url = s_url.substring(0, s_url.length() - 1);
				v.setUrl(s_url);
				v.setNeedpay("第" + i + "集");
				/*
				 * if (page.select(".item").size() > 3) { Elements nodes =
				 * page.select(".coll_10>ul>li>a"); StringBuffer urllist = new
				 * StringBuffer(); for (Element node : nodes) { String URL =
				 * node.attr("href"); String num = node.attr("title"); if
				 * (StringUtil.isBlank(URL)) { break; } urllist.append(num + "$"
				 * + URL); urllist.append("#"); } String s_url =
				 * urllist.toString(); s_url = s_url.substring(0, s_url.length()
				 * - 1); v.setUrl(s_url); }
				 */

				dbutil.exesql(v);
				// createSQL(v);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// System.out.println(v);
			/* If you want to add urls to crawl,add them to nextLink */
			/*
			 * WebCollector automatically filters links that have been fetched
			 * before
			 */
			/*
			 * If autoParse is true and the link you add to nextLinks does not
			 * match the regex rules,the link will also been filtered.
			 */
		}
	}

	public static void main(String[] args) throws Exception {
		int i = 1;
		while (i > 0) {
			youkuDMCrawler crawler = new youkuDMCrawler("crawl", true, i);
			crawler.setThreads(50);
			crawler.setTopN(100);
			// crawler.setResumable(true);
			/* start crawl with depth of 4 */
			crawler.start(5);
			i--;
		}

		/*
		 * for (Vodinfo v : result) { try { DBUtil.exesql(v.toString()); } catch
		 * (Exception e) { e.printStackTrace(); } }
		 */
	}

	public static void execute(int pagesize) throws Exception {
		int i = pagesize;
		while (i > 0) {
			youkuDMCrawler crawler = new youkuDMCrawler("crawl", true, i);
			crawler.setThreads(5);
			crawler.setTopN(100);
			// crawler.setResumable(true);
			/* start crawl with depth of 4 */
			crawler.start(4);
			i--;
		}
		DBUtil.getInstance().close();
		/*
		 * for (Vodinfo v : result) { try { DBUtil.exesql(v.toString()); } catch
		 * (Exception e) { e.printStackTrace(); } }
		 */
	}

	public static void createSQL(Vodinfo v) throws Exception {
		// INSERT INTO `mac_vod` (`d_id`, `d_name`, `d_subname`, `d_enname`,
		// `d_letter`, `d_color`, `d_pic`, `d_picthumb`, `d_picslide`,
		// `d_starring`, `d_directed`, `d_tag`, `d_remarks`, `d_area`, `d_lang`,
		// `d_year`, `d_type`, `d_type_expand`, `d_class`, `d_topic`, `d_hide`,
		// `d_lock`, `d_state`, `d_level`, `d_usergroup`, `d_stint`,
		// `d_stintdown`, `d_hits`, `d_dayhits`, `d_weekhits`, `d_monthhits`,
		// `d_duration`, `d_up`, `d_down`, `d_score`, `d_scoreall`,
		// `d_scorenum`, `d_addtime`, `d_time`, `d_hitstime`, `d_maketime`,
		// `d_content`, `d_playfrom`, `d_playserver`, `d_playnote`, `d_playurl`,
		// `d_downfrom`, `d_downserver`, `d_downnote`, `d_downurl`) VALUES (1,
		// '纽约黑帮', '', 'niuyueheibang', 'N', '',
		// 'upload/vod/2016-07-19/14688604600.jpg', '', '',
		// '莱昂纳多·迪卡普里奥,,丹尼尔·戴-刘易斯', '马丁·斯科塞斯', '阿姆斯特朗,迪卡普里奥,莱昂纳多,意大利,纽约黑帮',
		// 'BD版', '美国', '英语', 2009, 5, '', '', '0', 0, 0, 0, 0, 0, 0, 0, 230, 0,
		// 5, 209, 0, 0, 0, 7.0, 2394, 342, 1468709324, 1468860619, 1468861319,
		// 0,
		// '影片时间设定在1846到1863年间，复仇大业，他选择了暂时的忍耐和伪自己的杀父仇人。阿姆斯特朗得到了女贼珍妮-埃弗迪恩(卡梅伦-迪亚兹饰)的帮助，逐渐找到了接近“屠夫比尔”的机会，但越是接近他的内心，越是矛盾迷茫。他看清楚了“屠夫比尔”的所有罪行，他同情那些受害的人，但他却发现就算是将这个人杀死，外来移民和所有受迫害的人也不可能过上安稳日子，因为“屠夫比尔”所代表的不仅他个人和一个帮派，这个流氓政官身后有强大的政治援助。于是一个单纯的为父报仇的目的，导致了一场追求自由和平等的集体反抗。…………',
		// 'youku', '0', '', 'BD中字$XMjAyMzA3MDU2', '', '', '', '');
		Date date = new Date();
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String time = format.format(date);
		System.out.println(time);
		File f = new File("sql/" + time + ".sql");
		if (!f.exists()) {
			f.createNewFile();
		}
		synchronized (f) {
			FileUtils.writeFileWithParent(f, FileUtils.readFile(f, "utf-8") + v.toString(), "utf-8");
		}

	}

	/**
	 * unicode 转字符串
	 */
	public static String unicode2String(String utfString) {
		StringBuilder sb = new StringBuilder();
		int i = -1;
		int pos = 0;

		while ((i = utfString.indexOf("\\u", pos)) != -1) {
			sb.append(utfString.substring(pos, i));
			if (i + 5 < utfString.length()) {
				pos = i + 6;
				sb.append((char) Integer.parseInt(utfString.substring(i + 2, i + 6), 16));
			}
		}

		return sb.toString();
	}

}