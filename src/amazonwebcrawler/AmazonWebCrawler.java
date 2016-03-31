/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package amazonwebcrawler;

import java.io.FileReader;
import java.io.BufferedReader;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
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
            if (args[0].equals("readfile")) {
                String filePath = args[1];
                FileReader fr = new FileReader(filePath);
                BufferedReader br = new BufferedReader(fr);
                String aProduct = "";
                List<String> textProductList = new ArrayList<>();
                while ((aProduct = br.readLine()) != null) {
                    textProductList.add(aProduct);
                }
                listOfProducts = textProductList.toArray(listOfProducts);
                System.out.println("Reading products from file @ "+filePath);
            }
            else {
                listOfProducts = args;
                realTime = true;
            }
        }
        
        Spider spider = new Spider(realTime);               
               
        for (String tag : listOfProducts) {
            String url = spider.searchTag(tag);  
           
            Set<Map<String,Object>> products = spider.updateProductListing(url);
            
            System.setProperty("http.proxyHost", "");
            System.setProperty("http.proxyPort", "");
            
            Mongo mg = new Mongo();
            mg.addProducts(products);
        }
        
        
    }
    
}
