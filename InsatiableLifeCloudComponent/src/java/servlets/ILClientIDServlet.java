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
 * This class returns a client ID to anyone who makes a request.
 * 
 * @author jazzdman
 */
@WebServlet(value="/clientID")
public class ILClientIDServlet extends HttpServlet {

    private static final int CREATE = 1;
    
    private static final int ASSOCIATE = 2;
    
    private String operation;
    
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
        
        parseRequest(request);
        
        if(operation.matches("create"))
        {
            response.setContentType("text/xml;charset=UTF-8");
            create(response.getWriter());
        }
        if(operation.matches("associate"))
            associate(response.getWriter());
        
    }
    
    public void parseRequest(HttpServletRequest request)
    {
        String start = request.getQueryString();
	StringTokenizer params = new StringTokenizer(start, "&");
	StringTokenizer keysValues;
	String[] values = new String[3];
	String[] keys = new String[3];
        int i = 0;

	// Break apart the query string by '&'
	while(params.hasMoreTokens())
	{
	    // Break apart the key value pairs
	    keysValues = new StringTokenizer(params.nextToken(), "=");
	    keys[i] = keysValues.nextToken();
	    values[i++] = keysValues.nextToken();
	}
        
        operation = values[0];
        
        if(operation.matches("associate"))
        {
            originalID = values[1];
            associateID = values[2];
        }
    }
    
    public void associate(PrintWriter pw)
    {
        ClientIDManager.getInstance().getClientID(originalID).setAssociation(associateID);
        
        try {
            /* TODO output your page here. You may use following sample code. */
            pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            pw.println("<associate>success</associate>");            
        } catch (Exception e)
        {
            
        }
    }
    
    public void create(PrintWriter pw)
    {
        try {
            /* TODO output your page here. You may use following sample code. */
            pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            pw.println("<clientID>");
            pw.println(ClientIDManager.getInstance().createClientID());
            pw.println("</clientID>");            
        } catch (Exception e)
        {
            
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
