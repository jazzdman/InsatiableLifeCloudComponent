/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web_utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 *
 * @author jazzdman
 */
public class AllRecipesProxyTest {
    
    StringBuffer page;
    List<String> ingredientArray;
    
    public AllRecipesProxyTest() {
        page = new StringBuffer();
        ingredientArray = new ArrayList<>();
    }
    
    @Before
    public void setUp() 
    {
        ingredientArray.add("1/2 pound ground beef");
        ingredientArray.add("1/4 cup diced onion");
        ingredientArray.add("1-1/4 cups water");
        ingredientArray.add("1/2 cup uncooked rice");
        ingredientArray.add("1/2 cube beef bouillon");
        ingredientArray.add("1/4 teaspoon ground black pepper");
        ingredientArray.add("1/2 (14.5 ounce) can diced tomatoes");
        ingredientArray.add("1/2 cup diced green bell pepper");
        ingredientArray.add("1/2 (8 ounce) package mozzarella cheese");
    }
    
    @After
    public void tearDown() 
    {
    }

    /*
    //
    // Test of generateRecipes method, of class AllRecipesProxy.
    //
    @Test
    public void testGenerateRecipes() {
        System.out.println("generateRecipes");
        ArrayList<HashMap<String, String>> recipeHashes = new ArrayList<>();
        int servings = 2;
        AllRecipesProxy instance = new AllRecipesProxy();
        instance.generateRecipes(recipeHashes, servings);
        
    }
    */

    //
    // Test of loadRecipeWithReferer method, of class AllRecipesProxy.
    //
    @Test
    public void testLoadRecipeWithReferer_3args() 
    {
        BufferedReader in;
        String tmp;
        File newRecipeFile = new File(System.getProperty("user.home")+"/sandbox/InsatiableLifeCloudComponent/test/web_utils/beef-and-rice-medley.html");
        System.out.println("loadRecipeWithReferer");
        String url = "http://allrecipes.com/recipe/beef-and-rice-medley/";
        String referer = "http://www.google.com/search?q=beef+rice+site%3Dallrecipes.com";
        int servings = 2;
        
        AllRecipesProxy instance = new AllRecipesProxy();
        HashMap<String, Object> expResult = new HashMap<>();
        expResult.put("url", url+"?scale="+new Integer(servings).toString()+"&ismetric=0");
        expResult.put("title","Beef and Rice Medley Recipe");
        
        // Try to open the input stream from the connection.
	try 
	{
            in = new BufferedReader(
		 new FileReader(newRecipeFile));
            
            while((tmp = in.readLine()) != null)
	    {
		page.append(tmp);
	    }
	}catch (Exception e)
        {
            fail("Unable to set up test testLoadRecipeWithReferer_3args.");
            return;
        }
        expResult.put("page",page.toString());
        expResult.put("ingredients", ingredientArray);
        
        HashMap<String, Object> result = instance.loadRecipeWithReferer(url, referer, servings);
        assertEquals(expResult.get("ingredients"), result.get("ingredients"));
        
    }

    //
    // Test of loadRecipeWithReferer method, of class AllRecipesProxy.
    //
    @Test
    public void testLoadRecipeWithReferer_String_String() {
        System.out.println("loadRecipeWithReferer");
        String url = "http://allrecipes.com/recipe/beef-and-rice-medley/";
        String referer = "http://www.google.com/search?q=beef+rice+site%3Dallrecipes.com";
        AllRecipesProxy instance = new AllRecipesProxy();
        
        
        HashMap<String, String> expResult = new HashMap<>();
        expResult.put("referer", referer);
        expResult.put("url", url);
        expResult.put("title","Beef and Rice Medley Recipe");
        expResult.put("calories","702");
        expResult.put("preptime","55");
                
        
        HashMap<String, String> result = instance.loadRecipeWithReferer(url, referer);
        assertEquals(expResult, result);
    }

    
}
