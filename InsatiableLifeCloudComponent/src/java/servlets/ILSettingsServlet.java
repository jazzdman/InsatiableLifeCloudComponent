package servlets;

import common.ClientIDManager;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.StringTokenizer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/********************************************************************
 *
 * The settings display in the client needs to know what the the limits are for:
 * 
 * calories per serving
 * servings
 * preparation time
 * 
 * The client makes a call to this servlet to find that information.
 * 
 ********************************************************************/
@WebServlet(value="/settings")
public class ILSettingsServlet extends HttpServlet
{
   
    /**
     * A variable to decide if there is a problem setting up the servlet
     */
    private boolean servletProblem;
    
    /**
     * Use these to store calories, preptime and servings, so we only have
     * do read the init parameters once.
     */
    private String prepTime, calories, servings;
    
    /**
     * The number of minutes in an hour
     */
    private static final int MINUTES_PER_HOUR = 60;
    
    /**
     * Indicate an error in the init method
     */
    private static final int SERVER_INIT_ERROR = -1;
    
    /**
     * Indicate that we couldn't validate the user
     */
    private static final int VALIDATION_ERROR = -2;

    /**
     * This method gets called the first time a request is made to the 
     * servlet.  It is effectively the constructor for this class.  I use it
     * to instantiate member variables.  Those instance variables are the
     * values that the client setting screen will need.
     * @throws javax.servlet.ServletException
     */
    @Override
    public void init() throws ServletException
    {
        int preptemp;
        String tempString1, tempString2;
        ServletContext servletContext;
        
        try 
        {
            servletContext = getServletContext();
          
            servings = servletContext.getInitParameter("servings");
            
            tempString1= servletContext.getInitParameter("preptimeminutes");
            tempString2 = servletContext.getInitParameter("preptimehours");
            
            preptemp = Integer.parseInt(tempString1)+
                       MINUTES_PER_HOUR*Integer.parseInt(tempString2);
        
            prepTime = new Integer(preptemp).toString();
        
            calories = servletContext.getInitParameter("calories");
            
        } catch (NumberFormatException ex)
        {
            servletProblem = true;
        }
	
    }

    @Override
    /**
     * Return a name for this servlet
     */
    public String getServletInfo()
    {
	return "Insatiable Life Settings Servlet";
    }

    /**
     *
     * This method actually services the request that is made to the web
     * server.  The variables are passed back in the response
     * as XML.
     * 
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     * @throws NumberFormatException
     */
    @Override
    public void doGet(HttpServletRequest request,
	     	      HttpServletResponse response) throws ServletException,
							   IOException,
                                                           NumberFormatException
    {
        StringBuffer respBuf = new StringBuffer();
        
        respBuf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
         
        if(!validateRequest(request))
        {
            respBuf.append("</params>");
            respBuf.append("<param>"+ new Integer(VALIDATION_ERROR).toString() +"</param>");
            respBuf.append("</params>");
                
        } else if(servletProblem)
        {
            respBuf.append("</params>");
            respBuf.append("<param>"+ new Integer(SERVER_INIT_ERROR).toString() +"</param>");
            respBuf.append("</params>");
                
        } else {
            
            respBuf.append("<params>");
            respBuf.append("<preptime>"+ prepTime +"</preptime>");
            respBuf.append("<servings>"+ servings +"</servings>");
            respBuf.append("<calories>"+ calories +"</calories>");
            respBuf.append("</params>");
        }
            
	response.setContentType("text/xml");
        try (PrintWriter writer = response.getWriter()) 
        {
            writer.print(respBuf.toString());
        }
    }
    
    /**
     * This method breaks apart the query string ?X=A&Y=B&Z=C 
     * into sets of key value pairs and then breaks out the value
     * from those pairs. It also checks to see that the keys and
     * values are acceptable.
     * @param request
     * @return 
     */
    public boolean validateRequest(HttpServletRequest request)
    {
        ClientIDManager cm = ClientIDManager.getInstance();
	String start = request.getQueryString();
	StringTokenizer params = new StringTokenizer(start, "&");
	StringTokenizer keysValues;
	String[] values = new String[1];
	String[] keys = new String[1];
	int i = 0;
	boolean valid=true;

	// Break apart the query string by '&'
	while(params.hasMoreTokens())
	{
	    // Break apart the key value pairs
	    keysValues = new StringTokenizer(params.nextToken(), "=");
	    keys[i] = keysValues.nextToken();
	    values[i++] = keysValues.nextToken();
	}
        
        // Make sure the clientID is valid
        valid &= keys[0].matches("clientID");
        valid &= cm.validateClientID(values[0]);

	return valid;

    }

   
}
