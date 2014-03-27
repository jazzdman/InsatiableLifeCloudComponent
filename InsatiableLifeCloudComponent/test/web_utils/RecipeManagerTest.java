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
public class RecipeManagerTest {
    
    public RecipeManagerTest() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of run method, of class RecipeManager.
     */
    @Test
    public void testRun() {
        System.out.println("run");
        RecipeManager instance = null;
        instance.run();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of end method, of class RecipeManager.
     */
    @Test
    public void testEnd() {
        System.out.println("end");
        RecipeManager instance = null;
        instance.end();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getRecipes method, of class RecipeManager.
     */
    @Test
    public void testGetRecipes() {
        System.out.println("getRecipes");
        int calories = 0;
        int prepTime = 0;
        RecipeManager instance = null;
        ArrayList<HashMap<String, String>> expResult = null;
        ArrayList<HashMap<String, String>> result = instance.getRecipes(calories, prepTime);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of serializeRecipeList method, of class RecipeManager.
     */
    @Test
    public void testSerializeRecipeList() {
        System.out.println("serializeRecipeList");
        String directoryPath = "";
        RecipeManager instance = null;
        instance.serializeRecipeList(directoryPath);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
