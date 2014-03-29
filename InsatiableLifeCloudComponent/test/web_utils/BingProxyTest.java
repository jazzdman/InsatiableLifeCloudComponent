/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web_utils;

import java.util.ArrayList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author jazzdman
 */
public class BingProxyTest
{
    
    ArrayList<String> searchResultsExpected;
    ArrayList<String> recipeURLsExpected;
    
    public BingProxyTest() 
    {
        searchResultsExpected = new ArrayList<>();
        recipeURLsExpected = new ArrayList<>();
    }
    
    @Before
    public void setUp() 
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        Document recipeDoc;
        Element rootElement;
        NodeList searchList, urlList;
        
        // Read in the contents from an XML file.
        try 
        {
            db = dbf.newDocumentBuilder();
            recipeDoc = db.parse(System.getProperty("user.home") +"sandbox/InsatiableLifeCloudComponent/test/BingProxyTestExpectedResults.xml");
            rootElement = recipeDoc.getDocumentElement();
            searchList = rootElement.getElementsByTagName("search_result");
            
            for(int i = 0; i < searchList.getLength(); i++)
            {           
                searchResultsExpected.add(searchList.item(i).getNodeValue());
            }
            
            urlList = rootElement.getElementsByTagName("recipeurl_result");
            for(int i = 0; i < urlList.getLength(); i++)
            {    
                recipeURLsExpected.add(urlList.item(i).getNodeValue());
            }
            
        } catch (Exception e)
        {
            fail("Unable to set up test."+ e.getMessage());
        }
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of findRecipes method, of class BingProxy.
     */
    @Test
    public void testFindRecipes() 
    {
        System.out.println("findRecipes");
        String searchString = "http://www.bing.com/search?q=beef+russian+site%3Aallrecipes.com";
        BingProxy instance = new BingProxy();
        instance.findRecipes(searchString);
        // TODO review the generated test code and remove the default call to fail.
        Assert.assertArrayEquals(searchResultsExpected.toArray(), instance.getSearchResults().toArray());
    }


    /**
     * Test of filterRecipes method, of class BingProxy.
     */
    @Test
    public void testFilterRecipes() {
        System.out.println("filterRecipes");
        int rndIndex = 2;
        String searchString = "http://www.bing.com/search?q=beef+russian+site%3Aallrecipes.com";
        BingProxy instance = new BingProxy();
        instance.findRecipes(searchString);
        instance.filterRecipes(rndIndex);
        // TODO review the generated test code and remove the default call to fail.
        Assert.assertArrayEquals(recipeURLsExpected.toArray(), instance.getRecipeURLs().toArray());
    }
    
}
