/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package amazonwebcrawler;

import java.io.IOException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author hwei
 */
public class AmazonWebCrawler {

    private static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        System.out.println("Hello World!");
        
        Spider spider = new Spider();
        SpiderLeg leg = new SpiderLeg();
        //
        
        String tag = "basketball";
        String url = spider.searchTag(tag);
        
        leg.getProductLinks(url);
        
        //Connection connection = Jsoup.connect(url).userAgent(USER_AGENT).timeout(5000);
        //Document htmlDocument = connection.get();   
     
        
        //System.out.println(htmlDocument);
        
        //Elements links = htmlDocument.select("a[href]");
        
        //for (Element link : links) {
        //    System.out.println(link);
       // 
        //}
        
        //
        
        //spider.search("http://www.tomshardware.com", "Google");
    }
    
}
