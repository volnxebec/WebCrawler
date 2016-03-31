/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package amazonwebcrawler;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.security.SecureRandom;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 *
 * @author hwei
 */
public class SpiderLeg
{
    // We'll use a fake USER_AGENT so the web server thinks the robot is a normal web browser.
    private static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 6.1; ";
    private List<String> links = new LinkedList<String>();
    private Document htmlDocument;
    
    private static final int LINK_THRESHOLD = 100;
    private static final int PROXY_NUM_THRESHOLD = 80;
    
    // Proxy info
    private static final String proxyListUrl = "https://free-proxy-list.net";
    private LinkedHashMap<String, String> proxyInfo;
    private List<String> proxyHostSet;
    
    private SecureRandom random = new SecureRandom();

    public String nextSessionId() {
        return new BigInteger(130, random).toString(32);
    }

    public SpiderLeg() {
        proxyInfo = new LinkedHashMap<>();       
        populateProxy();       
        proxyHostSet = new ArrayList(proxyInfo.keySet());
    }
    
    private boolean populateProxy() {
        
        try
        {
            System.setProperty("http.proxyHost", "");
            System.setProperty("http.proxyPort", "");
                
            String myUserAgent = USER_AGENT+nextSessionId()+")";
            System.out.println("Using USER_AGENT: "+myUserAgent);
            Connection connection = Jsoup.connect(proxyListUrl).userAgent(myUserAgent);
            Document htmlDocument = connection.get();
            this.htmlDocument = htmlDocument;
            if(connection.response().statusCode() == 200) // 200 is the HTTP OK status code
                                                          // indicating that everything is great.
            {
                System.out.println("\n**Visiting** Received web page at " + proxyListUrl);
            }
            if(!connection.response().contentType().contains("text/html"))
            {
                System.out.println("**Failure** Retrieved something other than HTML");
                return false;
            }
            //Elements linksOnPage = htmlDocument.select("a[href]");
            //System.out.println("Found (" + linksOnPage.size() + ") links");
            //for(Element link : linksOnPage)
            //{
            //    this.links.add(link.absUrl("href"));
            //}
            //System.out.println(htmlDocument);
            Elements proxyTable = htmlDocument.select("table[id=proxylisttable]");
            Elements proxyList = proxyTable.select("tbody");
            Elements proxyEntry = proxyList.select("tr");
            int numProxy = 0;
            for (Element proxy : proxyEntry) {
                if (numProxy == PROXY_NUM_THRESHOLD) break;
                //System.out.println(proxy);
                Elements proxyData = proxy.select("td");
                Element proxyHostData = proxyData.get(0);
                Element proxyPortData = proxyData.get(1);
                String proxyHostText = proxyHostData.text();
                String proxyPortText = proxyPortData.text();
                System.out.println("ProxyInfo: "+proxyHostText+"/"+proxyPortText);
                proxyInfo.put(proxyHostText, proxyPortText);
                numProxy++;
            }
            System.out.println("Number of Proxy obtained: "+numProxy);
            return true;
        }
        catch(IOException ioe)
        {
            // We were not successful in our HTTP request
            return false;
        }
    }
    
    /**
     * This performs all the work. It makes an HTTP request, checks the response, and then gathers
     * up all the links on the page. Perform a searchForWord after the successful crawl
     * 
     * @param url
     *            - The URL to visit
     * @return whether or not the crawl was successful
     */
    public boolean crawl(String url)
    {
        try
        {
            String myUserAgent = USER_AGENT+nextSessionId()+")";
            System.out.println("Using USER_AGENT: "+myUserAgent);
            Connection connection = Jsoup.connect(url).userAgent(myUserAgent);
            Document htmlDocument = connection.get();
            this.htmlDocument = htmlDocument;
            if(connection.response().statusCode() == 200) // 200 is the HTTP OK status code
                                                          // indicating that everything is great.
            {
                System.out.println("\n**Visiting** Received web page at " + url);
            }
            if(!connection.response().contentType().contains("text/html"))
            {
                System.out.println("**Failure** Retrieved something other than HTML");
                return false;
            }
            Elements linksOnPage = htmlDocument.select("a[href]");
            System.out.println("Found (" + linksOnPage.size() + ") links");
            for(Element link : linksOnPage)
            {
                this.links.add(link.absUrl("href"));
            }
            return true;
        }
        catch(IOException ioe)
        {
            // We were not successful in our HTTP request
            return false;
        }
    }


