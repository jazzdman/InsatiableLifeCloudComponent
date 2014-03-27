/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web_utils;

import java.util.ArrayList;
import java.util.HashMap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jazzdman
 */
public class AllRecipesProxyTest {
    
    public AllRecipesProxyTest() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getRecipeList method, of class AllRecipesProxy.
     */
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
     */
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
     * Test of loadRecipeWithReferer method, of class AllRecipesProxy.
     */
    @Test
    public void testLoadRecipeWithReferer_3args() {
        System.out.println("loadRecipeWithReferer");
        String url = "";
        String referer = "";
        int servings = 0;
        AllRecipesProxy instance = new AllRecipesProxy();
        HashMap<String, Object> expResult = null;
        HashMap<String, Object> result = instance.loadRecipeWithReferer(url, referer, servings);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of generateRecipe method, of class AllRecipesProxy.
     */
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

    /**
     * Test of loadRecipeWithReferer method, of class AllRecipesProxy.
     */
    @Test
    public void testLoadRecipeWithReferer_String_String() {
        System.out.println("loadRecipeWithReferer");
        String url = "";
        String referer = "";
        AllRecipesProxy instance = new AllRecipesProxy();
        HashMap<String, String> expResult = null;
        HashMap<String, String> result = instance.loadRecipeWithReferer(url, referer);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
