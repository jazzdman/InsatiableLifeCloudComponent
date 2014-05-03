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
 * This class is used to control access to the grocery lists of all clients.  
 * 
 * It contains methods to save a grocery list from a client and to return a 
 * grocery list for a particular client.
 * 
 * This class also contains methods for serializing and de-serializing the 
 * grocery lists.
 * 
 * This class is a Singleton, since there is only one list of grocery lists. 
 * 
 * @author jazzdman
 */
public class GroceryListManager 
{

    /**
     * The list of recipes that this class maintains for each client
     */
    private final HashMap<String, HashMap<String,String>> groceryList;
    
    /**
     * The constructor for this class.
     */
    private GroceryListManager() 
    {
        groceryList = new HashMap();
    }
    
    /**
     * The method that returns the static reference to this class
     * @return 
     */
    public static GroceryListManager getInstance() {
        return GroceryListManagerHolder.INSTANCE;
    }
    
    /**
     * The inner class that makes this a Singleton.
     */
    private static class GroceryListManagerHolder {

        private static final GroceryListManager INSTANCE = new GroceryListManager();
    }
    
    /**
     * This method reads in the contents of the recipeList from a file.
     */
    private void fillGroceryList()
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
            recipeDoc = db.parse(System.getenv("CATALINA_HOME")+"/webapps/InsatiableLifeCloudComponent/WEB-INF/conf/grocerylist.xml");
            rootElement = recipeDoc.getDocumentElement();
         
        } catch (Exception e)
        {
            
        }
    }
 
    /**
     * This method saves the contents of the recipeList.
     * This method likely will only be called when the ILMenuServlet is stopped.
     */
    public void serializeGroceryList()
    {
        BufferedWriter bw;
        HashMap<String,String> recipe;
        File oldGroceryFile = new File(System.getenv("CATALINA_HOME")+"/webapps/InsatiableLifeCloudComponent/WEB-INF/conf/grocerylist.xml");
        File newGroceryFile = new File(System.getenv("CATALINA_HOME")+"/webapps/InsatiableLifeCloudComponent/WEB-INF/conf/grocerylist.xml");
        
      
    }
    
}
