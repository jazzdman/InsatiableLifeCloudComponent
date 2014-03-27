/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web_utils;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 *
 * @author jazzdman
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({web_utils.RecipeManagerTest.class, web_utils.AllRecipesProxyTest.class, web_utils.BingProxyTest.class})
public class InsatiableLifeJUnitTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }
    
}
