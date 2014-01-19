package web_utils;

import java.net.URL;
import java.net.MalformedURLException;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/*****************************************************************
 *
 * The purpose of this class is to collect URLs from allrecipes.com
 * using the search engine Bing.  This class uses Regular Expressions
 * to find the links.  It assumes that ten pages of results will
 * be returned and chooses a random page from those ten.
 *
 *****************************************************************/
public class BingProxy
{

    // The list of URLs that this class finds
    private final ArrayList<String> recipeURLs;

    // The constructor for this class.
    // Initialize the list of URLs
    public BingProxy()
    {
	recipeURLs = new ArrayList<>();
    }

    // Return the list of URLs, and its contents, compiled 
    // by this class
    public ArrayList<String> getRecipeURLs()
    {
	return recipeURLs;
    }

    // This method is called to actually carry out the search for
    // allrecipes.com URLs using Bing.  It uses Regular Expressions
    // to grab a random page of Bing results and to then scrape out
    // the actual allrecipes.com URLs from that random Bing page.
    public void findRecipes(String searchString) throws MalformedURLException,
							IOException
    {
	// The RE that finds the various pages from the search results
	String pageRegex = "<a href=\"(/search[^\"]*)\"[^>]*>\\d</a>";
	// The RE that will find the links to the recipes we want
	String recipeRegex = 
	       "<a href=\"(http://allrecipes.com/[Rr]ecipe/[^/]*/).*?\"";
	String tmp, tempURL3, userAgent, tmp1, tmp2;
	URL tempURL1;
	StringBuffer bingPage = new StringBuffer();
	BufferedReader in;
	List<String> searchResults = new ArrayList<>(), 
	          searchResults2 = new ArrayList<>();
	int rndIndex;
	Pattern ptrn;
	Matcher mtchr;
	
	// Remove any URLs that are already stored
	recipeURLs.clear();
	
	// Open a connection to Bing 
	HttpURLConnection connection = 
	    (HttpURLConnection)new URL(searchString).openConnection();


	// In order for the strategy implemented in this method to work, we 
        // need to tell allRecipes.com that we are requesting the recipe from
        // the desktop version of Safari
	//connection.setRequestProperty("Host", connection.getURL().getHost());
        //userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9) AppleWebKit/537.71 (KHTML, like Gecko) Version/7.0 Safari/537.71";
        //connection.setRequestProperty("User-Agent",userAgent);
        //connection.setRequestProperty("Accept", "application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
        //connection.setRequestProperty("Accept-Language", "en-us");

	// Here we are telling the allrecipes.com webserver that we accept
        // gzip'ed responses.
	//connection.setRequestProperty("Accept-Encoding", "gzip,deflate");
	//connection.setRequestProperty("Connection", "keep-alive");

	// Create a BufferedReader from the connection made to Bing
	in = new BufferedReader(
		 new InputStreamReader(
		     connection.getInputStream()));


	// Read the results from Bing
	while((tmp = in.readLine()) != null)
	{
	    bingPage.append(tmp).append("\r\n)");
	}

	// Close that HTTPURLConnection
	connection.disconnect();

	// Get the links to all the pages of Bing results
	//NSLog(@"bing page %@.", bingPage);
        ptrn = Pattern.compile(pageRegex, Pattern.DOTALL);
	mtchr = ptrn.matcher(bingPage);
	
	while(mtchr.find())
	{
	    //System.out.println(mtchr.group(1)+"\r\n");
	    searchResults.add(mtchr.group(1));
	}
    
	// Get one of the pages of results if there is more than one.
	if (!searchResults.isEmpty()) 
        {
	    // Get a random number based on the number of search
	    // results
            rndIndex = (int)Math.random()*searchResults.size();
            tmp = (String)searchResults.get(rndIndex);

	    // Create a URL based on the random result
	    tempURL1 = 
		new URL("http://" + connection.getURL().getHost() + tmp);

	    
	    // Read the contents of that randomly selected page
	    connection = (HttpURLConnection)tempURL1.openConnection();
	    in = new BufferedReader(
		     new InputStreamReader(
		         connection.getInputStream()));

	    bingPage.delete(0, bingPage.length()-1);
	    while((tmp = in.readLine()) != null)
	    {
		bingPage.append(tmp).append("\r\n)");
	    }

	    connection.disconnect();


        }   

	// Find the recipe URLs from allrecipes.com
	//NSLog(@"bing page %@.", bingPage);
	ptrn = Pattern.compile(recipeRegex, Pattern.DOTALL);
	mtchr = ptrn.matcher(bingPage);
	
	while(mtchr.find())
	{
	    //System.out.println(mtchr.group(1)+"\r\n");
            tmp = mtchr.group(1);
            tmp = tmp.toLowerCase();
            searchResults2.add(tmp);
	}

	// Make sure there are no duplicates
        for(int i = 0; i<searchResults2.size(); i++)
        {
            tmp1 = searchResults2.get(i);
            for(int j = 0; j<searchResults2.size(); j++)
            {
                tmp2 = searchResults2.get(j);
                
                if((i!=j) && (tmp1.compareTo(tmp2) == 0))
                {
                    searchResults2.remove(tmp1);
                }
            }
        }
	
        
	// Store the URLs in the recipeURLs instance variable.
	for ( String url : searchResults2) 
	{
	    System.out.println(url+"\r\n");
	    recipeURLs.add(url +"Detail.aspx");             
        }  
    }
}
