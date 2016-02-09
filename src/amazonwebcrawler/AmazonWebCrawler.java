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

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import java.util.Map;
import java.util.Set;

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
        
        String tag = "basketball";
        String url = spider.searchTag(tag);
        
        Set<Map<String,String>> products = spider.updateProductListing(url);
        
        Mongo mg = new Mongo();
        DBCollection tb = mg.queryDb(null);
        
        for (Map<String,String> prod : products) {               
            mg.inputData(tb, prod);
            mg.searchData(tb, prod.get("name"));
        }
    }
    
}
