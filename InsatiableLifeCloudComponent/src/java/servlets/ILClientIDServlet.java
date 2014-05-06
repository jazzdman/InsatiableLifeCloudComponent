package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import common.ClientIDManager;
import java.util.StringTokenizer;
import javax.servlet.annotation.WebServlet;

/**
 *
 * This class returns a client ID to anyone who makes a request.  Also allows
 * a client to associate one client ID to another.
 * 
 * @author jazzdman
 */
@WebServlet(value="/clientID")
public class ILClientIDServlet extends HttpServlet {
    
    /** 
     * Indicate to a client that an error occurred while parsing the incoming
     * request.
     */
    private static final int PARSE_ERROR = -1;
    
    /**
     * Indicate to a client that an error occurred while attempting to associate
     * one client ID with another.
     */
    private static final int ASSOCIATE_ERROR = -2;
    
    /**
     * Indicate to a client that an error occurred while attempting to create
     * a clientID.
     */
    private static final int CREATE_ERROR = -3;
    
    /**
     * The operation the client has requested.
     */
    private String operation;
    
    /**
     * In the case that the user has requested an association, these are
     * the strings/ client IDs to associate with one another.
     */
    private String associateID;
    private String originalID;
    
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException 
    {
        // Figure out what the user has requested
        parseRequest(request);
        
        // Respond based upon their request
        switch(operation)
        {
            // We have been asked to create a client ID
            case "create":
                response.setContentType("text/xml;charset=UTF-8");
                create(response.getWriter());
                break;
            // We have been asked to associate one client ID with another
            case "associate":
                response.setContentType("text/xml;charset=UTF-8");
                associate(response.getWriter());
                break;
            // If we weren't able to parse the request, send back an error 
            // message.
            default:
                try (PrintWriter writer = response.getWriter()) 
                {    
                    response.setContentType("text/xml;charset=UTF-8");
                    writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                    writer.println("<error>"+new Integer(PARSE_ERROR).toString()+"</error>");
                }
                break;
        }
    }
    
    /**
     * 
     * @param request - The HTTPServletRequest that contains the query string
     *                  we're interested in.
     * 
     * Use this method to figure out what the user has requested 
     * 
     */
    public void parseRequest(HttpServletRequest request)
    {
        String start = request.getQueryString();
	StringTokenizer params;
	StringTokenizer keysValues;
	String[] values = new String[3];
	String[] keys = new String[3];
        int i = 0;
        
        params = new StringTokenizer(start, "&");
        
	// Break apart the query string by '&'
	while(params.hasMoreTokens())
	{
	    // Break apart the key value pairs
	    keysValues = new StringTokenizer(params.nextToken(), "=");
	    keys[i] = keysValues.nextToken();
	    values[i++] = keysValues.nextToken();
	}
        
        // For safety's sake, make sure the request is valid
        if(!keys[0].matches("operation"))
        {
            operation = "error";
            return;
        }
        
        // Set the operation
        operation = values[0];
        
        // If the request is for an association, make sure we have the right
        // number of values
        if(operation.matches("associate") &&
           (values[1] == null || values[2] == null))
        {
            operation = "error";
            return;
        }
        
        // If the user has requested an association, get the IDs to associate
        // with one another.
        if(operation.matches("associate"))
        {
            originalID = values[1];
            associateID = values[2];
        }
    }
    
    /**
     * 
     * @param pw - The PrintWriter used to return the response
     * 
     * Call the ClientIDManager to associate one clientID with another.
     * 
     */
    public void associate(PrintWriter pw)
    {
       
        // Print out a valid XML header
        pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        
        // Make sure that both IDs are valid.  If they are, make the association
        if(ClientIDManager.getInstance().validateClientID(originalID) &&
           ClientIDManager.getInstance().validateClientID(associateID))
        {
            ClientIDManager.getInstance().getClientID(originalID).addAssociation(associateID);
            ClientIDManager.getInstance().getClientID(associateID).addAssociation(originalID);
            pw.println("<associate>success</associate>");       
        }
        // If both IDs are not valid, let the client know.
        else 
        {
            pw.println("<error>"+new Integer(ASSOCIATE_ERROR).toString()+"</error>");
        }
                         
        
    }
    
    /**
     * 
     * @param pw - The PrintWriter used to return the response
     * 
     * Call the ClientIDManager to create and save a client ID.
     * 
     */
    public void create(PrintWriter pw)
    {
        // Ask for a client ID from the ClientIDManager
        String clientID = ClientIDManager.getInstance().createClientID();
        
        // Return the result to the client.
        pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        
        // Make sure the clientID is valid.
        if(clientID == null)
        {
            pw.println("<error>"+ CREATE_ERROR +"</error>");
           
        } else {
            
            pw.println("<clientID>"+clientID+"</clientID>");
            
        }
       
        
          
        
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
