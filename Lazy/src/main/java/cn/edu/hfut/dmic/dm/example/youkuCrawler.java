package cn.edu.hfut.dmic.dm.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
public class youkuCrawler extends BreadthCrawler {

	public static Map<String, String> t = new HashMap<String, String>();
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
	public youkuCrawler(String crawlPath, boolean autoParse, int id) {
		super(crawlPath, autoParse);
		/* start page */
		            //http://list.youku.com/category/show/c_97_s_1_d_1_p_
		this.addSeed("http://list.youku.com/category/show/c_97_s_1_d_1_p_" + id + ".html");// 电视剧
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
			Document doc = page.getDoc();
			//String nexturl = page.select(".desc-link").attr("href");
			String nexturl = page.select(".desc-link").attr("href");
			if(StringUtils.isBlank(nexturl)){
				nexturl = page.select(".title>a").attr("href");
			}
			if(StringUtils.isBlank(nexturl)){
				return;
			}
			this.setRegexRule(null);
			next.add("http:"+nexturl);
		}

		if (page.matchUrl("http://list.youku.com/show/id_.*html")) {
			try {
				/* we use jsoup to parse page */
				Document doc = page.getDoc();
				Vodinfo v = new Vodinfo();

				/* extract title and content of news by css selector */

				String smalltype = page.select(".type").get(1).attr("title");
				String title = page.select(".name").text();
				String img = page.select(".thumb").get(0).childNodes().get(0).attr("src");
				v.setBigtype("13");
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
				int i = 0;
				if (page.select(".item").size() > 3) {
					Elements nodes = page.select(".coll_10>ul>li>a");
					StringBuffer urllist = new StringBuffer();
					for (Element node : nodes) {
						String URL = node.attr("href");
						String num = node.attr("title");
						if (StringUtil.isBlank(URL)) {
							continue;
						}
						urllist.append(num + "$" + URL);
						urllist.append("#");
						i++;
					}
					String s_url = urllist.toString();
					s_url = s_url.substring(0, s_url.length() - 1);
					v.setUrl(s_url);
					v.setNeedpay("第"+i+"集");
				}

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
		int i = 2;
		while (i > 0) {
			youkuCrawler crawler = new youkuCrawler("crawl", true, i);
			crawler.setThreads(50);
			crawler.setTopN(100);
			// crawler.setResumable(true);
			/* start crawl with depth of 4 */
			crawler.start(4);
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
			youkuCrawler crawler = new youkuCrawler("crawl", true, i);
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

}