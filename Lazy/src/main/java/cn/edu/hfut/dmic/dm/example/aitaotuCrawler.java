package cn.edu.hfut.dmic.dm.example;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.DateUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.edu.hfut.dmic.dm.example.domain.imgattach;
import cn.edu.hfut.dmic.dm.example.domain.imginfo;
import cn.edu.hfut.dmic.dm.example.util.ImgDBUtil;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;
import cn.edu.hfut.dmic.webcollector.util.FileUtils;

/**
 * Crawling news from hfut news
 *
 */
public class aitaotuCrawler extends BreadthCrawler {

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
	public aitaotuCrawler(String crawlPath, boolean autoParse, int id, String year) {
		super(crawlPath, autoParse);
		String url = "http://aitaotu.92game.net/guonei/index_" + id + ".html";
		this.addSeed(url);//
		// this.addRegex("http://www.ziyuanpian.com/detail.*");
	}

	@Override
	public void visit(Page page, CrawlDatums next) {
		if (page.matchUrl("http://aitaotu.92game.net/guonei/index.*html")) {
			Elements as = page.select(".item.masonry_brick");
			for (int i = 0; i < as.size() - 1; i++) {
				Element a = as.get(i);
				String nexturl = "http://aitaotu.92game.net" + a.select(".img>a").get(0).attr("href");
				String id = nexturl.substring(nexturl.lastIndexOf("guonei") + 7, nexturl.lastIndexOf(".html"));
				
				Element t = a.select(".img>a>img").get(0);
				String title = t.attr("title");
				Elements tags = a.select(".blue");
				String cover = t.attr("data-original");
				String tag = "";
				for (Element temp : tags) {
					tag += temp.text() + ",";
				}
				imginfo imginfo=new imginfo();
				imginfo.setCid("3");
				imginfo.setTitle(title);
				imginfo.setId(id);
				imginfo.setTag(tag);
				imginfo.setCover(cover);
				imgmap.put(id, imginfo);
				String like = a.select(".items_likes").text();
				int total = Integer.parseInt(like.substring(like.indexOf("共") + 1, like.indexOf("张")));
				next.add(nexturl);
				
				downloadimg(cover, id);
				for (int c = 2; c < total; c++) {
					next.add("http://aitaotu.92game.net/guonei/" + id + "_" + c + ".html");
				}
				dbutil.exesql(imginfo.toString());
			}
		} else {

			if (page.matchUrl("http://aitaotu.92game.net/guonei/.*")) {
				String url = page.getUrl();
				String id = "";
				if (url.indexOf("_") > -1) {
					id = url.substring(url.lastIndexOf("guonei") + 7, url.lastIndexOf("_"));
				} else {
					id = url.substring(url.lastIndexOf("guonei") + 7, url.lastIndexOf(".html"));
				}
				imgattach imgattach=new imgattach();
				String file =page.select("#big-pic>p>a>img").get(0).attr("src");
				imgattach.setArticle_id(id);
				imgattach.setFile(file);
				imginfo imginfo=imgmap.get(id);
				imginfo.getImgattachlist().add(imgattach);
				dbutil.exesql(imgattach.toString());
				//downloadimg(file, id);
			}
		}

	}

	public static void execute(int pagesize) throws Exception {
		int i = pagesize;
		while (i > 0) {
			aitaotuCrawler crawler = new aitaotuCrawler("crawl", true, i, "2016");
			crawler.setThreads(50);
			crawler.setTopN(100);
			// crawler.setResumable(true);
			/* start crawl with depth of 4 */
			crawler.start(4);
			i--;
		}
	}

	public static void main(String[] args) throws Exception {
		int i = 300;
		while (i > 1) {
			aitaotuCrawler crawler = new aitaotuCrawler("crawl", true, i, "2016");
			crawler.setThreads(50);
			crawler.setTopN(100);
			// crawler.setResumable(true);
			/* start crawl with depth of 4 */
			crawler.start(4);
			i--;
		}
	}

	public static void downloadimg(String url,String id) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		String[] names = url.split("/");
		String name = names[names.length - 1];
		System.out.println(name);
		HttpGet get = new HttpGet(url);
		CloseableHttpResponse response;
		try {
			response = httpclient.execute(get);
			String path = "/home/upload/vod/" +id+"/";
			long filelength = response.getEntity().getContentLength();
			if(filelength<=28){
				return ;
			}
			FileUtils.writeFileWithParent(new File(path+name),
			 EntityUtils.toByteArray(response.getEntity()));
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//System.out.println(EntityUtils.toString(response.getEntity()));

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