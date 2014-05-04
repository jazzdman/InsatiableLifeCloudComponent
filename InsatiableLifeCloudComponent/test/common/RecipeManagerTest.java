/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package common;

import java.io.File;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author jazzdman
 */
public class RecipeManagerTest {
    
    HashMap<String, HashMap<String,String>> testList = new HashMap<>();
    File testFile1, testFile2;
    
    public RecipeManagerTest() {
    }
    
    @Before
    public void setUp() {
         DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        Document recipeDoc;
        Element rootElement;
        NodeList rcpList, recipeChildren;
        HashMap<String,String> tempHash;
        Node recipeNode, tempNode;
        String urlValue=null;
        
        // Read in the contents from an XML file.
        try 
        {
            db = dbf.newDocumentBuilder();
            testFile1 = new File(System.getProperty("user.home")+"/sandbox/InsatiableLifeCloudComponent/build/web/WEB-INF/conf/recipelist.xml");
            recipeDoc = db.parse(System.getProperty("user.home")+"/sandbox/InsatiableLifeCloudComponent/build/web/WEB-INF/conf/recipelist.xml");
            testFile1.renameTo(new File(System.getProperty("user.home")+"/sandbox/InsatiableLifeCloudComponent/build/web/WEB-INF/conf/recipelist-test.xml"));
            
            rootElement = recipeDoc.getDocumentElement();
            rcpList = rootElement.getElementsByTagName("recipe");
            
            for(int i = 0; i < rcpList.getLength(); i++)
            {
                recipeNode = rcpList.item(i);
                recipeChildren = recipeNode.getChildNodes();
                tempHash = new HashMap();
                for(int j = 0; j < recipeChildren.getLength(); j++)
                {
                    tempNode = recipeChildren.item(j);
                    if(tempNode.getNodeName().matches("url"))
                        urlValue = tempNode.getNodeValue();
                    tempHash.put(tempNode.getNodeName(), tempNode.getNodeValue());
                }
                
                testList.put(urlValue, tempHash);
            }
        } catch (Exception e)
        {
            
        }
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of end method, of class RecipeManager.
     */
    @Test
    public void testEnd() {
        System.out.println("end");
        Thread th = null;
        RecipeManager instance = null;
        try
        {
            
            instance = new RecipeManager();
            th = new Thread(instance);
            
            th.setDaemon(true);
            th.start();
            Thread.sleep(1000);
            instance.end();
            Thread.sleep(1000);
            Assert.assertFalse(th.isAlive());
            
        } catch(Exception e)
        {
            fail("Failed to set up testEnd: "+ e.getMessage());
        }
        
    }

    /**
     * Test of fillRecipeList method, of class RecipeManager.
     */
    @Test
    public void testFillRecipeList() {
        System.out.println("fillRecipeList");
        RecipeManager instance = null;
        try
        {
            instance = new RecipeManager();
        } catch (Exception e)
        {
            fail("Failed to set up testFillRecipeList: "+ e.getMessage());
        }    
        Assert.assertEquals(testList, instance.getRecipeList() );
    }

    /**
     * Test of serializeRecipeList method, of class RecipeManager.
     */
    @Test
    public void testSerializeRecipeList() {
        System.out.println("serializeRecipeList");
        RecipeManager instance = null;
        testFile2 = new File(System.getProperty("user.home")+"/sandbox/InsatiableLifeCloudComponent/build/web/WEB-INF/conf/recipelist.xml");
        
        try
        {
            instance = new RecipeManager();
            instance.serializeRecipeList(testFile2.getAbsolutePath());
            
        } catch (Exception e)
        {
            fail("Failed to set up testSerializeReport: "+ e.getMessage());
        }   
        Assert.assertEquals(testFile1, testFile2);
    }
    
}
