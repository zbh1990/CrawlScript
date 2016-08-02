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
public class LetvDMCrawler extends BreadthCrawler {

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
	public LetvDMCrawler(String crawlPath, boolean autoParse, int id) {
		super(crawlPath, autoParse);
		/* start page */
		this.addSeed("http://list.le.com/listn/c5_t30272_a-1_y-1_vt-1_f-1_s1_lg-1_st-1_md_o20_d1_p.html");// 电影

		// this.addSeed("http://list.youku.com/category/show/c_100_s_1_d_1_p_"+id+".html")

		/* fetch url like http://news.hfut.edu.cn/show-xxxxxxhtml */
		// http://www.hunantv.com/v/3/150215/f/1503499.html
		// http://www.hunantv.com/v/3/102123/f/1503553.html
		this.addRegex("http://www.letv.com/comic/.*html");
		/* do not fetch jpg|png|gif */
		// this.addRegex("-.*\\.(jpg|png|gif).*");
		/* do not fetch url contains # */
		// this.addRegex("-.*#.*");
	}

	@Override
	public void visit(Page page, CrawlDatums next) {
		if(page.matchUrl("http://list.le.com/listn/c5_.*html")){
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
		
		if (page.matchUrl("http://www.letv.com/comic/.*html")) {
			try {
				/* we use jsoup to parse page */
				Document doc = page.getDoc();
				Vodinfo v = new Vodinfo();

				/* extract title and content of news by css selector */
				Elements elements = page.select(".w120");
				StringBuffer urllist = new StringBuffer();
				for (Element node : elements) {
					try{
						String URL = node.childNodes().get(2).childNodes().get(1).childNodes().get(1).attr("href");
						String num = node.childNodes().get(2).childNodes().get(1).childNodes().get(1).childNodes()
								.get(0).outerHtml();
						if (StringUtil.isBlank(URL)) {
						break;
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
				String bigtype = "4";
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
				v.setArea("日本");
				v.setYear(page.select(".p4>a").text());
				v.setActeres("");
				v.setDirector(director);
				// v.setScore(page.select(".num").get(0).childNode(0).outerHtml());
				v.setDesc(page.select(".p7").toString());
				v.setVclass(",172,");																	// s_url.length()
																								// -
																								// 1);
				String imglide = page.select(".showPic>a>img").get(0).attr("src");
				v.setImglide(imglide);
				v.setImg(img);
				v.setPlayer("letv");
				v.setNeedpay("免会员直达");

				//
				// Elements typenode = page.select(".crumbs>a");
				// String type
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
				dbutil.exesql(v.toString());
				// createSQL(v);
			} catch (Exception e) {
				System.out.println("url："+page.getUrl()+"error");
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws Exception {
		int i = 1;
		while (i > 0) {
			LetvDMCrawler crawler = new LetvDMCrawler("crawl", true, i);
			crawler.setThreads(5);
			crawler.setTopN(100);
			// crawler.setResumable(true);
			/* start crawl with depth of 4 */
			crawler.start(4);
			i--;
		}
		DBUtil dbUtil =DBUtil.getInstance();
		for (Vodinfo v : result) {
			try {
				DBUtil.exesql(v.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void execute(int  pagesize) throws Exception {
		int i = pagesize;
		while (i > 0) {
			LetvDMCrawler crawler = new LetvDMCrawler("crawl", true, i);
			crawler.setThreads(5);
			crawler.setTopN(100);
			// crawler.setResumable(true);
			/* start crawl with depth of 4 */
			crawler.start(4);
			i--;
		}
		DBUtil dbUtil =DBUtil.getInstance();
		for (Vodinfo v : result) {
			try {
				DBUtil.exesql(v.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
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
		File f = new File("sql/" + time + ".sql");
		if (!f.exists()) {
			f.createNewFile();
		}
		synchronized (f) {
			FileUtils.writeFileWithParent(f, FileUtils.readFile(f, "utf-8") + v.toString(), "utf-8");
		}

	}

}