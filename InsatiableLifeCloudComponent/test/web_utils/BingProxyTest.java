/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web_utils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;


/**
 *
 * @author jazzdman
 */
public class BingProxyTest
{
   
    public BingProxyTest() 
    {
    }
    
    @Before
    public void setUp() 
    {
       
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
        Assert.assertTrue(instance.getSearchResults().size() > 0 );
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
        Assert.assertTrue(instance.getRecipeURLs().size() > 0 );
    }
    
}
