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
        ingredientArray.add("1 pound ground beef");
        ingredientArray.add("1/2 cup diced onion");
        ingredientArray.add("2 1/2 cups water");
        ingredientArray.add("1 cup uncooked rice");
        ingredientArray.add("1 cube beef bouillon");
        ingredientArray.add("1/2 teaspoon ground black pepper");
        ingredientArray.add("1 (14.5 ounce) can diced tomatoes");
        ingredientArray.add("1 cup diced green bell pepper");
        ingredientArray.add("1 (8 ounce) package mozzarella cheese");
    }
    
    @After
    public void tearDown() 
    {
    }

    //
    // Test of loadRecipeWithReferer method, of class AllRecipesProxy.
    //
    @Test
    public void testLoadRecipeWithReferer_3args_positive() 
    {
        BufferedReader in;
        String tmp;
        File newRecipeFile = new File(System.getProperty("user.dir") +"/test/web_utils/beef-and-rice-medley.html");
        System.out.println("loadRecipeWithReferer positive");
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
            fail("Unable to set up test testLoadRecipeWithReferer_3args:" + e.getMessage());
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
    public void testLoadRecipeWithReferer_3args_negative() 
    {
        BufferedReader in;
        String tmp;
        File newRecipeFile = new File( System.getProperty("user.dir") + "/test/web_utils/beef-and-rice-medley.html");
        System.out.println("loadRecipeWithReferer negative");
        String url = "http://allrecipes.com/recipe/beef-and-rice-medley/";
        String referer = "http://www.google.com/search?q=beef+rice+site%3Dallrecipes.com";
        int servings = 2;
        
        AllRecipesProxy instance = new AllRecipesProxy();
        
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
            fail("Unable to set up test testLoadRecipeWithReferer_3args negative." + e.getMessage());
            return;
        }
        
        HashMap<String, Object> result = instance.loadRecipeWithReferer(url, referer, servings);
        assertFalse(result.isEmpty());
        
    }
    
    //
    // Test of loadRecipeWithReferer method, of class AllRecipesProxy.
    //
    @Test
    public void testLoadRecipeWithReferer_String_String_positive() {
        System.out.println("loadRecipeWithReferer positive");
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
    
    //
    // Test of loadRecipeWithReferer method, of class AllRecipesProxy.
    //
    @Test
    public void testLoadRecipeWithReferer_String_String_negative() {
        System.out.println("loadRecipeWithReferer netative");
        String url = "http://allrecipes.com/recipe/beef-and-rice-medley/";
        String referer = "http://www.google.com/search?q=beef+rice+site%3Dallrecipes.com";
        AllRecipesProxy instance = new AllRecipesProxy();
        
        HashMap<String, String> result = instance.loadRecipeWithReferer(url, referer);
        assertFalse(result.isEmpty());
    }

    /**
     * Test of getRecipeList method, of class AllRecipesProxy.
     *
    @Test
    public void testGetRecipeList() {
        System.out.println("getRecipeList");
        AllRecipesProxy instance = new AllRecipesProxy();
        ArrayList<HashMap<String, Object>> expResult = null;
        ArrayList<HashMap<String, Object>> result = instance.getRecipeList();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of generateRecipes method, of class AllRecipesProxy.
     *
    @Test
    public void testGenerateRecipes() {
        System.out.println("generateRecipes");
        ArrayList<HashMap<String, String>> recipeHashes = null;
        int servings = 0;
        AllRecipesProxy instance = new AllRecipesProxy();
        instance.generateRecipes(recipeHashes, servings);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of generateRecipe method, of class AllRecipesProxy.
     *
    @Test
    public void testGenerateRecipe() throws Exception {
        System.out.println("generateRecipe");
        String url = "";
        String current_request_url = "";
        AllRecipesProxy instance = new AllRecipesProxy();
        HashMap<String, String> expResult = null;
        HashMap<String, String> result = instance.generateRecipe(url, current_request_url);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    */
}
