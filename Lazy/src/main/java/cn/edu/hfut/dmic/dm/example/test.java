package cn.edu.hfut.dmic.dm.example;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;
import org.jsoup.nodes.Document;

/**
 * Crawling news from hfut news
 *
 * @author hu
 */
public class test extends BreadthCrawler {

    /**
     * @param crawlPath crawlPath is the path of the directory which maintains
     * information of this crawler
     * @param autoParse if autoParse is true,BreadthCrawler will auto extract
     * links which match regex rules from pag
     */
    public test(String crawlPath, boolean autoParse) {
    super(crawlPath, autoParse);
    /*start page*/
    this.addSeed("http://list.youku.com/category/show/c_100_a_%E6%97%A5%E6%9C%AC_s_1_d_1_p_1.html");

    /*fetch url like http://news.hfut.edu.cn/show-xxxxxxhtml*/
    this.addRegex("http://v.youku.com/v_show/id.*html");
    /*do not fetch jpg|png|gif*/
/*    this.addRegex("-.*\\.(jpg|png|gif).*");
    do not fetch url contains #
    this.addRegex("-.*#.*");*/
    }

    @Override
    public void visit(Page page, CrawlDatums next) {
    String url = page.getUrl();
    /*if page is news page*/
    if (page.matchUrl("http://v.youku.com/v_show/id.*html")) {
        /*If you want to add urls to crawl,add them to nextLink*/
        /*WebCollector automatically filters links that have been fetched before*/
        /*If autoParse is true and the link you add to nextLinks does not 
          match the regex rules,the link will also been filtered.*/
        next.add("http://www.baidu.com");
        this.setRegexRule(null);
    }
    if (page.matchUrl("http://www.baidu.com")) {
        /*we use jsoup to parse page*/
        Document doc = page.getDoc();

       System.out.println("hehe");
    }
    }

    public static void main(String[] args) throws Exception {
    	test crawler = new test("crawl", true);
    crawler.setThreads(50);
    crawler.setTopN(100);
    //crawler.setResumable(true);
    /*start crawl with depth of 4*/
    crawler.start(4);
    }

}