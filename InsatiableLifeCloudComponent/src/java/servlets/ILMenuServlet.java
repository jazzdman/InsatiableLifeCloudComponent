package servlets;

import web_utils.AllRecipesProxy;
import web_utils.BingProxy;
import web_utils.RecipeRequestConstructor;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.PrintWriter;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPOutputStream;
import java.util.List;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.servlet.ServletConfig;
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
    // Defines a set of methods that a servlet uses to communicate with 
    // its servlet container, for example, to get the MIME type of a file, 
    /// dispatch requests, or write to a log file.
    private ServletContext servletContext;
    // A variable to decide if there is a problem setting up the servlet
    private boolean servletProblem;
      
    // This allows the servlet to get information about Tomcat
    private transient ServletConfig servletConfig;
    
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
    public void init(ServletConfig servletConfig) throws ServletException
    {
	servletProblem = false;
	// A temporary path to open files used in this servlet
	StringBuffer filePath = new StringBuffer();
	// The lists of dishes and ingredients used by 
	// the RecipeRequestConstructor
	List<String> dishes = null, ingredients = null;
	
	// The object used to get recipes from allrecipes.com
	AllRecipesProxy arp = new AllRecipesProxy();

	// The object used to find URLs to feed to the AllRecipesProxy
	BingProxy bingProxy = new BingProxy();

	this.servletConfig = servletConfig;
        servletContext = servletConfig.getServletContext();

	// Attempt to open the files that need to be passed to the 
        // RecipeRequestConstructor
	try 
	{
	    filePath.append(servletContext.getRealPath("/"));
            filePath.append("conf/dishes.txt");
	    dishes = Files.readAllLines(Paths.get(filePath.toString()),
					StandardCharsets.US_ASCII);
            filePath.delete(0, filePath.length());
            
            filePath.append(servletContext.getRealPath("/"));
            filePath.append("conf/ingredients.txt");
	    ingredients = Files.readAllLines(Paths.get(filePath.toString()),
					StandardCharsets.US_ASCII);
            
            maxPrepTime = Integer.parseInt(servletContext.getInitParameter("preptimeminutes")) +
                            MINUTES_PER_HOUR * Integer.parseInt(servletContext.getInitParameter("preptimehours"));
        
            minServings = Integer.parseInt(servletContext.getInitParameter("servings"));
        
            minCalories = Integer.parseInt(servletContext.getInitParameter("calories"));
	}
	// If we hit an exception here, we really can't do anything with
	// the servlet.  Stop at this point.
	catch(Exception e)
	{
	    servletProblem = true;
	    return;
	}


	// Create the RecipeRequestConstructor
	RecipeRequestConstructor recipeConstructor = 
	    new RecipeRequestConstructor(dishes, ingredients);
	
	// Save these variables to the servlet context of
	// this application
	servletContext.setAttribute("arp", arp);
	servletContext.setAttribute("bp",bingProxy);
	servletContext.setAttribute("rrc", recipeConstructor);
	
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
	AllRecipesProxy arp;
	BingProxy bp;
	RecipeRequestConstructor rrc;
        String current_request_url, encodedString;
        int tmpCal, tmpTime, tmpServ;
	response.setContentType("text/xml");
	PrintWriter writer = response.getWriter();
	List<String> ingredientArray;
        HashMap<String,Object> tempHash;
        
        servletContext.log("doGet has been called.");
	
        // Start the XML document
        writer.print("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
        writer.print("<recipes>\r\n");
        
        // Don't return any results if we had a problem starting the servlet.
        // Indicate servlet error with count of -1
        if (servletProblem)
        {
            writer.print("<count>"+ new Integer(SERVER_INIT_ERROR).toString()+"</count>");
            writer.print("</recipes>");
            return;
        }

	// Get the objects saved in the init method
	rrc = (RecipeRequestConstructor)servletContext.getAttribute("rrc");
	bp = (BingProxy)servletContext.getAttribute("bp");
	arp = (AllRecipesProxy)servletContext.getAttribute("arp");

        try
	{
	    // Get the URL to send to Bing
	    current_request_url = rrc.getRequest();
	
	    // Get recipe URLs from Bing
	    bp.findRecipes(current_request_url);
            
             // Check to make sure the request parameters are named correctly 
            // and have acceptable values.
	    if (!validateRequest(request))
	    {   
		writer.print("<count>"+ new Integer(INVALID_REQUEST_PARAMETERS_ERROR).toString()+"</count>");
                writer.print("</recipes>");
		return;
	    }
            
	    // If we didn't get any recipe URLs, send back an empty
	    // XML document.  Indicate empty list with count of -2
	    if (bp.getRecipeURLs().isEmpty())
	    {   
                writer.print("<count>"+new Integer(BING_LIST_EMPTY_ERROR).toString()+"</count>");
                writer.print("</recipes>");
		return;
	    } else 
            {
                 writer.print("<count>"+new Integer(bp.getRecipeURLs().size())+"</count>\r\n");
            }

	    // Get all recipes we can find that match the request parameters
            for(String url:bp.getRecipeURLs())
	    { 
		arp.generateRecipes(url, 
				    calories, 
				    prepTime, 
				    servings, 
				    current_request_url);
	    }
	}
	// If we hit a problem, don't do anything. We want to return
	// whatever we did find.
	catch(Exception e)
	{
            e.printStackTrace();
            writer.print("<count>"+ new Integer(SERVER_COLLECT_ERROR).toString()+"</count>");
            writer.print("</recipes>");
            return;
	}

        
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

	servletContext.removeAttribute("arp");
	servletContext.removeAttribute("bp");
	servletContext.removeAttribute("rrc");
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
            gzip.write(str.getBytes("UTF-8"));
        }

	// Get the gzip'ed string and return it to the caller
	outStr = obj.toString("UTF-8");

	// Convert gzip'ed string to Base64
	outStr = new String(Base64.encodeBase64(outStr.getBytes()));

	return outStr;
    }

}
