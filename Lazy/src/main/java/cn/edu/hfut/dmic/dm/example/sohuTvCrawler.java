package cn.edu.hfut.dmic.dm.example;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
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
public class sohuTvCrawler extends BreadthCrawler {

	public static Map<String, String> t = new HashMap<String, String>();
	public static Map<String, String> vod = new HashMap<String, String>();
	public Map<String, String> imgmap = new HashMap<String, String>();
	public static DBUtil dbutil = DBUtil.getInstance();
	static {
		// 内地 香港 台湾 美国 日本 韩国 英国 泰国 其它
		t.put("国产", "12");
		t.put("内地", "12");
		t.put("香港", "13");
		t.put("台湾", "13");
		t.put("港台", "13");
		t.put("日本", "14");
		t.put("韩国", "14");
		t.put("美国", "15");
		t.put("英国", "15");
		t.put("泰国", "15");
		t.put("欧美", "15");
		t.put("其它", "15");
	}
	static {
		vod.put("言情", "15");
		vod.put("都市", "16");
		vod.put("家庭", "17");
		vod.put("生活", "18");
		vod.put("偶像", "19");
		vod.put("喜剧", "20");
		vod.put("历史", "21");
		vod.put("古装", "22");
		vod.put("武侠", "23");
		vod.put("刑侦", "24");
		vod.put("战争", "25");
		vod.put("神话", "26");
		vod.put("军旅", "27");
		vod.put("谍战", "28");
		vod.put("商战", "29");
		vod.put("校园", "30");
		vod.put("穿越", "31");
		vod.put("悬疑", "32");
		vod.put("犯罪", "33");
		vod.put("科幻", "34");
		vod.put("预告片", "35");

	}

	/**
	 * @param crawlPath
	 *            crawlPath is the path of the directory which maintains
	 *            information of this crawler
	 * @param autoParse
	 *            if autoParse is true,BreadthCrawler will auto extract links
	 *            which match regex rules from pag
	 */
	public sohuTvCrawler(String crawlPath, boolean autoParse, int id) {
		super(crawlPath, autoParse);
		/* start page */
		this.addSeed("http://so.tv.sohu.com/list_p1101_p20_p3_p40_p5_p6_p74_p80_p91_p10" + id + "_p11_p12_p13.html");// 电影

		// this.addSeed("http://list.youku.com/category/show/c_100_s_1_d_1_p_"+id+".html")

		/* fetch url like http://news.hfut.edu.cn/show-xxxxxxhtml */
		// http://www.hunantv.com/v/3/150215/f/1503499.html
		// http://www.hunantv.com/v/3/102123/f/1503553.html
		this.addRegex("http://tv.sohu.com/item/.*html");
		/* do not fetch jpg|png|gif */
		// this.addRegex("-.*\\.(jpg|png|gif).*");
		/* do not fetch url contains # */
		// this.addRegex("-.*#.*");
	}

	@Override
	public void visit(Page page, CrawlDatums next) {

		if (page.matchUrl("http://tv.sohu.com/item/.*html")) {
			try {
				/* we use jsoup to parse page */
				Document doc = page.getDoc();
				Vodinfo v = new Vodinfo();

				String img = page.select(".drama-pic>a>img").attr("src");

				String title = page.select(".vname").text();
				Elements elements = page.select(".cfix.drama-info").select(".cfix>li");
				for (Element element : elements) {
					if (element.html().indexOf("上映时间") > 0) {
						v.setYear(element.childNodes().get(1).outerHtml());
					}
					if (element.html().indexOf("导演") > 0) {
						v.setDirector(element.childNodes().get(1).childNodes().toString());
					}
					if (element.html().indexOf("主演") > 0) {
						String acteres="";
						Elements temp=element.select(".w1>a");
						for (Element t : temp) {
							acteres+=","+t.childNodes().toString();
						}
						v.setActeres(acteres);
					}
					if (element.html().indexOf("地区") > 0) {
						String area = element.childNodes().get(1).childNodes().get(0).outerHtml();
						v.setArea(area);
						String bigtype = t.get(area);
						if (StringUtils.isBlank(bigtype)) {
							System.out.println("没找到类型" + area);
							bigtype = "12";
						}
						v.setBigtype(t.get(area));

					}
					if (element.html().indexOf("类型") > 0) {
;						List<Node> node = element.childNodes();
						String smalltype="";
						String vclass = "";
						for(int i=1;i<node.size();i++){
							String smalltypes = node.get(i).outerHtml();
							Iterator it = vod.keySet().iterator();
							
							while (it.hasNext()) {
								String key = (String) it.next();
								if (smalltypes.indexOf(key) > -1) {
									vclass = vclass+vod.get(key)+",";
									smalltype=smalltype+key+",";
								}
							}
						}
						
						v.setVclass(vclass);
						v.setSmalltype(smalltype);
					}
				}

				;
				v.setTitle(title);
				v.setHits(999);
				v.setScore("9");
				v.setDesc(page.select(".short_intro.hide").toString());

				elements = page.select(".mod.general").get(0).select(".pic>a");
				StringBuffer urllist = new StringBuffer();
				int i = 0;
				for (Element node : elements) {
					try {
						String URL = node.attr("href");
						String num = node.attr("title");
						if (StringUtil.isBlank(URL)) {
							continue;
						}
						i++;
						urllist.append(num + "$" + URL);
						urllist.append("#");
					} catch (Exception e) {
						break;
					}
				}
				String s_url = urllist.toString();
				if(s_url.length()<=1){
					System.out.println("s_url:"+s_url);
					return ;
				}
				s_url = s_url.substring(0, s_url.length() - 1);
				v.setUrl(s_url);

				String imglide = "";
				v.setImglide(imglide);
				v.setImg(img);
				v.setPlayer("sohu");
				v.setNeedpay("第" + i + "集");
				 dbutil.exesql(v);
			} catch (Exception e) {
				System.out.println("url：" + page.getUrl() + "error");
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws Exception {
		int i = 3;
		while (i > 0) {
			sohuTvCrawler crawler = new sohuTvCrawler("crawl", true, i);
			crawler.setThreads(50);
			crawler.setTopN(100);
			// crawler.setResumable(true);
			/* start crawl with depth of 4 */
			crawler.start(4);
			i--;
		}
	}

	public static void execute(int pagesize) throws Exception {
		int i = pagesize;
		while (i > 0) {
			sohuTvCrawler crawler = new sohuTvCrawler("crawl", true, i);
			crawler.setThreads(5);
			crawler.setTopN(100);
			// crawler.setResumable(true);
			/* start crawl with depth of 4 */
			crawler.start(4);
			i--;
		}
		DBUtil.getInstance().close();
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