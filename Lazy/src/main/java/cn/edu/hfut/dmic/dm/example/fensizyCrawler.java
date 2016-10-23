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
import org.jsoup.select.Elements;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;
import cn.edu.hfut.dmic.webcollector.util.FileUtils;
import cn.edu.hfut.dmic.webcollector.util.RegexRule;

/**
 * Crawling news from hfut news
 *
 * @author hu
 */
public class fensizyCrawler extends BreadthCrawler {

	public static Map<String, String> t = new HashMap<String, String>();
	public List<Vodinfo> result = new ArrayList<Vodinfo>();
	public static DBUtil dbutil =DBUtil.getInstance();
	public Map<String,Vodinfo> infomap=new HashMap<String,Vodinfo>();
	public static RegexRule curl = new RegexRule();
	static {
		t.put("电影", "1");
		t.put("电视剧", "2");
		t.put("综艺", "3");
		t.put("动漫", "4");
		t.put("动画", "4");
		t.put("动作", "5");
		t.put("喜剧", "6");
		t.put("爱情", "7");
		t.put("科幻", "8");
		t.put("恐怖", "9");
		t.put("剧情", "10");
		t.put("战争", "11");
		t.put("国产电视剧", "12");
		t.put("内地电视剧", "12");
		t.put("香港电视剧", "13");
		t.put("台湾电视剧", "13");
		t.put("日本电视剧", "14");
		t.put("韩国电视剧", "14");
		t.put("美国电视剧", "15");
		t.put("英国电视剧", "15");
		t.put("泰国电视剧", "15");
		t.put("欧美电视剧", "15");
		t.put("其它", "15");
		curl.addRule(".*\\$C.*==");
	}

	/**
	 * @param crawlPath
	 *            crawlPath is the path of the directory which maintains
	 *            information of this crawler
	 * @param autoParse
	 *            if autoParse is true,BreadthCrawler will auto extract links
	 *            which match regex rules from pag
	 */
	public fensizyCrawler(String crawlPath, boolean autoParse, int id,String year) {
		super(crawlPath, autoParse);
		/* start page */
		String url = "http://www.fensizy.com/index_zy.asp?url=&page="+id;
		this.addSeed(url);// 

		// this.addSeed("http://list.youku.com/category/show/c_100_s_1_d_1_p_"+id+".html")

		/* fetch url like http://news.hfut.edu.cn/show-xxxxxxhtml */
		// http://www.hunantv.com/v/3/150215/f/1503499.html
		// http://www.hunantv.com/v/3/102123/f/1503553.html
		this.addRegex("http://www.fensizy.com/details_zy.asp.*");
		/* do not fetch jpg|png|gif */
		// this.addRegex("-.*\\.(jpg|png|gif).*");
		/* do not fetch url contains # */
		// this.addRegex("-.*#.*");
	}

