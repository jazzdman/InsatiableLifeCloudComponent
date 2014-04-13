/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author jazzdman
 */
public class GroceryListManager 
{

    // The list of recipes that this class maintains to 
    private final HashMap<String, HashMap<String,String>> groceryList;
    
    private GroceryListManager() 
    {
        groceryList = new HashMap();
    }
    
    public static GroceryListManager getInstance() {
        return GroceryListManagerHolder.INSTANCE;
    }
    
    private static class GroceryListManagerHolder {

        private static final GroceryListManager INSTANCE = new GroceryListManager();
    }
    
    // This method reads in the contents of the recipeList from a file.
    private void fillGroceryList(String directoryPath)
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        Document recipeDoc;
        Element rootElement;
        NodeList groceryList, groceryChildren;
        HashMap<String,String> tempHash;
        Node recipeNode, tempNode;
        String urlValue=null;
        
        // Read in the contents from an XML file.
        try 
        {
            db = dbf.newDocumentBuilder();
            recipeDoc = db.parse(directoryPath+"WEB-INF/conf/grocerylist.xml");
            rootElement = recipeDoc.getDocumentElement();
         
        } catch (Exception e)
        {
            
        }
    }
 
    // This method saves the contents of the recipeList.
    // This method likely will only be called when the ILMenuServlet is stopped.
    public void serializeGroceryList(String directoryPath)
    {
        BufferedWriter bw;
        HashMap<String,String> recipe;
        File oldGroceryFile = new File(directoryPath+"WEB-INF/conf/grocerylist.xml");
        File newGroceryFile = new File(directoryPath+"WEB-INF/conf/grocerylist.xml");
        
      
    }
    
}
