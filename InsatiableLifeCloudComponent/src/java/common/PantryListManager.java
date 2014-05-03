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
 * This class is used to control access to the pantries of all clients.
 * 
 * It contains one unique method to update the contents of pantry for a 
 * particular client.
 * 
 * This class also contains methods for serializing and de-serializing the 
 * pantries.
 * 
 * This class is a Singleton, since there is only one list of pantry contents. 
 * 
 * @author jazzdman
 */
public class PantryListManager {
    
    /**
     * The constructor for this class.
     */
    private PantryListManager() {
    }
    
    /**
     * The method that returns the static reference to this class.
     * @return 
     */
    public static PantryListManager getInstance() {
        return PantryListManagerHolder.INSTANCE;
    }
    
    /**
     * The inner class that makes this a Singleton.
     */
    private static class PantryListManagerHolder {

        private static final PantryListManager INSTANCE = new PantryListManager();
    }
    
    /**
     * This method reads in the contents of the recipeList from a file.
     */
    private void fillPantryList()
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
            recipeDoc = db.parse(System.getenv("CATALINA_HOME")+"/webapps/InsatiableLifeCloudComponent/WEB-INF/conf/pantrylist.xml");
            rootElement = recipeDoc.getDocumentElement();
         
        } catch (Exception e)
        {
            
        }
    }
 
    /**
     * This method saves the contents of the recipeList.
     * This method likely will only be called when the ILMenuServlet is stopped.
     */
    public void serializePantryList()
    {
        BufferedWriter bw;
        HashMap<String,String> recipe;
        File oldGroceryFile = new File(System.getenv("CATALINA_HOME")+"/webapps/InsatiableLifeCloudComponent/WEB-INF/conf/pantrylist.xml");
        File newGroceryFile = new File(System.getenv("CATALINA_HOME")+"/webapps/InsatiableLifeCloudComponent/WEB-INF/conf/pantrylist.xml");
        
      
    }
}
