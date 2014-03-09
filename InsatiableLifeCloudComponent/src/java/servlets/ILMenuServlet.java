package servlets;

import web_utils.RecipeManager;
import web_utils.AllRecipesProxy;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.PrintWriter;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.StringTokenizer;


import javax.servlet.annotation.WebServlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;

/********************************************************************
 *
 * This class is responsible for responding to a web server request.
 * 
 ********************************************************************/
@WebServlet(value="/menu")
public class ILMenuServlet extends HttpServlet
{
    
    ServletContext servletContext;
    
    // A variable to decide if there is a problem setting up the servlet
    private boolean servletProblem;
    
    // Indicate an error in the init method
    private static final int SERVER_INIT_ERROR = -1;
    
    // Indicate an empty recipe search list
    private static final int BING_LIST_EMPTY_ERROR = -2;
    
    // Indicate invalid request parameters
    private static final int INVALID_REQUEST_PARAMETERS_ERROR = -3;
    
    // Indicate invalid request parameters
    private static final int SERVER_COLLECT_ERROR = -5;
    
    private static final int MINUTES_PER_HOUR = 60;
    
     // The calories, preptime and servings the user has passed along in 
    // the request.
    private int prepTime, calories, servings;
    
    private int maxPrepTime, minCalories, minServings;

    @Override
    public void init() throws ServletException
    {  
	servletProblem = false;
        servletContext = getServletContext();

	// Attempt to open the files that need to be passed to the 
        // RecipeRequestConstructor
	try 
	{
            maxPrepTime = Integer.parseInt(servletContext.getInitParameter("preptimeminutes")) +
                            MINUTES_PER_HOUR * Integer.parseInt(servletContext.getInitParameter("preptimehours"));
            minServings = Integer.parseInt(servletContext.getInitParameter("servings"));
            minCalories = Integer.parseInt(servletContext.getInitParameter("calories"));
            
    
            RecipeManager rm = new RecipeManager(servletContext.getRealPath("/"));
            servletContext.setAttribute("rm", rm);
            
	}
	// If we hit an exception here, we really can't do anything with
	// the servlet.  Stop at this point.
	catch(Exception e)
	{
	    servletProblem = true;
	    return;
	}
    }

    @Override
    // Return a name for this servlet
    public String getServletInfo()
    {
	return "Insatiable Life Menu Servlet";
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
							 IOException
    {
        String encodedString;
	response.setContentType("text/xml");
	PrintWriter writer = response.getWriter();
	List<String> ingredientArray;
        ArrayList<HashMap<String,String>> recipeList;
        RecipeManager recipeManager = (RecipeManager)servletContext.getAttribute("rm");
        AllRecipesProxy arp = new AllRecipesProxy();
        
	
        // Start the XML document
        writer.print("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
        writer.print("<recipes>\r\n");
        
        // Don't return any results if we had a problem starting the servlet.
        // Indicate servlet error with count of -1
        if (servletProblem || recipeManager == null)
        {
            writer.print("<count>"+ new Integer(SERVER_INIT_ERROR).toString()+"</count>");
            writer.print("</recipes>");
            return;
        }

	

        try
	{   
             // Check to make sure the request parameters are named correctly 
            // and have acceptable values.
	    if (!validateRequest(request))
	    {   
		writer.print("<count>"+ new Integer(INVALID_REQUEST_PARAMETERS_ERROR).toString()+"</count>");
                writer.print("</recipes>");
		return;
	    }
            
            
	}
	// If we hit a problem, don't do anything. We want to return
	// whatever we did find.
	catch(Exception e)
	{
            writer.print("<count>"+ new Integer(SERVER_COLLECT_ERROR).toString()+"</count>");
            writer.print("</recipes>");
            return;
	}

        recipeList = recipeManager.getRecipes(calories, prepTime);
        
        arp.generateRecipes(recipeList, servings);
        
	for(HashMap<String,Object> recipe:arp.getRecipeList())
	{

           if(recipe.get("error") != null ||
              recipe.get("ingredients") == null ||
              recipe.get("title") == null ||
              recipe.get("url") == null ||
              recipe.get("page") == null)
           {	
               servletContext.log("Found recipe that has a problem.");
               continue;
           } 
           // Get the list of ingredients
	   ingredientArray = (List<String>)recipe.get("ingredients");
           
	   // Create a recipe Node
           writer.print("<recipe>\r\n");
	   // Write out the title for the recipe
	   writer.print("<title><![CDATA[ "+(String)recipe.get("title")+" ]]></title>\r\n");
	   // Write out the URL for the recipe
	   writer.print("<url><![CDATA[ "+(String)recipe.get("url")+" ]]></url>\r\n");

	   // Try to gzip the page HTML
	   try 
           {
	   	encodedString = compress((String)recipe.get("page"));
	   	writer.print("<page><![CDATA[ "+ encodedString +" ]]></page>\r\n");
	   }
	   // If that fails just write the page HTML to the document
	   catch(Exception e)
	   {
	   	writer.print("<page><![CDATA[ "+(String)recipe.get("page")+" ]]></page>\r\n");
	   }
           
	   for(String ingredient:ingredientArray)
	   {
	       writer.print("<ingredient><![CDATA[ "+ingredient+" ]]></ingredient>\r\n");
	   }

	   writer.print("</recipe>\r\n");	
	}
	writer.print("</recipes>");

	// Clear out all the recipes we just sent back
	arp.getRecipeList().clear();

	//close the writer
	writer.close();
    }

    @Override
    // Where the servlet closes any persistent objects
    public void destroy()
    {
        RecipeManager rm = (RecipeManager)servletContext.getAttribute("rm");
        rm.end();
        rm.serializeRecipeList(servletContext.getRealPath("/"));
        
        servletContext.removeAttribute("rm");
    }

    // This method breaks apart the query string ?X=A&Y=B&Z=C 
    // into sets of key value pairs and then breaks out the value
    // from those pairs. It also checks to see that the keys and
    // values are acceptable
    public boolean validateRequest(HttpServletRequest request)
    {
	String start = request.getQueryString();
	StringTokenizer params = new StringTokenizer(start, "&");
	StringTokenizer keysValues;
	String[] values = new String[3];
	String[] keys = new String[3];
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

	// Check to make sure keys are what we expect
        valid &= keys[0].equals("maxCal");
        valid &= keys[1].equals("maxPrepTime");
        valid &= keys[2].equals("servings");
	
	// Convert the calorie, prepTime and servings 
	// strings to integers.
	calories = Integer.parseInt(values[0]);
	prepTime = Integer.parseInt(values[1]);
	servings = Integer.parseInt(values[2]);

	// Check to make sure converted values are valid
        valid &= (calories >= minCalories);
        valid &= (prepTime <=  maxPrepTime);
        valid &= (servings >= minServings);


	return valid;

    }

    // This method is used to GZIP and Base64 encode 
    // the HTML for a recipe
    public String compress(String str) throws IOException,
				              UnsupportedEncodingException  
    {
	//Create an byte array stream
	ByteArrayOutputStream obj = new ByteArrayOutputStream();
        String outStr;

	//GZIP the HTML
        try (GZIPOutputStream gzip = new GZIPOutputStream(obj)) 
        {
            gzip.write(str.getBytes("US-ASCII"));
        }


	// Convert gzip'ed bytes to Base64 string
	outStr = new String(Base64.encodeBase64(obj.toByteArray()));

	return outStr;
    }

}
