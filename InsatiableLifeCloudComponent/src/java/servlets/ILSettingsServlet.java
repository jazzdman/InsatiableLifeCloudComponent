package servlets;

import java.io.PrintWriter;
import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/********************************************************************
 *
 * This class is responsible for responding to a web server request.
 * 
 ********************************************************************/
@WebServlet(value="/settings")
public class ILSettingsServlet extends HttpServlet
{
    // Defines a set of methods that a servlet uses to communicate with 
    // its servlet container, for example, to get the MIME type of a file, 
    /// dispatch requests, or write to a log file.
    private ServletContext servletContext;
   
    // A variable to decide if there is a problem setting up the servlet
    private boolean servletProblem;
 
    // This allows the servlet to get information about Tomcat
    private transient ServletConfig servletConfig;
    
    // Use these to store calories, preptime and servings, so we only have
    // do read the init parameters once.
    private String prepTime, calories, servings;
    
    private static final int MINUTES_PER_HOUR = 60;
    
    // Indicate an error in the init method
    private static final int SERVER_INIT_ERROR = -1;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException
    {
        int preptemp;
        String tempString1, tempString2;
        Enumeration<String> e = servletConfig.getInitParameterNames();
        
	this.servletConfig = servletConfig;
        servletContext = servletConfig.getServletContext();
        
        try 
        {
            while(e.hasMoreElements())
            {
               servletContext.log(e.nextElement());       
            }
            /*
            servings = servletContext.getInitParameter("servings");
            
            tempString1= servletContext.getInitParameter("preptimeminutes");
            tempString2 = servletContext.getInitParameter("preptimehours");
            
            preptemp = Integer.parseInt(tempString1)+
                       MINUTES_PER_HOUR*Integer.parseInt(tempString2);
        
            prepTime = new Integer(preptemp).toString();
        
            calories = servletContext.getInitParameter("calories");
            */
            
        } catch (Exception ex)
        {
            servletProblem = true;
        }
	
    }

    @Override 
    // Return the reference to the servletConfig object for this
    // servlet
    public ServletConfig getServletConfig()
    {
	return servletConfig;
    }

    @Override
    // Return a name for this servlet
    public String getServletInfo()
    {
	return "Insatiable Life Settings Servlet";
    }

    @Override
    // This method actually services the request that is made to the web
    // server.  It uses the RecipeRequestConstructor to create a search URL
    // to pass to Bing.  The BingProxy ucses the results from the 
    // RecipeRequestConstructor to find allrecipes.com URLs.  The 
    // AllRecipesProxy uses those URLs to find recipes that fall within
    // the servings and calories requirements sent in the request.  The
    // recipes that match the requirements are passed back in the response
    // as XML.
    public void doGet(HttpServletRequest request,
	     	      HttpServletResponse response) throws ServletException,
							   IOException,
                                                           NumberFormatException
    {
        
	response.setContentType("text/xml");
	PrintWriter writer = response.getWriter();
	
        // Start the XML document
        writer.print("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
        
        if(servletProblem)
        {
            writer.print("</params>");
            writer.print("<param>"+ new Integer(SERVER_INIT_ERROR).toString() +"</param>\r\n");
            writer.print("</params>");
            return;
        }
        
        
        writer.print("<params>\r\n");
        
        writer.print("<preptime>"+ prepTime +"</minutes>\r\n");
       
        
        writer.print("<servings>"+ servings +"</servings>\r\n");
        
        writer.print("<calories>"+ calories +"</calories>\r\n");
        
        writer.print("</params>");
        
	//close the writer
	writer.close();
    }

    @Override
    // Where the servlet closes any persistent objects
    public void destroy()
    {

    }

   
}
