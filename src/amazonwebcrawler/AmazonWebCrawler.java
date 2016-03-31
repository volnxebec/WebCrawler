/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package amazonwebcrawler;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author hwei
 */
public class AmazonWebCrawler {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        System.out.println("Hello World!");
        
        //Default search list...
        //String[] listOfProducts = {"basketball", "soccer", "football"};
        boolean realTime = true;
        String[] listOfProducts = {"basketball"};
        
        //Check if we have any arguments...
        if (args.length > 0) {
            listOfProducts = args;
            realTime = true;
        }
        
        Spider spider = new Spider(realTime);               
 
        
        
        for (String tag : listOfProducts) {
            String url = spider.searchTag(tag);  
           
            Set<Map<String,Object>> products = spider.updateProductListing(url);
            
            Mongo mg = new Mongo();
            mg.addProducts(products);
        }
        
    }
    
}
