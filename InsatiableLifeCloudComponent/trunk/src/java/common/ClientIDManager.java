package common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 *
 * The purpose of this class is to assign an ID to each client that communicates
 * with the server.  This adds a level of security to the application.  The 
 * class will check and discard any IDs that haven't been seen in a year.  
 * This class is a Singleton, since there is only one list of client IDs. 
 * @author jazzdman
 */
public class ClientIDManager {
    
    /**
     * A client ID is actually a time stamp, number of milliseconds since 
     * January 1, 1970.  This number is the time that this class was first
     * written.  As a security feature, All IDs must be later than this number.
     */ 
    public static final String START_ID = "1396731101020";
    
    /**
     * System.currentTimeMillis() returns a long.  The maximum number of digits
     * in a long is 19.  As an added security feature, the client ID must
     * be padded with zeros to make a string 19 chars long.
     */
    public static final int CLIENT_ID_LENGTH = 19;
    
    /**
     * This {@link HashMap} holds all of the clients that have accessed the web app
     * The key is the ID for the client.  The value is the Client ID object
     * that describes that client.
     */
    private HashMap<String,ClientID> clientList;
    
    /**
     * Since this is a singleton class, but will be accessed many times in many
     * places, we need to make sure that its operations are thread safe.  This
     * busy flag ensures that.
     */
    private final BusyFlag bf;
    
    /**
     * The constructor for this class by creating the client list and populate the
     * list.  This client list is not likely to ever need to be populated from
     * file.  However, if the application is stopped, then serializeClientList
     * should be called.
     */
    private ClientIDManager() 
    {
       
        clientList = new HashMap<>();   
        bf = new BusyFlag();
        
    }
    
    
    /**
     * This method fills the recipeList from clientlist.xml, if it exists.
     * This method likely will only be called when the ILMenuServlet is stopped.
     * @param realPath
     * @throws javax.xml.parsers.ParserConfigurationException
     * @throws org.xml.sax.SAXException
     * @throws java.io.IOException
     */
    public void fillClientList(String realPath) throws ParserConfigurationException, 
                                                       SAXException, 
                                                       IOException
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        Document recipeDoc;
        Element rootElement;
        NodeList tmpList;
        Element tmpElement;
        File xmlFile = new File(realPath+"/WEB-INF/conf/clientlist.xml");
        
        // Read in the contents from an XML file.
        if(!xmlFile.exists())
            return;
        
        db = dbf.newDocumentBuilder();
        recipeDoc = db.parse(xmlFile.getAbsolutePath());
        rootElement = recipeDoc.getDocumentElement();
        tmpList = rootElement.getElementsByTagName("clientID");
            
        for(int i = 0; i < tmpList.getLength(); i++)
        {
            tmpElement = (Element)tmpList.item(i);   
            clientList.put(tmpElement.getElementsByTagName("clientID").item(0).getFirstChild().getNodeValue(), 
                           new ClientID(tmpElement));
        }
    }
    
    /**
     * This method saves the contents of the recipeList.
     * This method likely will only be called when the ILMenuServlet is stopped.
     * @param realPath
     * @throws java.io.IOException
     */
    public void serializeClientList(String realPath) throws IOException
    {
        ClientID tmpID;
        BufferedWriter bw;
        HashMap<String,String> recipe;
        File oldClientFile = new File(realPath+"/WEB-INF/conf/clientlist.xml");
        File newClientFile = new File(realPath+"/WEB-INF/conf/clientlist.xml");
        
        // Delete any file that already exists.  We want to overwrite the 
        // contents.
        if(oldClientFile.exists())
            oldClientFile.delete();
        
        // Write out the contents of the clientList as an XML file.
        bw = new BufferedWriter(new FileWriter(newClientFile));
            
        bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
        bw.write("<clients>\r\n");
        
        for(String keyOne:clientList.keySet())
        {
            tmpID = clientList.get(keyOne);
            tmpID.serialize(bw);
        }
        
        bw.write("\r\n</clients>");
            
        bw.flush();
        bw.close();
            
    }
    
    /**
     * 
     * Get the {@link ClientID} associated with String ID.
     * 
     * @param ID 
     * @return clientID associated with String ID
     */
    public ClientID getClientID(String ID)
    {
        return clientList.get(ID);
    }
    
    /**
     * The method that returns the static reference to this class
     * @return 
     */
    public static ClientIDManager getInstance() {
        return ClientIDManagerHolder.INSTANCE;
    }
    
    /**
     * The inner class that makes this a Singleton.
     */
    private static class ClientIDManagerHolder 
    {

        private static final ClientIDManager INSTANCE = new ClientIDManager();
    }
    
    /**
     * Creates a client ID for the caller.  
     * 
     * @return the String associated with the created ClientID.
     */
    public String createClientID()
    {
        long time;
        StringBuffer clientIDSB;
        Document doc;
        Element rootElement;
        Element idNode, requestNode;
        bf.getBusyFlag();
        
        // We save a clientID as an XML node.  So, we create them as such.
        // If we can't create an XML document to start with, then the clientID
        // doesn't get created.
        try
        {    
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        }
        catch(ParserConfigurationException e)
        {
            return null;
        }
            
        // Create the ClientID node and its children.
        rootElement = doc.createElement("clientID");
        idNode = doc.createElement("ID");
        // Create the clientID itself and buffer it with zeros
        time = System.currentTimeMillis();
        clientIDSB = new StringBuffer();
        clientIDSB.append(Long.toString(time));
        for(int i = clientIDSB.length()+1;i < CLIENT_ID_LENGTH + 1;i++)
        {
            clientIDSB.insert(0, "0");
        }
        idNode.appendChild(doc.createTextNode(clientIDSB.toString()));
        rootElement.appendChild(idNode);
        
        requestNode = doc.createElement("request");
        requestNode.appendChild(doc.createTextNode(new Long(time).toString()));
        rootElement.appendChild(requestNode);
       
        // Save the clientID to our list of IDs
        clientList.put(clientIDSB.toString(), new ClientID(rootElement));  
        
        bf.freeBusyFlag();
        
        return Long.toString(time);
    }
   
    /**
     * Check to see if the client ID is valid according to the following rules:
     * 
     * 1)  If the client ID is the correct length
     * 2)  If the client ID is large enough number
     * 3)  If the client ID is in the list of IDs we've created so far
     * 
     * @param clientID
     * @return whether or not a client ID is valid.
     */
    public boolean validateClientID(String clientID)
    {
        StringBuilder idBuf = new StringBuilder();
        boolean isValid = true;
        
        bf.getBusyFlag();
        
        // Apparently Long.decode can't handle the leading zeroes.  So, we 
        // need to remove them.
        idBuf.append(clientID);
        while(idBuf.charAt(0) == '0')
        {
            idBuf.deleteCharAt(0);
        }
        
        // Make sure the clientID string is the right length
        isValid &= (clientID.length() == CLIENT_ID_LENGTH);
        
        // Make sure the clientID is in the right range
        isValid &= (Long.decode(idBuf.toString()) > Long.decode(START_ID));
        
        // Make sure the clientID is one we have handed out
        isValid &= clientList.containsKey(clientID);
        
        // Update the number of requests
        if(isValid)
        {
            clientList.get(clientID).updateRequest(System.currentTimeMillis());
        }
        
        bf.freeBusyFlag();
        
        return isValid;
    }
}
