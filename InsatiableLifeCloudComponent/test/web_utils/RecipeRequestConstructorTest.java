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
import static org.junit.Assert.*;

/**
 *
 * @author jazzdman
 */
public class RecipeRequestConstructorTest 
{
    
    private List<String> dishes, ingredients;
    private StringBuffer filePath;
    boolean setupFailed;
    
    public RecipeRequestConstructorTest() 
    {
        
        setupFailed = false;
        
    }
    
    @Before
    public void setUp() 
    {
        try
        {
            filePath = new StringBuffer();
            filePath.append(System.getProperty("user.home"));
            filePath.append("/sandbox/InsatiableLifeCloudComponent/src/conf/dishes.txt");

            dishes = Files.readAllLines(Paths.get(filePath.toString()), StandardCharsets.US_ASCII);
            filePath.delete(0, filePath.length());
            
            filePath.append(System.getProperty("user.home"));
            filePath.append("/sandbox/InsatiableLifeCloudComponent/src/conf/ingredients.txt");
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
        double rndVal1 = 0.0;
        double rndVal2 = .99;
        RecipeRequestConstructor instance;
        
        if(setupFailed)
        {
            fail("Setup failed.");
            return;
        }
         
        instance = new RecipeRequestConstructor(dishes,ingredients);
        String expResult;
        expResult = "http://www.bing.com/search?q=anchovies+ziti+site%3Aallrecipes.com";
        String result = instance.getRequest(rndVal1, rndVal2);
        assertEquals(expResult, result);
        
    }
    
    /**
     * Test of getRequest method, of class RecipeRequestConstructor.
     */
    @Test
    public void testGetRequest_negative() {
        System.out.println("getRequest negative");
        double rndVal1 = 0.0;
        double rndVal2 = .99;
        RecipeRequestConstructor instance;
        
        if(setupFailed)
        {
            fail("Setup failed.");
            return;
        }
         
        instance = new RecipeRequestConstructor(dishes,ingredients);
        String result = instance.getRequest(rndVal1, rndVal2);
        assertFalse(result == null);
        
    }
    
}
