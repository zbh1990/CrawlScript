package cn.edu.hfut.dmic.dm.example;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
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
public class llys extends BreadthCrawler {

	public static Map<String, String> t = new HashMap<String, String>();
	public static DBUtil dbutil = DBUtil.getInstance();
	private static String url="";
	private static String type="";
	static {
		t.put("Beautyleg","100");
		t.put("3AGirL","101");
		t.put("4K-STAR","102");
		t.put("RQ-STAR","103");
		t.put("经典写真","104");
		t.put("Rosimm","105");
		t.put("Siyamm","106");
		t.put("Ru1mm","107");
		t.put("Showgirl","108");
		t.put("Pantyhose","109");
		t.put("丽柜Ligui","110");
		t.put("细高跟","111");
		t.put("微拍福利","112");
		t.put("学院派私拍","113");
		t.put("性感车模","114");
		t.put("PANS写真","115");
		t.put("动感小站","116");
		t.put("锦尚天舞","117");
		t.put("国产私拍","118");
		t.put("国产私拍II","119");
		t.put("韩国饭拍","120");
		t.put("韩国饭拍II","121");
		t.put("韩国饭拍III","122");
		t.put("韩国MV","123");
		t.put("韩国女主播","124");
		t.put("街拍美女","125");
		t.put("街拍美女II","126");
		t.put("街拍美女III","127");
		t.put("街拍美女IV","128");
		t.put("街拍美女V","129");
		t.put("爱丝AISS","130");
		t.put("推女郎","131");
		t.put("BL时尚写真","132");
		t.put("瑜伽美女","133");
		t.put("秀人写真","134");
		t.put("Ru1mm-vip","135");
		t.put("Allure Girls","136");
		t.put("中高艺","137");
		t.put("芬妮玉足","138");
		t.put("Ugirls尤果","139");
		t.put("赤足者","140");	
	}

	/**
	 * @param crawlPath
	 *            crawlPath is the path of the directory which maintains
	 *            information of this crawler
	 * @param autoParse
	 *            if autoParse is true,BreadthCrawler will auto extract links
	 *            which match regex rules from pag
	 */
	public llys(String crawlPath, boolean autoParse, int id,String url,String type) {
		super(crawlPath, autoParse);
		/* start page */
		this.addSeed("http://v.23c.im" + url+id );// 电影
		this.url=url;
		this.type=type;
		this.addRegex("http://v.23c.im/.*");
	}

	@Override
	public void visit(Page page, CrawlDatums next) {

		if (page.matchUrl("http://v.23c.im"+url+".*")) {
			/* we use jsoup to parse page */
			try {
				Elements elements = page.select("#ha");
				for(Element element:elements){
					
					String playurl =element.attr("href");
					String tag=playurl;
					String img = element.select("#ca").get(0).attr("name");
					String title = element.attr("title");
					if(title.contains("vip")){
						return;
					}
					Vodinfo v = new Vodinfo();
					if(StringUtils.isBlank(t.get(type))){
						System.out.println(type);
					}
					v.setBigtype(t.get(type));
					v.setImg(img);
					v.setImglide(img );
					v.setPlayer("pptvyun");
					v.setYear("2016");
					v.setUrl(tag);
					v.setTitle(element.attr("title"));
					dbutil.exesql(v);
				}
				

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void execute(int pagesize) throws Exception {
		int i = pagesize;
		String url="";
		while (i > 0) {
			llys crawler = new llys("crawl", true, i,url,"");
			crawler.setThreads(50);
			crawler.setTopN(100);
			// crawler.setResumable(true);
			/* start crawl with depth of 4 */
			crawler.start(4);
			i--;
		}
	}

	public static void main1(String[] args) throws Exception {
		int i = 50;
		while (i > 0) {
			llys crawler = new llys("crawl", true, i,"","");
			crawler.setThreads(5);
			crawler.setTopN(10);
			// crawler.setResumable(true);
			/* start crawl with depth of 4 */
			crawler.start(4);
			i--;
		}
	}

	public static void main(String[] args) throws Exception {
		/*int num=241;
		String tag="f";
		for (int i = 1; i <= num; i++) {
			
		}
		System.out.println("done");*/
		String s = FileUtils.readFile(new File("D:\\llys.txt"),"utf-8");
		//System.out.println(s);
		String a[] = s.split("</a>");
		for(String t :a ){
			//System.out.println(t);
			if(t.length()<10)
				continue;
			String type =t.substring(t.indexOf("target=\"_blank\">")+"target=\"_blank\">".length());
			String url = StringUtils.substringBetween(t, "<a href=\"", "\" target=");
			int i=1;
			while (i > 0) {
				llys crawler = new llys("crawl", true, i,url,type);
				crawler.setThreads(5);
				crawler.setTopN(10);
				// crawler.setResumable(true);
				/* start crawl with depth of 4 */
				crawler.start(4);
				i--;
			}
			//System.out.println(type+" "+url);
		}

	}

}