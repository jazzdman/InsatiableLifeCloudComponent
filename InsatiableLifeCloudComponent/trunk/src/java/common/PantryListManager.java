package common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

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
     * @param realPath
     * @throws javax.xml.parsers.ParserConfigurationException
     * @throws org.xml.sax.SAXException
     * @throws java.io.IOException
     */
    public void fillPantryList(String realPath) throws ParserConfigurationException,
                                                       SAXException,
                                                       IOException
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        Document recipeDoc;
        Element rootElement;
        String urlValue=null;
        File xmlFile = new File(realPath+"/WEB-INF/conf/pantrylist.xml");
        
        // Read in the contents from an XML file.
        if(!xmlFile.exists())
            return;
        
        db = dbf.newDocumentBuilder();
        recipeDoc = db.parse(xmlFile.getAbsolutePath());
        rootElement = recipeDoc.getDocumentElement();
         
    }
 
    /**
     * This method saves the contents of the recipeList.
     * This method likely will only be called when the ILMenuServlet is stopped.
     * 
     * @param realPath
     */
    public void serializePantryList(String realPath)
    {
        BufferedWriter bw;
        HashMap<String,String> recipe;
        File oldGroceryFile = new File(realPath +"/WEB-INF/conf/pantrylist.xml");
        File newGroceryFile = new File(realPath +"/WEB-INF/conf/pantrylist.xml");
        
      
    }
}
