/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package amazonwebcrawler;


/**
 *
 * @author hwei
 */
public class AmazonWebCrawler {

    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        System.out.println("Hello World!");
        
        Spider spider = new Spider();
        
        spider.search("http://www.tomshardware.com", "Google");
    }
    
}
