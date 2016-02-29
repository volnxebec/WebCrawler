/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package amazonwebcrawler;

import java.util.Date;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author hwei
 */
//package com.mkyong.core;
public class Mongo {

    private DBCollection myTable;
    
    public Mongo() {
        //Constructor
        myTable = this.queryDb(null);
    }

    private DBCollection queryDb(String[] args) {
        try {

            /**** Connect to MongoDB ****/
            // Since 2.10.0, uses MongoClient
            MongoClient mongo = new MongoClient("localhost", 27017);

            /**** Get database ****/
            // if database doesn't exists, MongoDB will create it for you
            DB db = mongo.getDB("testdb");

            /**** Get collection / table from 'testdb' ****/
            // if collection doesn't exists, MongoDB will create it for you
            DBCollection table = db.getCollection("user");
            return table;
        } catch (MongoException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public void addProducts(Set<Map<String,String>> prodList) {
        for (Map<String,String> prod : prodList) {               
            this.inputData(prod);
            this.searchData(prod.get("name"));
        }
    }

    public void inputData(Map<String, String> product) {
        try{
            /**** Insert ****/
            // create a document to store key and value
            BasicDBObject document = new BasicDBObject();
            document.put("name", product.get("name"));
            document.put("url", product.get("url"));
            //document.put("tag", product.get("tag"));
            //Make tag a list...
            String tagString = product.get("tag");
            ArrayList<String> tagList = new ArrayList<String>();
            for (String eachTag:tagString.split(",")){
                eachTag = eachTag.replace("[", "");
                eachTag = eachTag.replace("]","");
                eachTag = eachTag.trim();
                tagList.add(eachTag);
            }
            document.put("tag", tagList);
            document.put("createdDate", new Date());
            removeData(product.get("name"));
            myTable.insert(document);
        } catch (MongoException e) {
            e.printStackTrace();
        }
    }
    
    public void removeData(String name) {
        try{
            BasicDBObject searchQuery = new BasicDBObject();
            searchQuery.put("name", name);

            DBCursor cursor = myTable.find(searchQuery);

            while (cursor.hasNext()) {
                myTable.remove(cursor.next());
            }
        } catch (MongoException e) {
            e.printStackTrace();
        }
    }
    
    public void searchData(String name) {
    try{
        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put("name", name);

        DBCursor cursor = myTable.find(searchQuery);

        while (cursor.hasNext()) {
                System.out.println(cursor.next());
        }

    } catch (MongoException e) {
        e.printStackTrace();
    }
  }

  public void searchAndReplaceData(DBCollection table, String[]args){
        try {
            BasicDBObject query = new BasicDBObject();
            query.put("name", "mkyong");

            BasicDBObject newDocument = new BasicDBObject();
            newDocument.put("name", "mkyong-updated");

            BasicDBObject updateObj = new BasicDBObject();
            updateObj.put("$set", newDocument);

            table.update(query, updateObj);
        } catch (MongoException e) {
            e.printStackTrace();
        }
    }

}  
