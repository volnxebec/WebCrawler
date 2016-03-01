/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package amazonwebcrawler;

import java.io.IOException;
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
        
        //Default search list...
        String[] listOfProducts = {"basketball", "soccer", "football"};
        
        //Check if we have any arguments...
        if (args.length > 0) {
            listOfProducts = args;
        }
        
        Spider spider = new Spider();       
        
        //String[] listOfProducts = {"basketball"};
        
        for (String tag : listOfProducts) {
            String url = spider.searchTag(tag);       
            Set<Map<String,Object>> products = spider.updateProductListing(url);
            Mongo mg = new Mongo();
            mg.addProducts(products);
        }
    }
    
}