    /**
     * Performs a search on the body of on the HTML document that is retrieved. This method should
     * only be called after a successful crawl.
     * 
     * @param searchWord
     *            - The word or string to look for
     * @return whether or not the word was found
     */
    public boolean searchForWord(String searchWord)
    {
        // Defensive coding. This method should only be used after a successful crawl.
        if(this.htmlDocument == null)
        {
            System.out.println("ERROR! Call crawl() before performing analysis on the document");
            return false;
        }
        System.out.println("Searching for the word " + searchWord + "...");
        String bodyText = this.htmlDocument.body().text();
        return bodyText.toLowerCase().contains(searchWord.toLowerCase());
    }
    
    public List<String> getLinks()
    {
        return this.links;
    }


    public List<String> getProductLinks(String url)
    {
        List<String> productLinks = new LinkedList<String>();
        int numLink = 0;
        
        try
        {
            Connection connection;
            Elements linksOnPage = null;
            while (numLink < LINK_THRESHOLD) {
                if (proxyInfo.isEmpty()) {
                    populateProxy();                   
                }
                
                int selectHost = random.nextInt(proxyInfo.size());
                String proxyHost = proxyHostSet.get(selectHost);
          
                System.setProperty("http.proxyHost", proxyHost);
                System.setProperty("http.proxyPort", proxyInfo.get(proxyHost));
                
                String curHost = System.getProperty("http.proxyHost");
                String curPort = System.getProperty("http.proxyPort");        
                System.out.println("Using Proxy: "+curHost+"/"+curPort);
                
                proxyInfo.remove(proxyHost);
                proxyHostSet.remove(proxyHost);
                System.out.println(proxyInfo.size());
                
                String myUserAgent = USER_AGENT+nextSessionId()+")";
                connection = Jsoup.connect(url).userAgent(myUserAgent);
                System.out.println("Using USER_AGENT: "+myUserAgent);
                Document htmlDocument = connection.get();
                this.htmlDocument = htmlDocument;
                if(connection.response().statusCode() == 200) // 200 is the HTTP OK status code
                                                              // indicating that everything is great.
                {
                    System.out.println("\n**Visiting** Received web page at " + url);
                }
                if(!connection.response().contentType().contains("text/html"))
                {
                    System.out.println("**Failure** Retrieved something other than HTML");
                    //return false;
                }
                linksOnPage = htmlDocument.select("a[href]");
                numLink = linksOnPage.size();
                System.out.println("Found (" + numLink + ") links");
            }
            for(Element link : linksOnPage)
            {
                
                String actualLink = link.attr("abs:href");
                //System.out.println(link);
                if (actualLink.matches("(.*)customerReviews(.*)")) {
                    //System.out.println(actualLink);
                    productLinks.add(link.absUrl("href"));
                }
            }
            //return true;
        }
        catch(IOException ioe)
        {
            // We were not successful in our HTTP request
            //return false;
        }
        
        
        return productLinks;
    }
    
