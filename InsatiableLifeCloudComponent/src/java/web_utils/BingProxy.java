package web_utils;

import java.net.URL;
import java.net.MalformedURLException;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

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

    /**
     * The list of URLs that this class finds
     */
    private final HashMap<String, Object> recipeURLs;
    
    /**
     * A temporary collection of recipe URLs used to enable testing.
     */
    List<String> searchResults;
    
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
     * The object used to read out the results from the HttpURLConnection.
     */
    BufferedReader in;
    
    /**
     * The object used to hold the results read from the HttpURLConnection.
     */
    StringBuffer bingPage; 

    /**
     * Initialize the lists of URLs
     */
    public BingProxy()
    {
	recipeURLs = new HashMap();
        searchResults = new ArrayList();
        
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
     * to grab a random page of Bing results.
     * @param searchString
     */
    public void findRecipes(String searchString) 
    {
	// The RE that finds the various pages from the search results
	String pageRegex = "<a href=\"(/search[^\"]*)\"[^>]*>\\d</a>";
	String tmp;
	bingPage = new StringBuffer();
        
	// Remove any URLs that are already stored
	recipeURLs.clear();
        searchResults.clear();
	
	// Open a connection to Bing 
        try
        {    
            connection = 
	    (HttpURLConnection)new URL(searchString).openConnection();

        } catch (IOException e)
        {
            if(connection != null)
               connection.disconnect();
            
            return;
        }
        
        try
        {
            // Create a BufferedReader from the connection made to Bing
            in = new BufferedReader(
		 new InputStreamReader(
		     connection.getInputStream()));
        } catch(IOException e)
        {
            connection.disconnect();
            return;
        }


	// Read the results from Bing
        try
        {
            while((tmp = in.readLine()) != null)
            {
                bingPage.append(tmp).append("\r\n)");
            }
        } catch (IOException e)
        {
            connection.disconnect();
            return;
        }
        

	// Close that HTTPURLConnection
	connection.disconnect();

	// Get the links to all the pages of Bing results
        ptrn = Pattern.compile(pageRegex, Pattern.DOTALL);
	mtchr = ptrn.matcher(bingPage);
	
	while(mtchr.find())
	{
	    searchResults.add(mtchr.group(1));
	}
    }
    
    /**
     * This a utility method.  By allowing access to the search results we
     * got from Bing, we are able to unit test this class.
     * @return 
     */
    public List<String> getSearchResults()
    {
        return searchResults;
    }
    
    /** 
     * This method was split off from the findRecipes method to enable
     * unit testing of this class.  This method scrapes out the actual 
     * allrecipes.com URLs from a random Bing page.
     * @param rndIndex
     */
    public void filterRecipes(int rndIndex)
    {
        URL tempURL1;
        String tmp;
        bingPage = new StringBuffer();
        // The RE that will find the links to the recipes we want
	String recipeRegex = 
	       "<a href=\"(http://allrecipes.com/[Rr]ecipe/[^/]*/).*?\"";
        
	// Get one of the pages of results if there is more than one.
	if (!searchResults.isEmpty()) 
        {
	    
            tmp = (String)searchResults.get(rndIndex);

	    // Create a URL based on the random result
            try
            {
                tempURL1 = 
		new URL("http://" + connection.getURL().getHost() + tmp);
            } catch(MalformedURLException e)
            {
                return;
            }

	    
	    // Read the contents of that randomly selected page
            try
            {
                connection = (HttpURLConnection)tempURL1.openConnection();
                in = new BufferedReader(
		     new InputStreamReader(
		         connection.getInputStream()));
            } catch(IOException e)
            {
                if(connection != null)
                    connection.disconnect();
                return;
            }
            
            
            
            try
            {
                while((tmp = in.readLine()) != null)
                {
                    bingPage.append(tmp).append("\r\n)");
                }
            } catch (IOException e)
            {
                
            }
            connection.disconnect();
	    
        }   

	// Find the recipe URLs from allrecipes.com
	//NSLog(@"bing page %@.", bingPage);
	ptrn = Pattern.compile(recipeRegex, Pattern.DOTALL);
	mtchr = ptrn.matcher(bingPage);
	
	while(mtchr.find())
	{
            tmp = mtchr.group(1);
            tmp = tmp.toLowerCase();
            if(!recipeURLs.containsKey(tmp))
                recipeURLs.put(tmp, new Object());
	}   

    }
}
