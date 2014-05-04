/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package common;

import static common.ClientIDManager.CLIENT_ID_LENGTH;
import java.util.Date;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author jazzdman
 */
public class ClientIDTest {
    
    boolean setupFailed;
    ClientID instance;
    String id;
    long time;
    
    public ClientIDTest() 
    {
        setupFailed = false;
        id = "0123456789111213141"; 
    }
    
    @Before
    public void setUp() 
    {
        Document doc;
        Element rootElement, idNode, requestNode, associationsNode;
        StringBuffer clientIDSB;
        
        try
        {    
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        }
        catch(ParserConfigurationException e)
        {
            setupFailed = true;
            return;
        }
            
        // Create the ClientID node and its children.
        rootElement = doc.createElement("clientID");
        idNode = doc.createElement("ID");
        // Create the clientID itself and buffer it with zeros
        clientIDSB = new StringBuffer();
        clientIDSB.append(id);
        for(int i = clientIDSB.length()+1;i < CLIENT_ID_LENGTH + 1;i++)
        {
            clientIDSB.insert(0, "0");
        }
        idNode.appendChild(doc.createTextNode(clientIDSB.toString()));
        rootElement.appendChild(idNode);
        
        requestNode = doc.createElement("request");
        requestNode.appendChild(doc.createTextNode(new Long(time).toString()));
        rootElement.appendChild(requestNode);
       
        instance = new ClientID(rootElement);
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of addAssociation method, of class ClientID.
     */
    @Test
    public void testAssociation() 
    {
        System.out.println("addAssociation");
        
        
        if(setupFailed)
        {
            fail("Setup failed.");
            return;
        }
   
        instance.addAssociation(id);
        assertEquals(instance.getAssociations().size(), 1);
        instance.getAssociations().clear();
        
    }
    
    /**
     * Test of addAssociation method, of class ClientID.
     */
    @Test
    public void testAssociation2() 
    {
        System.out.println("association2");
        
        if(setupFailed)
        {
            fail("Setup failed.");
            return;
        }
   
        instance.addAssociation(id);
        assertEquals(instance.getAssociations().get(0) , id);
        instance.getAssociations().clear();
        
    }

    /**
     * Test of updateRequest method, of class ClientID.
     */
    @Test
    public void testLastRequest() {
        System.out.println("lastRequest");
        long now;
        now = System.currentTimeMillis();
        if(setupFailed)
        {
            fail("Setup failed.");
            return;
        }
        instance.updateRequest(now);
        assertEquals(instance.getLatestRequest(), now);
    }

    /**
     * Test of getID method, of class ClientID.
     */
    @Test
    public void testGetID() {
        System.out.println("getID");
        if(setupFailed)
        {
            fail("Setup failed.");
            return;
        }
       
        assertEquals(instance.getID(),id);
    }

    /**
     * Test of isAssociated method, of class ClientID.
     */
    @Test
    public void testIsAssociated() {
        System.out.println("isAssociated");
        if(setupFailed)
        {
            fail("Setup failed.");
            return;
        }
        
        instance.addAssociation(id);
        assertTrue(instance.isAssociated(id));
    }
    
}
