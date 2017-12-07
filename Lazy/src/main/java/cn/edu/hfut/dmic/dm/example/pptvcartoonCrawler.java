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
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.DateUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
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
public class pptvcartoonCrawler extends BreadthCrawler {

	public static Map<String, String> t = new HashMap<String, String>();
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
	}

	/**
	 * @param crawlPath
	 *            crawlPath is the path of the directory which maintains
	 *            information of this crawler
	 * @param autoParse
	 *            if autoParse is true,BreadthCrawler will auto extract links
	 *            which match regex rules from pag
	 */
	public pptvcartoonCrawler(String crawlPath, boolean autoParse, int id) {
		super(crawlPath, autoParse);
		/* start page */
		this.addSeed("http://list.pptv.com/?page="+id+"&type=3&sort=1&area=8");// 动漫

		// this.addSeed("http://list.youku.com/category/show/c_100_s_1_d_1_p_"+id+".html")

		/* fetch url like http://news.hfut.edu.cn/show-xxxxxxhtml */
		// http://v.pptv.com/show/afDcWsIomNY5tx8.html?rcc_src=L1
		this.addRegex("http://v.pptv.com/show/.*html.*");
		/* do not fetch jpg|png|gif */
		// this.addRegex("-.*\\.(jpg|png|gif).*");
		/* do not fetch url contains # */
		// this.addRegex("-.*#.*");
	}

	@Override
	public void visit(Page page, CrawlDatums next) {
		/* if page is news page */
		Vodinfo v = null;
		if (page.matchUrl("http://v.pptv.com/show/.*html.*")) {
			try {
				/* we use jsoup to parse page */
				v = new Vodinfo();
				v.setPlayer("pptv");
				String body = page.getHtml();

				// 播放列表
				String year = StringUtils.substringBetween(body, "http://list.pptv.com?year=", "&type=3");
				JSONObject param = new JSONObject(StringUtils.substringBetween(body, "var webcfg =", ";"));

				v.setTitle(param.getString("p_title"));

				// 播放列表地址
				String playinfourl = "http://apis.web.pptv.com/show/videoList?pid=" + param.get("pid");
				String playinfo = GetIpAddress.getInfo(playinfourl, 5000);
				String date = new JSONObject(playinfo).get("data")+"";
				int j=3;
				if (StringUtils.isBlank(date)) {
					while (j > 0) {
						playinfo = getByurl(playinfourl);
						date = new JSONObject(playinfo).get("data")+"";
						if (StringUtils.isNotBlank(date)) {
							break;
						}
						j--;
						Thread.sleep(1000);
					}
				}
				JSONArray playinfoList = null;
				try{
				 playinfoList = new JSONObject(playinfo).getJSONObject("data").getJSONArray("list");
				}catch(Exception e){
					e.printStackTrace();
				}
				StringBuffer urllist = new StringBuffer();
				String needpay = "";
				for (int i = 0; i < playinfoList.length(); i++) {
					JSONObject urlinfo = (JSONObject) playinfoList.get(i);

					urllist.append(urlinfo.getString("epTitle") + "$" + urlinfo.getString("url"));
					urllist.append("#");
					needpay=urlinfo.getString("epTitle");
				}
				v.setNeedpay("第"+needpay+"集");
				String s_url = urllist.toString();
				s_url = s_url.substring(0, s_url.length() - 1 > 0 ? s_url.length() - 1 : 0);
				v.setUrl(s_url);
				v.setHits(999);

				/*
				 * // 别名： Ange Vierge 声优： 寿美菜子 原由实 丰崎爱生 立花理香 布里德卡特·塞拉·惠美 相坂优歌
				 * 石原舞 生田善子 高桥李依 山本希望 田村由加莉 监督： 田村正文 标签： 2016年 日本 神魔 机械 人气：289万
				 * 简介：《Ange Vierge》每周日凌晨02:00全网首播，PPTV大陆正版授权，敬请期待！
				 * 电视动画动画《Ange... 详情>
				 */
				Elements nodelist = page.select(".bd>ul>li");
				String actors = "";
				String director = "";
				String area = "日本";
				String smalltype = "";
				String desc = "";
				for (Element t : nodelist) {
					if (t.outerHtml().indexOf("声优") > -1) {
						actors = getTitleFromNodes(t.childNodes());
					}
					if (t.outerHtml().indexOf("监督") > -1) {
						director =  getTitleFromNodes(t.childNodes());
					}
					if (t.outerHtml().indexOf("标签") > -1) {
						smalltype =  getTitleFromNodes(t.childNodes());
					}
					if (t.outerHtml().indexOf("简介") > -1) {
						desc =  getTitleFromNodes(t.childNodes());
					}
				}

				v.setBigtype("4");
				v.setSmalltype(smalltype);

				v.setArea(area);
				// v.setYear(page.select(".pub").get(0).childNodes().get(0).toString());
				v.setActeres(actors);
				v.setDirector(director);
				v.setDesc(desc);
				if(StringUtils.isNotBlank(year)){
				v.setYear(year);
				}else{
					v.setYear("2016");
				}
				String imgurl =page.select(".btn_more").get(0).attr("href");
				String detailbody = GetIpAddress.getInfo(imgurl, 5000);
				String img = StringUtils.substringBetween(detailbody, "data-src2=\"", "\"");
				v.setImg(img);
				//
				// Elements typenode = page.select(".crumbs>a");
				// String type
				dbutil.exesql(v);
				// createSQL(v);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

	public String getTitleFromNodes(List<Node> nodelist) {
		String result = "";
		for (Node temp : nodelist) {
			if (StringUtils.isNotBlank(temp.attr("title")))
				result = result + "," + temp.attr("title");
		}
		return result;
	}

	public static void execute(int  pagesize) throws Exception {
		int i = pagesize;
		while (i > 0) {
			pptvcartoonCrawler crawler = new pptvcartoonCrawler("crawl", true, i);
			crawler.setThreads(5);
			crawler.setTopN(10);
			// crawler.setResumable(true);
			/* start crawl with depth of 4 */
			crawler.start(4);
			i--;
		}
		DBUtil.getInstance().close();
	}
	
	public static void main(String[] args) throws Exception {
		int i = 1;
		while (i > 0) {
			pptvcartoonCrawler crawler = new pptvcartoonCrawler("crawl", true, i);
			crawler.setThreads(5);
			crawler.setTopN(10);
			// crawler.setResumable(true);
			/* start crawl with depth of 4 */
			crawler.start(4);
			i--;
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

	public static void main1(String[] args) throws FileNotFoundException, IOException {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		String url = "http://apis.web.pptv.com/show/videoList?pid=9042479";
		HttpGet get = new HttpGet(url);
		get.setHeader("Cookie", "pgv_pvi=8222973952; ppdi=eyJwbHQiOiJwYyIsIm1hYyI6IiIsIm1hYzEiOiIiLCJhbmRyb2lkaWQiOiIiLCJhbmRyb2lkaWQxIjoiIiwiaWRmYSI6IiIsIm9yaWdpbklwIjoiMTgzLjE1Ny44MC4yMTIifQ; PUID=e598c97fc68a47e880af-1e78d79beccc; PUID_CTM=315532800; ppi=302c3538; Hm_lvt_7adaa440f53512a144c13de93f4c22db=1492081394,1492677228,1492681036,1493283424; Hm_lpvt_7adaa440f53512a144c13de93f4c22db=1493283424; pgv_si=s8354109440");

		CloseableHttpResponse response = httpclient.execute(get);
		System.out.println(EntityUtils.toString(response.getEntity()));
	}
	
	public static String getByurl(String url) throws FileNotFoundException, IOException {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet get = new HttpGet(url);
		get.setHeader("Cookie", "pgv_pvi=8222973952; ppdi=eyJwbHQiOiJwYyIsIm1hYyI6IiIsIm1hYzEiOiIiLCJhbmRyb2lkaWQiOiIiLCJhbmRyb2lkaWQxIjoiIiwiaWRmYSI6IiIsIm9yaWdpbklwIjoiMTgzLjE1Ny44MC4yMTIifQ; PUID=e598c97fc68a47e880af-1e78d79beccc; PUID_CTM=315532800; ppi=302c3538; Hm_lvt_7adaa440f53512a144c13de93f4c22db=1492081394,1492677228,1492681036,1493283424; Hm_lpvt_7adaa440f53512a144c13de93f4c22db=1493283424; pgv_si=s8354109440");
		CloseableHttpResponse response = httpclient.execute(get);
		return EntityUtils.toString(response.getEntity());
	}


}