/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package common;

import java.io.BufferedWriter;
import java.io.File;
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
public class PantryManager {
    
    private PantryManager() {
    }
    
    public static PantryManager getInstance() {
        return PantryManagerHolder.INSTANCE;
    }
    
    private static class PantryManagerHolder {

        private static final PantryManager INSTANCE = new PantryManager();
    }
    
    // This method reads in the contents of the recipeList from a file.
    private void fillPantryList(String directoryPath)
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
            recipeDoc = db.parse(directoryPath+"WEB-INF/conf/pantrylist.xml");
            rootElement = recipeDoc.getDocumentElement();
         
        } catch (Exception e)
        {
            
        }
    }
 
    // This method saves the contents of the recipeList.
    // This method likely will only be called when the ILMenuServlet is stopped.
    public void serializePantryList(String directoryPath)
    {
        BufferedWriter bw;
        HashMap<String,String> recipe;
        File oldGroceryFile = new File(directoryPath+"WEB-INF/conf/pantrylist.xml");
        File newGroceryFile = new File(directoryPath+"WEB-INF/conf/pantrylist.xml");
        
      
    }
}
