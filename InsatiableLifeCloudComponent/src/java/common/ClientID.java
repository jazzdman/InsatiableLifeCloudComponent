/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package common;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.util.ArrayList;
import java.util.Date;
import java.text.DateFormat;
import java.io.IOException;
import java.io.BufferedWriter;

/**
 *
 * The purpose of this class it to represent a client that access the web service.
 * This class holds information about how often a client accesses the web service
 * as well as what other clients are associated with this one.
 * 
 * @author jazzdman
 */
public class ClientID 
{
    private String ID;
    private Date latestRequest;
    private ArrayList<String> associations;
    
    public ClientID(Node clientID)
    {
        String tmpString;
        NodeList tmpList;
        
        ID = clientID.getChildNodes().item(0).getNodeValue();
        try
        {
            latestRequest = DateFormat.getInstance().parse(clientID.getChildNodes().item(1).getNodeValue());
        }
        catch(Exception e)
        {
            latestRequest = new Date();
        }
        tmpList = clientID.getChildNodes().item(2).getChildNodes();
        associations = new ArrayList();
        
        for(int i = 0; i < tmpList.getLength();i++)
        {
            associations.add(tmpList.item(i).getNodeValue());
        }
    }
    
    public void setAssociation(String id)
    {
        associations.add(id);
    }
    
    public ArrayList<String> getAssociations()
    {
        return associations;
    }
    
    public void updateRequest()
    {
        latestRequest = new Date();
    }
    
    public Date getLatestRequest()
    {
        return latestRequest;
    }
    
    public String getID()
    {
        return ID;
    }
    
    public void serialize(BufferedWriter bw) throws IOException
    {
        bw.write("<clientID>");
        bw.write("<ID>"+ID+"</ID>");
        bw.write("<request>"+latestRequest.toString() +"</request>");
        bw.write("<associations>");
        for(String s:associations)
        {
            bw.write("<ID>"+s+"</ID>");
        }
        bw.write("</associations>");     
        bw.write("</clientID>");
    }
    
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
