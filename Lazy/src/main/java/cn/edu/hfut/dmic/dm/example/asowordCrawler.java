package cn.edu.hfut.dmic.dm.example;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;
import java.util.Map.Entry;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.DateUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.edu.hfut.dmic.dm.example.domain.imginfo;
import cn.edu.hfut.dmic.dm.example.domain.wordpress;
import cn.edu.hfut.dmic.dm.example.domain.wpDBUtil;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.net.HttpRequest;
import cn.edu.hfut.dmic.webcollector.net.HttpResponse;
import cn.edu.hfut.dmic.webcollector.net.Proxys;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;
import cn.edu.hfut.dmic.webcollector.util.FileUtils;

/**
 * Crawling news from hfut news
 *
 */
public class asowordCrawler extends BreadthCrawler {

	public static Map<String, String> t = new HashMap<String, String>();
	public static wpDBUtil dbutil = wpDBUtil.getInstance();
	protected HashMap<String, String> headerMap;
	protected Proxys proxys = new Proxys();
	
	static {		
		t.put("jishuzhai", "技术宅");
		t.put("znpd", "宅男");
		t.put("nvshen", "女神");
		t.put("film", "电影");
		t.put("zonghe", "资讯");
		t.put("acgzh", "ACG综合区");
		t.put("huodong", "福利活动");
		t.put("baike", "宅男百科");
		
	}

	/**
	 * @param crawlPath
	 *            crawlPath is the path of the directory which maintains
	 *            information of this crawler
	 * @param autoParse
	 *            if autoParse is true,BreadthCrawler will auto extract links
	 *            which match regex rules from pag
	 */
	public asowordCrawler(String crawlPath, boolean autoParse) {
		super(crawlPath, autoParse);
		String url = "http://aso.niaogebiji.com/rank/index?page=2";
		this.addSeed(url);//
		// this.addRegex("https://www.ziyuanpian.com/detail.*");
		
	}

	@Override
	public void visit(Page page, CrawlDatums next) {
			Elements as = page.select(".caption>p");
			for (int i = 0; i < as.size() - 1; i++) {
				Element a = as.get(i);
				if(i%3!=0){
					continue;
				}
				System.out.println(a.html());

			}

	}

	public static void execute(int pagesize) throws Exception {
		for (Map.Entry<String, String> e : t.entrySet()) {
			int i = 1;
			while (i > 1) {
				asowordCrawler crawler = new asowordCrawler("crawl", true);
				crawler.setThreads(50);
				crawler.setTopN(100);
				// crawler.setResumable(true);
				/* start crawl with depth of 4 */
				crawler.start(4);
				i--;
			}
		}
	}

	public static void main(String[] args) throws Exception {

				asowordCrawler crawler = new asowordCrawler("crawl", true);
				crawler.setThreads(50);
				crawler.setTopN(100);
				// crawler.setResumable(true);
				/* start crawl with depth of 4 */
				crawler.start(4);
	}



	public static void main1(String[] args) throws ClientProtocolException, IOException {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		String url = "https://www.ziyuanpian.com/detail/?2266.html";
		String[] names = url.split("/");
		String name = names[names.length - 1];
		System.out.println(name);
		HttpGet get = new HttpGet(url);
		// get.setHeader("Referer","https://easyplayer.site/?m=vod-detail-id-22033.html");
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

	public static void updateSite(String title, String content, String cat,String tag) {
		String xmlrpc = "http://www.fulisow.com/xmlrpc.php";
		String user = "admin";
		String passwd = ")V3otUb081qfgRg89r";
		String userid = "1";
		XmlRpcClient client = null;
		try {
			XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();

			config.setServerURL(new URL(xmlrpc));
			client = new XmlRpcClient();
			client.setConfig(config);

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 取数据更新rpc

		Vector v = new Vector();
		v.add(userid);
		v.add(user);
		v.add(passwd);
		Hashtable hashtable = new Hashtable();
		hashtable.put("title", title);
		hashtable.put("description", content);
		 Object[] categories = new Object[] { cat };// 分类
		hashtable.put("categories", categories);
		String[] tags = tag.split("\n");
		hashtable.put("mt_keywords",tags);
		hashtable.put("mt_tag",tag);

		v.add(hashtable);
		v.add("true");

		try {
			Object result = client.execute("metaWeblog.newPost", v);

		} catch (Exception e) {
			System.out.println(e);
		}

	}
	
	@Override
    public HttpResponse getResponse(CrawlDatum crawlDatum) throws Exception {
        HttpRequest request = new HttpRequest(crawlDatum);
        
        //request.addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.73 Safari/537.36");
        
        /*if (proxys != null) {
        	 Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 8888));
            request.setProxy(proxy);
        }*/
        return request.getResponse();
    }

}