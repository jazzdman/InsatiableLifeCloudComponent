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
 * The purpose of this class is to assign an ID to each client that communicates
 * with the server.  This adds a level of security to the application.  The 
 * class will check and discard any IDs that haven't been seen in a year.  
 * This class is a Singleton, since there is only one list of client IDs. 
 * @author jazzdman
 */
public class ClientIDManager {
    
    // 
    public static final String START_ID = "0000001396731101020";
    public static final int CLIENT_ID_LENGTH = 19;
    
    private HashMap<String,Object> clientList = new HashMap<>();
    private BusyFlag bf;
    
    private ClientIDManager() 
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        Document recipeDoc;
        Element rootElement;
        String urlValue=null;
        NodeList tmpList;
        Node tmpNode;
        
        // Read in the contents from an XML file.
        try 
        {
            db = dbf.newDocumentBuilder();
            recipeDoc = db.parse(System.getenv("CATALINA_HOME")+"/webapps/InsatiableLifeCloudComponent/WEB-INF/conf/clientlist.xml");
            rootElement = recipeDoc.getDocumentElement();
            tmpList = rootElement.getElementsByTagName("clientID");
            
            for(int i = 0; i < tmpList.getLength(); i++)
            {
                tmpNode = tmpList.item(i);   
                clientList.put(tmpNode.getNodeValue(), new Object());
            }
        } catch (Exception e)
        {
            
        }
        
        bf = new BusyFlag();
    }
    
    // This method saves the contents of the recipeList.
    // This method likely will only be called when the ILMenuServlet is stopped.
    public void serializeClientList()
    {
        BufferedWriter bw;
        HashMap<String,String> recipe;
        File oldClientFile = new File(System.getenv("CATALINA_HOME")+"/webapps/InsatiableLifeCloudComponent/WEB-INF/conf/clientlist.xml");
        File newClientFile = new File(System.getenv("CATALINA_HOME")+"/webapps/InsatiableLifeCloudComponent/WEB-INF/conf/clientlist.xml");
        
        // Delete any file that already exists.  We want to overwrite the 
        // contents.
        if(oldClientFile.exists())
            oldClientFile.delete();
        
        // Write out the contents of the recipeList as an XML file.
        try 
        {
            bw = new BufferedWriter(new FileWriter(newClientFile));
            
            bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
            bw.write("<clients>\r\n");
        
            for(String keyOne:clientList.keySet())
            {
                bw.write("\t<clientID>");
                bw.write(keyOne);    
                bw.write("</clientID>\r\n");
            }
        
            bw.write("</clients>");
            
            bw.flush();
            bw.close();
            
        } catch (IOException ioe)
        {
        }
    }
    
    // The method that returns the static reference to this class
    public static ClientIDManager getInstance() {
        return ClientIDManagerHolder.INSTANCE;
    }
    
    // The inner class that makes this a Singleton.
    private static class ClientIDManagerHolder 
    {

        private static final ClientIDManager INSTANCE = new ClientIDManager();
    }
    
    public String createClientID()
    {
        long time;
        StringBuffer clientID;
        
        bf.getBusyFlag();
        
        time = System.currentTimeMillis();
        clientID = new StringBuffer();
        
        clientID.append(Long.toString(time));
        
        for(int i = clientID.length()+1;i < CLIENT_ID_LENGTH + 1;i++)
        {
            clientID.insert(0, "0");
        }
            
        clientList.put(clientID.toString(), new Integer(1));  
        
        bf.freeBusyFlag();
        
        return Long.toString(time);
    }
   
    public boolean validateClientID(String clientID)
    {
        boolean isValid = true;
        
        bf.getBusyFlag();
        
        // Make sure the clientID string is the right length
        isValid &= (clientID.length() == CLIENT_ID_LENGTH);
        
        // Make sure the clientID is in the right range
        isValid &= (Long.decode(clientID) > Long.decode(START_ID));
        
        // Make sure the clientID is one we have handed out
        isValid &= clientList.containsKey(clientID);
        
        bf.freeBusyFlag();
        
        return isValid;
    }
}
