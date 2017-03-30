package cn.edu.hfut.dmic.dm.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
public class aitaotuCrawler2 extends BreadthCrawler {

	public static Map<String, String> t = new HashMap<String, String>();
	public static ImgDBUtil dbutil = ImgDBUtil.getInstance();
	public Map<String, imginfo> imgmap = new HashMap<String, imginfo>();
	static {
		t.put("国内", "1");
		t.put("日韩", "2");
		t.put("港台", "3");
	}

	/**
	 * @param crawlPath
	 *            crawlPath is the path of the directory which maintains
	 *            information of this crawler
	 * @param autoParse
	 *            if autoParse is true,BreadthCrawler will auto extract links
	 *            which match regex rules from pag
	 */
	public aitaotuCrawler2(String crawlPath, boolean autoParse, int id, String year) {
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
			aitaotuCrawler2 crawler = new aitaotuCrawler2("crawl", true, i, "2016");
			crawler.setThreads(50);
			crawler.setTopN(100);
			// crawler.setResumable(true);
			/* start crawl with depth of 4 */
			crawler.start(4);
			i--;
		}
	}

	public static void main1(String[] args) throws Exception {
		int i = 300;
		while (i > 1) {
			aitaotuCrawler2 crawler = new aitaotuCrawler2("crawl", true, i, "2016");
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
	public static void main(String[] args) throws  Exception {
		String s = FileUtils.readFile(new File("D:\\aitutu.txt"),"utf-8");
    	String []a = s.split("\\[");
    	List infos = new ArrayList();
    	for(String t :a ){
    		String[] tt = t.split("	");
    		Title info=new Title();
    		info.setTitle("["+tt[0]);
    		info.setTag(tt[1]);
    		infos.add(info);
    	}
    	
		readfile("D:\\xampp\\htdocs\\tucms\\attach",infos);
	}

	
    public static boolean readfile(String filepath,List temp) throws Exception {
    	
        try {

                File file = new File(filepath);
                if (!file.isDirectory()) {
                        System.out.println("文件");
                        System.out.println("path=" + file.getPath());
                        System.out.println("absolutepath=" + file.getAbsolutePath());
                        System.out.println("name=" + file.getName());
                     

                } else if (file.isDirectory()) {
                        System.out.println("文件夹");
                        String[] filelist = file.list();
                        for (int i = 0; i < filelist.length; i++) {
                                File readfile = new File(filepath + "\\" + filelist[i]);
                                if (!readfile.isDirectory()) {
                                        System.out.println("path=" + readfile.getPath());
                                        System.out.println("absolutepath="
                                                        + readfile.getAbsolutePath());
                                        System.out.println("name=" + readfile.getName());
                                        
                                    	imgattach imgattach=new imgattach();
                        				
                        				String[] ids = filepath.split("\\\\"); 
                        				String imgpath ="http://120.26.0.218:9999/vod/"+ids[ids.length-1]+"/"+filelist[i];
                        				imgattach.setArticle_id(ids[ids.length-1]);
                        				imgattach.setFile(imgpath);
                        				
                        				String id = ids[ids.length-1];
                                        int l =temp.size();
                                        l = new Random().nextInt(l);
                                        Title info=  (Title) temp.get(l);
                                    	imginfo imginfo=new imginfo();
                                    	 l = new Random().nextInt(3)+1;
                        				imginfo.setCid(l+"");
                        				imginfo.setTitle(info.getTitle());
                        				imginfo.setId(id);
                        				imginfo.setTag(info.getTag());
                        				imginfo.setCover(imgpath);
                        				dbutil.exesql(imginfo.toString());
                        				
                        				dbutil.exesql(imgattach.toString());
                                        

                                } else if (readfile.isDirectory()) {
                                        readfile(filepath + "\\" + filelist[i],temp);
                                        
                                        
                                        String id =filelist[i];
                                        int l =temp.size();
                                        l = Math.round(l-1);
                                        Title info=  (Title) temp.get(l);
                                    	imginfo imginfo=new imginfo();
                        				imginfo.setCid("3");
                        				imginfo.setTitle(info.getTitle());
                        				imginfo.setId(id);
                        				imginfo.setTag(info.getTag());
                        				//imginfo.setCover(cover);
                        				//dbutil.exesql(imginfo.toString());
                                }
                        }

                }

        } catch (FileNotFoundException e) {
                System.out.println("readfile()   Exception:" + e.getMessage());
        }
        return true;
}
}