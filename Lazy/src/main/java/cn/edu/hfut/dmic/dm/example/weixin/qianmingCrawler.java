package cn.edu.hfut.dmic.dm.example.weixin;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.DateUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.edu.hfut.dmic.dm.example.domain.imginfo;
import cn.edu.hfut.dmic.dm.example.util.ImgDBUtil;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;

/**
 * Crawling news from hfut news
 *
 */
public class qianmingCrawler extends BreadthCrawler {

	public static Map<String, String> t = new HashMap<String, String>();
	public static ImgDBUtil dbutil = ImgDBUtil.getInstance();
	public Map<String, imginfo> imgmap = new HashMap<String, imginfo>();
	static {
		t.put("国内", "1");
		t.put("日韩", "2");
		t.put("港台", "3");
		t.put("精品", "4");
	}

	/**
	 * @param crawlPath
	 *            crawlPath is the path of the directory which maintains
	 *            information of this crawler
	 * @param autoParse
	 *            if autoParse is true,BreadthCrawler will auto extract links
	 *            which match regex rules from pag
	 */
	public qianmingCrawler(String crawlPath, boolean autoParse, int id, String year) {
		super(crawlPath, autoParse);
		String url = "http://www.qqtn.com/qm/weixinqm_" + id + ".html";
		this.addSeed(url);//
		// this.addRegex("http://www.ziyuanpian.com/detail.*");
	}

	@Override
	public void visit(Page page, CrawlDatums next) {
		if (page.matchUrl("http://www.qqtn.com/qm/weixinqm_.*html")) {
			Elements as = page.select(".g-list-dl>dt>a");
			for (int i = 0; i < as.size() - 1; i++) {
				Element a = as.get(i);
				String nexturl = a.attr("href");
				next.add("http://www.qqtn.com"+nexturl);
			}
		} else {

			if (page.matchUrl("http://www.qqtn.com/article/article_.*")) {
				Elements as = page.select("#zoom>p");
				for (int i = 0; i < as.size() - 1; i++) {
					Element a = as.get(i);
					String file = a.text();
					if(StringUtils.isBlank(file))
						return;
					method3(file);
				}
				
			}
		}

	}

	public static void execute(int pagesize) throws Exception {
		int i = pagesize;
		while (i > 0) {
			qianmingCrawler crawler = new qianmingCrawler("crawl", true, i, "2016");
			crawler.setThreads(50);
			crawler.setTopN(100);
			// crawler.setResumable(true);
			/* start crawl with depth of 4 */
			crawler.start(4);
			i--;
		}
	}

	public static void main(String[] args) throws Exception {
		int i = 6;
		while (i > 1) {
			qianmingCrawler crawler = new qianmingCrawler("crawl", true, i, "2016");
			crawler.setThreads(50);
			crawler.setTopN(100);
			// crawler.setResumable(true);
			/* start crawl with depth of 4 */
			crawler.start(4);
			i--;
		}
	}

	
	public static void method3( String content) {
		String file="D:/test/qianming.txt";
		BufferedWriter out = null;
		try {
		out = new BufferedWriter(new OutputStreamWriter(
		new FileOutputStream(file, true)));
		out.write(content+"\r\n");
		} catch (Exception e) {
		e.printStackTrace();
		} finally {
		try {
		out.close();
		} catch (IOException e) {
		e.printStackTrace();
		}
		}
		}
	public static void main1(String[] args) throws ClientProtocolException, IOException {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		String url = "http://www.ziyuanpian.com/detail/?2266.html";
		String[] names = url.split("/");
		String name = names[names.length - 1];
		System.out.println(name);
		HttpGet get = new HttpGet(url);
		// get.setHeader("Referer","http://easyplayer.site/?m=vod-detail-id-22033.html");
		CloseableHttpResponse response = httpclient.execute(get);
		String date = DateUtils.formatDate(new Date(), "yyyy-MM-dd");
		System.out.println(date);
		String path = "upload/vod/" + date + "/" + name;
		// FileUtils.writeFile(new File("/home/2kys/"+path),
		// EntityUtils.toByteArray(response.getEntity()));
		// FileUtils.writeFile(new File("d:/test.jpg"),
		// EntityUtils.toByteArray(response.getEntity()));
		System.out.println(EntityUtils.toString(response.getEntity()));

	}

}