	@Override
	public void visit(Page page, CrawlDatums next) {

		if (page.matchUrl("http://www.fensizy.com/details_zy.asp.*")) {
			//if (page.matchUrl("http://www.y3600.com/hanju/2016/898.html")) {
			/* we use jsoup to parse page */
			//System.out.println(page.getHtml());
			 StringBuffer urllist = new StringBuffer();
			try {
				Vodinfo v = new Vodinfo();
				  Elements as = page.select("a[href]");
				  for (Element a : as) {
			            String text = a.childNode(0).outerHtml();
			            if (curl.satisfy(text)) {
			                urllist.append(text);
							urllist.append("#");
			            }
			        }
				  if(StringUtils.isBlank(urllist.toString())){
					  return;
				  }
				  String path = page.select(".img>img").attr("src");
				  Elements infos = page.select("html>body>table>tbody>tr>td>table>tbody>tr>td");
					  
				  		v.setTitle(infos.get(4).select("font").text());
				  		v.setActeres(infos.get(5).select("font").text());
				  		v.setDirector(infos.get(6).select("font").text());
				  		String bigtypes = infos.get(7).select("font").text();
						Iterator it = t.keySet().iterator();
						String bigtype = "";
						while (it.hasNext()) {
							String key = (String) it.next();
							if (bigtypes.indexOf(key) > -1) {
								bigtype = t.get(key);
								break;
							}
						}
						if(StringUtil.isBlank(bigtype)){
							System.out.println(bigtypes);
						}
						v.setBigtype(bigtype);
						v.setSmalltype("");
						v.setArea(infos.get(9).select("font").text());
						v.setYear(infos.get(12).select("font").text());
						v.setNeedpay(infos.get(11).select("font").text());
						
						v.setScore("7");
						v.setImg(path);
						v.setPlayer("youkuyun");
						v.setHits(1000);
					  System.out.println(infos.get(4).select("font").text());
			/*	 4  影片名称： 僵尸国度第三季 
				 5 影片演员： 迈克·韦尔奇,拉塞尔·霍奇金森,DJ·考尔斯,汤姆·艾弗瑞特·斯科特,奈特·赞
				6  影片导演： 迈克·韦尔奇
				7  影片类型： 欧美电视剧
				8  影片语言： 英语
				9  影片地区： 美国
				10  更新时间： 2016-10-23
				11  影片状态： 第6集
				12  上映日期： 2016
				13  近未来，名为ZN1的神秘病毒蔓延全球，导致成千上万的人变成嗜血如命的丧尸，而被他们咬过的人则很快受到感染成为丧尸的同类。美国政府彻底瘫痪，人类面临灭亡的厄运。在上纽约一处临时聚集点，国民警卫队负责人查尔斯·加内特（Tom Everett Scott 饰）和搭档罗伯塔·沃伦（Kellita Smith 饰）迎来了意外的访客。来者名叫马克·哈蒙德（Harold Perrineau 饰），自称是三角洲部队的中尉，他请求加内特等人协助护送名叫摩菲（Keith Allan 饰）的男子前往加州。摩菲曾先后被八只丧尸袭击，但正因为他注射了ZN1病毒抗体而安然无恙活了下来。作为人类最后的希望，临时政府要求无论如何保住摩菲的性命。 　　旅途中哈蒙德遇袭身亡，而加内特则承担起护送的重任，和身边的幸存者们踏上命运多舛的征程……
*/
				  
				  
			
				//v.setNeedpay("爱奇艺vip");
				String info = page.select(".intro").select("font").text();
				v.setDesc(info);
			
				String s_url = urllist.toString();
				s_url = s_url.substring(0, s_url.length() - 1);
				v.setUrl(s_url);

				dbutil.exesql(v);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void execute(int pagesize) throws Exception {
		int i = pagesize;
		while (i > 0) {
			fensizyCrawler crawler = new fensizyCrawler("crawl", true, i,"2016");
			crawler.setThreads(5);
			crawler.setTopN(10);
			// crawler.setResumable(true);
			/* start crawl with depth of 4 */
			crawler.start(4);
			i--;
		}
		 i = pagesize;
		while (i > 0) {
			fensizyCrawler crawler = new fensizyCrawler("crawl", true, i,"2015");
			crawler.setThreads(5);
			crawler.setTopN(10);
			// crawler.setResumable(true);
			/* start crawl with depth of 4 */
			crawler.start(4);
			i--;
		}
	}
	public static void main(String[] args) throws Exception {
		int i = 4;
		while (i > 0) {
			fensizyCrawler crawler = new fensizyCrawler("crawl", true, i,"2016");
			crawler.setThreads(50);
			crawler.setTopN(100);
			// crawler.setResumable(true);
			/* start crawl with depth of 4 */
			crawler.start(4);
			i--;
		}
	}
	public static void main1(String[] args) throws ClientProtocolException, IOException {
		 CloseableHttpClient httpclient = HttpClients.createDefault(); 
		 String url ="http://img.y3600.com/d/file/p/2016/07/26/small41dff436019e3b080941ce73da074ee9.jpg";
		 String[] names  = url.split("/");
		 String name = names[names.length-1];
		 System.out.println(name);
		 HttpGet get = new HttpGet(url);
		 get.setHeader("Referer","http://easyplayer.site/?m=vod-detail-id-22033.html");
		 CloseableHttpResponse response = httpclient.execute(get);
		 String date = DateUtils.formatDate(new Date(),"yyyy-MM-dd");
		 System.out.println(date);
		 String path = "upload/vod/"+date+"/"+name;
		//FileUtils.writeFile(new File("/home/2kys/"+path), EntityUtils.toByteArray(response.getEntity()));
		FileUtils.writeFile(new File("d:/test.jpg"), EntityUtils.toByteArray(response.getEntity()));
		 System.out.println(response.getEntity());
		
	}


}