    public Map<String, Object> getProductInfo(String url, List<String> extraLinks){
        
        List<String> tagLinks = new LinkedList<String>();
        List<String> reviewLinks = new LinkedList<String>();
        Map<String, Object> prodInfo = new HashMap<>();
        int numLink = 0;
        
        
        try
        {
            Connection connection;
            Elements linksOnPage = null;
            while (numLink < LINK_THRESHOLD) {
                if (proxyInfo.isEmpty()) {
                    populateProxy();                   
                }
                
                int selectHost = random.nextInt(proxyInfo.size());
                String proxyHost = proxyHostSet.get(selectHost);
          
                System.setProperty("http.proxyHost", proxyHost);
                System.setProperty("http.proxyPort", proxyInfo.get(proxyHost));
                
                String curHost = System.getProperty("http.proxyHost");
                String curPort = System.getProperty("http.proxyPort");        
                System.out.println("Using Proxy: "+curHost+"/"+curPort);
                
                proxyInfo.remove(proxyHost);
                proxyHostSet.remove(proxyHost);
                System.out.println(proxyInfo.size());
                
                String myUserAgent = USER_AGENT+nextSessionId()+")";
                connection = Jsoup.connect(url).userAgent(myUserAgent);
                System.out.println("Using USER_AGENT: "+myUserAgent);
                
                Document htmlDocument = connection.get();
                this.htmlDocument = htmlDocument;
                if(connection.response().statusCode() == 200) // 200 is the HTTP OK status code
                                                              // indicating that everything is great.
                {
                    System.out.println("\n**Visiting** Received web page at " + url);
                }
                if(!connection.response().contentType().contains("text/html"))
                {
                    System.out.println("**Failure** Retrieved something other than HTML");
                    //return false;
                }
                linksOnPage = htmlDocument.select("a[href]");
                numLink = linksOnPage.size();
                System.out.println("Found (" + numLink + ") links");
            }

            //Put in the product name
            Elements getName = htmlDocument.select("span[id=productTitle]");
            String myName = getName.text();
            //System.out.println(myName);
            prodInfo.put("name", myName);
            
            //Put in the product rating
            Elements getRating = htmlDocument.select("span[class=reviewCountTextLinkedHistogram noUnderline]");
            String myRating = getRating.attr("title");
            double myNumRate = 0.0;
            if (!myRating.equals("")) {
                myNumRate = this.parseRating(myRating);            
                //System.out.println(myNumRate);
            }
            prodInfo.put("rating", myNumRate);
            
            //Bad product...
            if (myName.equals("")) {
                return null;
            }
            
            //Put in the product price
            Elements getPrice = htmlDocument.select("span[id=priceblock_ourprice]");
            String myPrice = getPrice.text();
            //System.out.println(myPrice);
            prodInfo.put("price", myPrice);
            
            //Put in the product url
            Elements getUrl = htmlDocument.select("link[rel=canonical]");
            String myUrl = getUrl.attr("href");
            //System.out.println(myUrl);
            prodInfo.put("url", myUrl);
            
            //Get product ID from URL
            String[] idList = myUrl.split("/");
            String myID = idList[idList.length-1];
            prodInfo.put("ID", myID);
            
            //Get customer reviews in a list
            Elements getCustomerRev = htmlDocument.select("div[id=cm_cr_dpcmps]");
            //System.out.println(getCustomerRev);
            Elements parseMktRev = getCustomerRev.select("div[class=a-section celwidget");
            //System.out.println(parseMktRev);
            for (Element line : parseMktRev) {
                //System.out.println(line);
                //Get the star rating
                Elements starsLink = line.select("a[class=a-link-normal a-text-normal]");
                String starsRev = starsLink.attr("title");
                double myStarRate = 0.0;
                if (!starsRev.equals("")) {
                    myStarRate = this.parseRating(starsRev);
                    //prodInfo.put("rating", myStarRate);
                    //System.out.println(myStarRate);
                }
                //System.out.println(starsRev);
                //Get the title
                Elements titleLink = line.select("span[class=a-size-base a-text-bold]");
                String titleRev = titleLink.text();
                //System.out.println(titleRev);
                //Get the content
                Elements contentLink = line.select("div[class=a-section]");
                String contentRev = contentLink.text();
                //System.out.println(contentRev);
                
                //Append them all together with new line character
                String wholeRev = myStarRate+"\n"+titleRev+"\n"+contentRev;
                //System.out.println(wholeRev);
                reviewLinks.add(wholeRev);
            }
            //System.out.println(reviewLinks);
            //String reviewString = reviewLinks.toString();
            prodInfo.put("review", reviewLinks);
            
            if (extraLinks != null) {
                //Get extra links
                Elements alsoBuyText = htmlDocument.select("div[id=purchase-sims-feature]");
                //System.out.println(alsoBuyText);
                Elements alsoBuyLinks = alsoBuyText.select("a[href]");
                //System.out.println(alsoBuyLinks);
                for(Element link : alsoBuyLinks)
                {

                    String actualLink = link.attr("abs:href");
                    //System.out.println(actualLink);
                    if (!actualLink.matches("(.*)product-reviews(.*)")) {
                        //Some parsing hacks
                        String[] parseLink = actualLink.split("ref=");
                        String finalLink = parseLink[0];
                        finalLink = finalLink.substring(0, finalLink.length()-1);
                        if (!finalLink.equals(myUrl)) {
                            //System.out.println(finalLink);
                            //add this extra link as long as we don't have it yet
                            if (!extraLinks.contains(finalLink)) {
                                extraLinks.add(finalLink);
                            }
                        }
                        //productLinks.add(link.absUrl("href"));
                    }
                }
            }
            
            //Put in the product tags
            Elements getTags = htmlDocument.select("div#wayfinding-breadcrumbs_container");
            Elements myTags = getTags.select("a[href]");
            for (Element tag : myTags) {
                tagLinks.add(tag.text());                   
            }  
            //No tags found... return null
            if (tagLinks.isEmpty()) {
                return null;
            }
            //String tagString = tagLinks.toString();
            prodInfo.put("tag", tagLinks);
            //return true;
        }
        catch(IOException ioe)
        {
            // We were not successful in our HTTP request
            //return false;
        }
        
        return prodInfo;
    }
    
    private double parseRating(String strRate) {
        String[] strList = strRate.split(" ");
        double numRate = Double.parseDouble(strList[0]);
        return numRate;
    }


}
