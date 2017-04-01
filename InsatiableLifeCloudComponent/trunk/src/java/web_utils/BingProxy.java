package web_utils;

import java.net.URL;
import java.net.HttpURLConnection;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import javax.json.Json;
import javax.json.stream.JsonParser;

/*****************************************************************
 *
 * The purpose of this class is to collect URLs from allrecipes.com
 * using the search engine Bing.  This class uses Regular Expressions
 * to find the links.  It assumes that ten pages of results will
 * be returned and chooses a random page from those ten.
 *
 */
public class BingProxy
{
    
    String searchString;
    
    /**
     * A string that will hold the results created by this class.
     */
    private StringBuffer request;
    
    /**
     * A list of dishes and ingredients used to create the URL.
     */
    private final List<String> dishes, ingredients;

    /**
     * The list of URLs that this class finds
     */
    private final HashMap<String, Object> recipeURLs;
    
    /**
     * The object used to connect to Bing and get back results.
     */
    HttpURLConnection connection;
    
    /**
     * The objects used to apply Regular Expressions to the results collected
     * with the HttpURLConnection.
     */
    Pattern ptrn;
    Matcher mtchr;
    

    /**
     * Initialize the lists of URLs
     * @param d
     * @param i
     */
    public BingProxy(List<String> d, List<String> i)
    {
	recipeURLs = new HashMap();
        
        request = new StringBuffer();

	dishes = d;
	ingredients = i;
        
    }
    
    /**
     * 
     * @return 
     */
    public String getSearchString()
    {
        return searchString;
    }
    
     /**
     * This method will construct a search request using Bing.
     * The method returns that search request to the caller.
     * @param rndVal1
     * @param rndVal2
     * @return 
     */
    public String getRequest(double rndVal1, double rndVal2)
    {
	int tmpIndex;
	StringBuilder ingredientString = new StringBuilder();
	String start = 
	    "https://api.cognitive.microsoft.com/bing/v5.0/search?q=%i+site%3Aallrecipes.com&count=100&offset=0";
	String tmp;
       
	// Make sure the request is empty to begin with
	if(request.length() > 0)
	{
	    request.delete(0, request.length());
	}
    
	// Add in the basic search string
	request.append(start);
    
	// Create a set of search values from the array of 
	// ingredients and dishes
	tmpIndex = (int)(rndVal1*ingredients.size());
	ingredientString.append(ingredients.get(tmpIndex));
	ingredientString.append("+");
	tmpIndex = (int)(rndVal2*dishes.size());
	ingredientString.append(dishes.get(tmpIndex));    
	    
	// Put those search values into the search string
	tmp = request.toString();
	tmp = tmp.replaceAll("%i", ingredientString.toString());
	request = new StringBuffer(tmp);
    
	// Return the created query URL
	return request.toString();
    }

    /**
     * Return the list of URLs, and its contents, compiled by this class.
     * @return 
     */
    public ArrayList<String> getRecipeURLs()
    {
	return new ArrayList(recipeURLs.keySet());
    }

    /**
     * This method is called to actually carry out the search for
     * allrecipes.com URLs using Bing.  It uses Regular Expressions
     * to grab a the URLs out of the JSON stream.?
     * @param rndVal1
     * @param rndVal2
     */
    public void findRecipes(double rndVal1, double rndVal2) 
    {
        JsonParser parser;
        searchString = getRequest(rndVal1, rndVal2);
	// The RE that finds the various pages from the search results
	String recipeRegex = "allrecipes.com/[Rr]ecipe/[^/]*/.*?";
	String tmp;
        ptrn = Pattern.compile(recipeRegex, Pattern.DOTALL);
        boolean foundURL = false;
        
	// Remove any URLs that are already stored
	recipeURLs.clear();
	
	// Open a connection to Bing 
        try
        {    
            connection = 
	    (HttpURLConnection)new URL(searchString).openConnection();
            connection.setRequestProperty("Ocp-Apim-Subscription-Key", "c67805c17ae94afda81e82e71eca5818");
            connection.setRequestProperty("Ocp-Apim-Subscription-Key", "6a4d795f937d4349828969486958e698");

        } catch (IOException e) {
            if(connection != null)
               connection.disconnect(); 
            return;
        } 
        
        try
        {
            parser = Json.createParser(connection.getInputStream());
            while (parser.hasNext()) 
            {
                JsonParser.Event event = parser.next();
                switch(event) 
                {
                    case KEY_NAME:
                        if ( parser.getString().equals("displayUrl"))
                        {
                            foundURL = true;
                        }
                        break;
                    case VALUE_STRING:
                    case VALUE_NUMBER:
                        
                        mtchr = ptrn.matcher(parser.getString());
                        if(foundURL && mtchr.find())
                        {
                            if(!recipeURLs.containsKey(mtchr.group()))
                            {
                                recipeURLs.put(mtchr.group().toLowerCase(), new Object());
                            }
                            foundURL = false;
                        }
                        break;
                       
                }
            }
        } catch(IOException e) { 
            
        }   
        connection.disconnect();
        
    }
    
}
