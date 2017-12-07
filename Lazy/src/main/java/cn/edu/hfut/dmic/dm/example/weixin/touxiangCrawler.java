package cn.edu.hfut.dmic.dm.example.weixin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
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
public class touxiangCrawler extends BreadthCrawler {

	public static Map<String, String> t = new HashMap<String, String>();
	public static ImgDBUtil dbutil = null;//ImgDBUtil.getInstance();
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
	public touxiangCrawler(String crawlPath, boolean autoParse, int id, String year) {
		super(crawlPath, autoParse);
		String url = "http://www.qqtn.com/tx/nvshengtx_" + id + ".html";
		this.addSeed(url);//
		// this.addRegex("http://www.ziyuanpian.com/detail.*");
	}

	@Override
	public void visit(Page page, CrawlDatums next) {
		if (page.matchUrl("http://www.qqtn.com/tx/nvshengtx_.*html")) {
			Elements as = page.select(".g-piclist-cont");
			for (int i = 0; i < as.size() - 1; i++) {
				Element a = as.get(i);
				String nexturl = a.attr("href");
				next.add("http://www.qqtn.com"+nexturl);
			}
		} else {

			if (page.matchUrl("http://www.qqtn.com/article/article_.*")) {
				Elements as = page.select("#zoom>p>img");
				for (int i = 0; i < as.size() - 1; i++) {
					Element a = as.get(i);
					String file = a.attr("src");
					downloadimg(file);
				}
				
			}
		}

	}

	public static void execute(int pagesize) throws Exception {
		int i = pagesize;
		while (i > 0) {
			touxiangCrawler crawler = new touxiangCrawler("crawl", true, i, "2016");
			crawler.setThreads(50);
			crawler.setTopN(100);
			// crawler.setResumable(true);
			/* start crawl with depth of 4 */
			crawler.start(4);
			i--;
		}
	}

	public static void main1(String[] args) throws Exception {
		int i = 87;
		while (i > 1) {
			touxiangCrawler crawler = new touxiangCrawler("crawl", true, i, "2016");
			crawler.setThreads(50);
			crawler.setTopN(100);
			// crawler.setResumable(true);
			/* start crawl with depth of 4 */
			crawler.start(4);
			i--;
		}
	}

	public static void downloadimg(String url) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		String[] names = url.split("/");
		String name = names[names.length - 1];
		System.out.println(name);
		HttpGet get = new HttpGet(url);
		CloseableHttpResponse response;
		try {
			response = httpclient.execute(get);
			String path = "D:/test/";
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
	public static void main(String[] args) {
		readfile("D:\\touxiang");
	}
	
	 public static boolean readfile(String filepath)  {
	    	
	        File file = new File(filepath);
			if (!file.isDirectory()) {
			        System.out.println("文件");
			        System.out.println("path=" + file.getPath());
			        System.out.println("absolutepath=" + file.getAbsolutePath());
			        System.out.println("name=" + file.getName());
			     

			} else if (file.isDirectory()) {
                String[] filelist = file.list();
                for (int i = 0; i < filelist.length; i++) {
                        File readfile = new File(filepath + "\\" + filelist[i]);
                        if (!readfile.isDirectory()) {
                                System.out.println(readfile.getName());
                        } 
                }

        }
	        return true;
	}

}