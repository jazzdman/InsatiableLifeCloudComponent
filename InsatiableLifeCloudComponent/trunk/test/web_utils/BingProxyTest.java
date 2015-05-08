/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web_utils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


/**
 *
 * @author jazzdman
 */
public class BingProxyTest
{
   
    private List<String> dishes, ingredients;
    private StringBuffer filePath;
    boolean setupFailed;
    double rndVal1;
    double rndVal2;
    
    public BingProxyTest() 
    {
        setupFailed = false;
        rndVal1 = 0.99;
        rndVal2 = 0.0;
    }
    
    @Before
    public void setUp() 
    {
       try
        {
            filePath = new StringBuffer();
            filePath.append(System.getProperty("basedir"));
            filePath.append("/src/conf/dishes.txt");

            dishes = Files.readAllLines(Paths.get(filePath.toString()), StandardCharsets.US_ASCII);
            filePath.delete(0, filePath.length());
            
            filePath.append(System.getProperty("basedir"));
            filePath.append("/src/conf/ingredients.txt");
            ingredients = Files.readAllLines(Paths.get(filePath.toString()), StandardCharsets.US_ASCII);
            
        } catch(Exception e)
        {
            setupFailed = true;
        }
    }
    
    @After
    public void tearDown() {
    }
    
     /**
     * Test of getRequest method, of class RecipeRequestConstructor.
     */
    @Test
    public void testGetRequest_positive() {
        System.out.println("getRequest positive");
        BingProxy instance;
        
        if(setupFailed)
        {
            fail("Setup failed.");
            return;
        }
         
        instance = new BingProxy(dishes,ingredients);
        String expResult;
        expResult = "http://www.bing.com/search?q=veal+bake+site%3Aallrecipes.com";
        String result = instance.getRequest(rndVal1, rndVal2);
        assertEquals(expResult, result);
        
    }
    
    /**
     * Test of getRequest method, of class RecipeRequestConstructor.
     */
    @Test
    public void testGetRequest_negative() {
        System.out.println("getRequest negative");
        BingProxy instance;
        
        if(setupFailed)
        {
            fail("Setup failed.");
            return;
        }
         
        instance = new BingProxy(dishes,ingredients);
        String result = instance.getRequest(rndVal1, rndVal2);
        assertFalse(result == null);
        
    }
    
    

    /**
     * Test of findRecipes method, of class BingProxy.
     */
    @Test
    public void testFindRecipes() 
    {
        System.out.println("findRecipes");
        BingProxy instance = new BingProxy(dishes,ingredients);
        
        if(setupFailed)
        {
            fail("Setup failed.");
            return;
        }
        
        if(instance == null)
        {
            fail("Instance is null.");
            return;
        }
        
        instance.findRecipes(rndVal1, rndVal2);
        // TODO review the generated test code and remove the default call to fail.
        assertTrue(new Integer(instance.getSearchResults().size()).toString() , instance.getSearchResults().size() > 0 );
    }


    /**
     * Test of filterRecipes method, of class BingProxy.
     */
    @Test
    public void testFilterRecipes() {
        System.out.println("filterRecipes");
        int rndIndex = 2;
        BingProxy instance = new BingProxy(dishes,ingredients);
        
        
         if(setupFailed)
        {
            fail("Setup failed.");
            return;
        }
        
        if(instance == null)
        {
            fail("Instance is null.");
            return;
        }
        
        instance.findRecipes(rndVal1, rndVal2);
        instance.filterRecipes(rndIndex);
        // TODO review the generated test code and remove the default call to fail.
        assertTrue(new Integer(instance.getRecipeURLs().size()).toString() ,
                   instance.getRecipeURLs().size() > 0 );
    }
    
}
