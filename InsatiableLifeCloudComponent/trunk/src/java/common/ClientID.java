/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package common;

import org.w3c.dom.Element;
import java.util.ArrayList;
import java.io.IOException;
import java.io.BufferedWriter;

/**
 *
 * The purpose of this class it to represent a client that accesses the web service.
 * This class holds information about how often a client accesses the web service
 * as well as what other clients are associated with this one.
 * 
 * @author jazzdman
 */
public class ClientID 
{
    /**
     * A unique ID for this client
     */
    private String ID;
    
    /**
     * The last time the client accessed the web application
     */
    private long latestRequest;
    
    /**
     * The other clients that are associated with this one
     */
    private ArrayList<String> associations;
    
    /**
     * 
     * The constructor for this class, create a ClientID from an XML Node.
     * 
     * @param clientID - an XML Node that contains information about a client
     * ID
     * 
     */ 
    public ClientID(Element clientID)
    {
        // Break apart the XML node to get the member variables for this clientID.
        ID = clientID.getElementsByTagName("ID").item(0).getFirstChild().getNodeValue();
        try
        {
            latestRequest = Long.parseLong(clientID.getElementsByTagName("request").item(0).getFirstChild().getNodeValue());
        }
        catch(Exception e)
        {
            latestRequest = System.currentTimeMillis();
        }
        associations = new ArrayList();
    }
    
    /**
     * 
     * We assume that some other part of the application will verify that the
     * id is a valid one.
     * 
     * @param id - the other clientID that we want to associate with this one
     * 
     */
    public void addAssociation(String id)
    {
        if(!isAssociated(id))
            associations.add(id);
    }
    
    /**
     * 
     * @return the list of IDs associated with this one.
     * 
     */
    public ArrayList<String> getAssociations()
    {
        return associations;
    }
    
    /**
     * 
     * We assume that some other part of the application will verify that the
     * id is a valid one.
     * 
     * @param lr - the {@link Date} to set updateRequest to
     * 
     */
    public void updateRequest(long lr)
    {
        latestRequest = lr;
    }
    
    /**
     * 
     * @return the {@link Date} that this client ID was last seen 
     * 
     */
    public long getLatestRequest()
    {
        return latestRequest;
    }
    
    /**
     * 
     * @return the ID for this client
     * 
     */
    public String getID()
    {
        return ID;
    }
    
    /**
     * 
     * Save this client ID as an XML node.
     * 
     * The node looks like:
     * 
     * <clientID>
     *  <ID></ID>
     *  <request></request>
     *  <associations>
     *    <ID></ID>
     *  </associations>
     * </clientID>
     * 
     * @param bw - the {@link BufferedWriter} we will use to save the contents
     *             of this clientID to file.
     * @throws java.io.IOException
     * 
     */
    public void serialize(BufferedWriter bw) throws IOException
    {
       
        bw.write("<clientID>");
        bw.write("<ID>"+ID+"</ID>");
        bw.write("<request>"+Long.toString(latestRequest) +"</request>");
        bw.write("<associations>");
        for(String s:associations)
        {
            bw.write("<ID>"+s+"</ID>");
        }
        bw.write("</associations>");     
        bw.write("</clientID>");
    }
    
    /**
     * 
     * Search the list of associations to see if this client ID is associated
     * with ID.  This method assumes that some other part of the application
     * has decided that ID is a valid one
     * 
     * @param ID
     * @return the ID for this client
     * 
     */
    public boolean isAssociated(String ID)
    {
        boolean isAssociated = false;
        
        for(String id:associations)
        {
            if(id.matches(ID))
            {
                isAssociated = true;
                break;
            }
        }
        
        return isAssociated;
    }
    
}
