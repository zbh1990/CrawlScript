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
public class IQIYITVCrawler extends BreadthCrawler {

	public static Map<String, String> t = new HashMap<String, String>();
	public static List<Vodinfo> result = new ArrayList<Vodinfo>();
	public static String needvodnames = "老九门";
	public static DBUtil dbutil = new DBUtil();
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
	public IQIYITVCrawler(String crawlPath, boolean autoParse, int id) {
		super(crawlPath, autoParse);
		/* start page */
		this.addSeed("http://www.iqiyi.com/dianshiju/VIP.html");// 电视剧

		// this.addSeed("http://list.youku.com/category/show/c_100_s_1_d_1_p_"+id+".html")

		/* fetch url like http://news.hfut.edu.cn/show-xxxxxxhtml */
		// http://www.hunantv.com/v/3/150215/f/1503499.html
		// http://www.hunantv.com/v/3/102123/f/1503553.html
		this.addRegex("http://www.iqiyi.com/a_.*html");
		/* do not fetch jpg|png|gif */
		// this.addRegex("-.*\\.(jpg|png|gif).*");
		/* do not fetch url contains # */
		// this.addRegex("-.*#.*");
	}

	@Override
	public void visit(Page page, CrawlDatums next) {
		/* if page is news page */

		if (page.matchUrl("http://www.iqiyi.com/a_.*html")) {
			/* we use jsoup to parse page */
			try {
				String title = page.select(".white").get(0).childNodes().get(0).outerHtml();
				if (needvodnames.indexOf(title) < 0) {
					return;
				}
				Vodinfo v = new Vodinfo();

				/* extract title and content of news by css selector */

				String smalltype = "剧情 / 网络剧 / 悬疑剧";
				String img = "http://pic5.qiyipic.com/image/20160720/aa/b7/a_100026930_m_601_m2_180_236.jpg";
				v.setBigtype("12");
				v.setSmalltype(smalltype);
				v.setTitle(title);
				v.setImg(img);
				v.setArea("大陆");
				v.setYear("2016");
				v.setActeres(" 陈伟霆 / 张艺兴 / （特邀主演）赵丽颖 / 胡耘豪 / 应昊茗 / 袁冰妍 / 王美人 / 王闯 / 张铭恩 / 杨紫茳 / 张鲁一 / 李乃文 / 李宗翰");
				v.setDirector(" 梁胜权");
				v.setScore("10");
				v.setImglide("http://pic1.qiyipic.com/common/lego/20160705/9184be135ca144a0999a55ac26392e8c.jpg");
				v.setPlayer("qiyi");
				v.setHits(9999);
				v.setNeedpay("爱奇艺vip");
				v.setDesc(
						" 民国年间，九大家族镇守长沙，被称为“九门提督”。这九门势力庞大，外八行的无人不知，无人不晓，几乎所有明器，流出长沙必然经过其中一家。1933年秋，一辆神秘鬼车缓缓驶入长沙火车站，九门之首“张大佛爷”张启山身为布防官，奉命调查始末。张启山与八爷齐铁嘴一路探访，发现长沙城外有一座疑点重重的矿山，一直被日本人窥伺。为破解矿山之谜，张启山求助同为九门上三门的戏曲名伶二月红，无奈二月红虽出身考古世家，却心系重病的妻子丫头，早已金盆洗手。张启山为了国家大义和手足之情，北上去往新月饭店为二月红爱妻求药。在北平，张启山邂逅了新月饭店的大小姐尹新月，并为二月红连点三盏天灯，散尽家财。尹新月帮助张启山等人顺利返回长沙，二人暗生情愫。二月红爱妻病入膏肓，服药后不见好转，最终故去。二月红悲伤之余却意外发现家族祖辈与矿山亦有重大关联，于是振作精神，决定与张启山联手，解开矿山之谜。");
				//
				// Elements typenode = page.select(".crumbs>a");
				// String type
				Elements nodes = page.select(".site-piclist_pic_link");
				int currentindex=Integer.parseInt(page.select(".c-999>em").get(0).childNodes().get(0).outerHtml());
				v.setNeedpay("爱奇艺vip,更新到"+currentindex);
				StringBuffer urllist = new StringBuffer();
				for (int i=0;i<currentindex;i++) {
					Element node = nodes.get(i);
					String URL = node.attr("href");
					String num =i+"";
					if (StringUtil.isBlank(URL)) {
						break;
					}
					urllist.append(num + "$" + URL);
					urllist.append("#");
				}
				String s_url = urllist.toString();
				s_url = s_url.substring(0, s_url.length() - 1);
				v.setUrl(s_url);

				dbutil.exesql(v.toString());
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
			IQIYITVCrawler crawler = new IQIYITVCrawler("crawl", true, i);
			crawler.setThreads(50);
			crawler.setTopN(100);
			// crawler.setResumable(true);
			/* start crawl with depth of 4 */
			crawler.start(4);
			i--;
		}
		dbutil.close();
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
	/*
	 * public static void main(String[] args) throws IOException { String
	 * info=GetIpAddress.getInfo(
	 * "http://v.api.mgtv.com/player/video?retry=1&video_id=1054753.html",
	 * 5000); JSONObject j= new JSONObject(info);
	 * System.out.println(j.getJSONObject("data").getJSONObject("info").
	 * getString("thumb"));;
	 * 
	 * }
	 */

}