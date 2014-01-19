package web_utils;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import org.apache.commons.lang3.StringEscapeUtils;

/*****************************************************************
 *
 * The purpose of this class is to interact with the allrecipes.com 
 * website and scrape HTML read from the allrecipes.com website.
 * Specifically, this class requests a recipe, scaled to a certain
 * number of servings. It the uses Regular Expression to check 
 * whether it can be prepared in a certain amount of time and has
 * only so many calories per serving.  Regular Expressions are then
 * used to find the title of the recipe and the ingredients.
 *
 * Potential improvements : add a second HashMap for recipe titles
 * to reduce search time in generateRecipes
 *
 *****************************************************************/
public class AllRecipesProxy
{
    // The array of recipes that this class find.
    private final ArrayList<HashMap<String,Object>> recipeList;

    // The constructor for this class
    // Initialize the recipeList
    public AllRecipesProxy() 
    {
	recipeList = new ArrayList<>();
    }

    // Return to the caller the recipeList and its contents
    public ArrayList<HashMap<String,Object>> getRecipeList()
    {
	return recipeList;
    }

    // This method populates the recipeList.  It calls a helper method
    // to actually load a recipe.  This method then looks to see if
    // that recipe is valid (i.e. the title is not empty and not a repeat).
    // If the recipe is valid, the recipe is added to recipeList.  For this
    // method a recipe is represented as a HashMap. 
    public void generateRecipes(String url, 
				int desiredCalories,
				int desiredPrepTime,
				int servings,
				String current_request_url) 
				throws MalformedURLException, IOException
    {

	int current_recipe;
	boolean foundRepeat = false;
	HashMap<String,Object> tempHash;
	String tmpString;

	// Get a HashMap that represents a recipe
        tempHash = loadRecipeWithReferer(url,
       				         desiredCalories,
					 desiredPrepTime,
					 servings,
					 current_request_url);

	// Do not proceed if the recipe title is empty.  This means
	// that something went wrong when trying to collect the recipe
        if(((String)tempHash.get("title")).equals(""))
	{ 
            //System.out.println("For some reason title is empty.");
            recipeList.add(tempHash);
	    return;
	}

	// Search the recipeList for any recipes we've already collected
	// that have the same title. 
	for(HashMap currentRecipe: recipeList)
	{
	    tmpString = (String)currentRecipe.get("title");
	    // If the title of a previously collected recipe matches
	    // the one we just found.  Set a boolean to mark this and
	    // break fromt the search.
	    if(tmpString.equals((String)tempHash.get("title")))
            {  
                tempHash.put("error","Found repeat. "+(String)currentRecipe.get("title"));
	        foundRepeat = true;
		break;
            }
        }

	// If we found a repeat, don't proceed
        if(foundRepeat)
	{
	    //System.out.println("Found a repeat.");
            //recipeList.add(tempHash);
	    return;
	}

	// Add the recipe to recipeList
	//System.out.println("Found a recipe.");
     	recipeList.add(tempHash);
    }

    // This method is used to actually collect a recipe from allrecipes.com 
    // scaled to "servings".  It then uses Regular Expression to check if
    // the recipe has no more than "desiredCalories" calories per servings
    // and takes no more than "desiredPrepTime" minutes to prepare.  Regular
    // Expressions are then used to find the tile of the recipes and the
    // ingredients in the recipe.  That information is loaded into a HashMap.
    // That HashMap is then returned to the user.
    public HashMap<String,Object> loadRecipeWithReferer(String url,
							  int desiredCalories,
							  int desiredPrepTime,
							  int servings,
							  String referer)
    {
    
	// The dictionary to pass back to the caller
	HashMap<String,Object> recipeHash = new HashMap<>();
	// A pointer to the HTML returned from allrecipes.com
	StringBuffer recipePage;
	// The Regular Expression used to find a recipe title
        String titleRegex = "<title>\\s*(.*?) - Allrecipes.com";
	// The Regular Expression used to find ingredients in a recipe
	String ingredientsRegex = 
	"class=\"ingredient-amount\">(.*?)</span>.*?class=\"ingredient-name\">(.*?)</span>";
	// An RE to help us find the number of calories per serving
	// in a recipe
	String calorieRegex1 = 
	"<span id=\"lblNutrientValue\">(\\d+)</span> kcal"; 
        String calorieRegex2 = "equals (\\d+) calories";
	// An RE to help us find out how long this recipe takes to 
	// prepare the recipe
        String timeRegex = "title=\"Ready in (.*?)\"";
	String userAgent, tmp;
	// The connection we will make to allrecipes.com
	HttpURLConnection connection;
	// The array of ingredients
	List<String> ingredientArray = new ArrayList<>(); 
	List<String> matches;
	// Did we find a recipe with too many calories?
	boolean tooManyCalories=false;
	// Did we find a recipe that takes too much time?
	boolean tooManyMinutes=false;
	int foundCalories;
	BufferedReader in;
	GZIPInputStream gis;
	InputStream is = null;
	Pattern ptrn;
	Matcher mtchr;
	Map<String,List<String>> headerFields = null;
        String tmpMatch1=null, tmpMatch2=null;
        

	// Create a request and set header fields to make it 
	// look like we're sending this from a web browser.
	try 
	{
	    //System.out.println("URL : " + url);
	    connection = (HttpURLConnection)new URL(url+"?scale="+new Integer(servings).toString()+"&ismetric=0").openConnection();
	}
	// If we encounter an exception here, we can't collect a recipe.
	// We don't need to collect every single recipe, so we can bail at 
	// this point.  We signal an error by setting the title of the 
        // recipe to an empty string.
	catch (Exception e)
	{
	    //e.printStackTrace();
            recipeHash.put("error",e.getMessage());
	    recipeHash.put("title","");
	    return recipeHash;
	}
 
        // Set properties for the connection
	connection.setRequestProperty("Host", connection.getURL().getHost());

        // In order for the strategy implemented in this method to work, we 
        // need to tell allRecipes.com that we are requesting the recipe from
        // the desktop version of Safari.
        userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9) AppleWebKit/537.71 (KHTML, like Gecko) Version/7.0 Safari/537.71";
        connection.setRequestProperty("User-Agent",userAgent);
        connection.setRequestProperty("Accept", "application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
        connection.setRequestProperty("Accept-Language", "en-us");

	// Here we are telling the allrecipes.com webserver that we accept
        // gzip'ed responses.
	connection.setRequestProperty("Accept-Encoding", "gzip,deflate");
	connection.setRequestProperty("Connection", "keep-alive");
    
	recipePage = new StringBuffer();
	
	// Read the response from the webserver.  Assume that we will get
	// back a gzip'ed response.
	try 
	{
	    gis = new GZIPInputStream(connection.getInputStream(), connection.getContentLength());
	    in = new BufferedReader(new InputStreamReader(gis, "ISO-8859-1"));
			 
	} 
        // If we get this exception, then the response was not gzip'ed.  So
	// we get a regular input stream from the connection.
	catch (ZipException ze)
	{
	    // Try to open the input stream from the connection.
	    try 
	    {
		in = new BufferedReader(
		     new InputStreamReader(connection.getInputStream()));
	    }
	    // If we encounter an exception here, we can't collect a recipe.
	    // We don't need to collect every single recipe, so we can bail at 
	    // this point.  We signal an error by setting the title of the 
	    // recipe to an empty string.
	    catch(Exception e)
	    {
		//e.printStackTrace();
                recipeHash.put("error",e.getMessage());
		recipeHash.put("title","");
		return recipeHash;
	    }

	}
	// If we encounter an exception here, we can't collect a recipe.
	// We don't need to collect every single recipe, so we can bail at 
	// this point.  We signal an error by setting the title of the 
        // recipe to an empty string.
	catch(Exception e)
	{
	    //e.printStackTrace();
            recipeHash.put("error",e.getMessage());
	    recipeHash.put("title","");
	    return recipeHash;
	}

	// Try to read the contents of the HTTPURLConnection inputStream
	try 
	{
	    while((tmp = in.readLine()) != null)
	    {
		recipePage.append(tmp);
	    }
	    //System.out.println("Recipe page: "+ recipePage);
	}
	// If we encounter an exception here, we can't collect a recipe.
	// We don't need to collect every single recipe, so we can bail at 
	// this point.  We signal an error by setting the title of the 
        // recipe to an empty string.
	catch(Exception e)
	{
	    //e.printStackTrace();
            recipeHash.put("error",e.getMessage());
	    recipeHash.put("title","");
	    return recipeHash;
	}

	connection.disconnect();

	// Find the part of the page that contains info about
	// calories per serving
	ptrn = Pattern.compile(calorieRegex1, Pattern.DOTALL);
	mtchr = ptrn.matcher(recipePage);

	matches = new ArrayList<>();
	while(mtchr.find())
	{
            tmpMatch1 = mtchr.group(1);
            if(tmpMatch1 != null)
                matches.add(tmpMatch1);
	}
		
	// Make sure we actually found the calories per serving
	// information
	if (matches.size() > 0) 
	{
	    // Are the calories per serving less than the value the user set 
	    // in the settings view?
	    foundCalories = Integer.parseInt(matches.get(0));
        
	    if (foundCalories > desiredCalories) 
	    {
		tooManyCalories = true;
	    }

	} else {
	    
	    // Do second check for calories in the recipe page.
	    ptrn = Pattern.compile(calorieRegex2, Pattern.DOTALL);
	    mtchr = ptrn.matcher(recipePage);

	    matches = new ArrayList<>();
	    while(mtchr.find())
	    {
                tmpMatch1 = mtchr.group(1);
                if(tmpMatch1 != null)
                    matches.add(tmpMatch1);
	    }
                
	    // Are the calories per serving less than the value the user set 
	    // in the settings view?
	    if (matches.size() > 0) 
	    {	
		foundCalories = Integer.parseInt(matches.get(0));
            
		if (foundCalories > desiredCalories) 
		{
		    tooManyCalories = true;
                    recipeHash.put("error","Too many calories");
		}
	    }
	    // If we've hit this case, we didn't find calories listed in the 
	    // recipe.  So, we won't use this recipe.
	    else {
		
		tooManyCalories = true;
                recipeHash.put("error","Couldn't find calories");
	    }

	}
    
	// If the recipe has too many calories, we don't want it
	if (tooManyCalories) 
	{
	    //System.out.println("Too many calories.");
	    recipeHash.put("title","");
	    return recipeHash;

	}
    
        // Find the part of the page that contains info about
	// prep time
	ptrn = Pattern.compile(timeRegex, Pattern.DOTALL);
	mtchr = ptrn.matcher(recipePage);

	matches = new ArrayList<>();
	while(mtchr.find())
	{
	    //System.out.println("Time string :" + mtchr.group(1));
            tmpMatch1 = mtchr.group(1);
            if(tmpMatch1 != null)
                matches.add(tmpMatch1);
	}
	
    
	// Are the number of minutes required to prepare the dish too great?
	if (matches.size() > 0) 
	{
	    // If there are hours listed, we have too many minutes.
	    if (matches.get(0).split(" ").length > 2 ||
		matches.get(0).matches("Hr")) {
           	//System.out.println("Found hours in time."); 
		tooManyMinutes = true;
                recipeHash.put("error","Too many minutes. "+matches.get(0));
	    } else {
            
	
		int foundPrepTime = Integer.parseInt(matches.get(0).split(" ")[0]);
            
		if (foundPrepTime > desiredPrepTime) 
		{
                    tooManyMinutes = true;
                    recipeHash.put("error","Too many minutes. "+matches.get(0));
		}
	    }
        
	} // If we got here, we didn't find prep time.
	else {
	    //System.out.println("Didn't find prep time."); 
	    tooManyMinutes = true;
            recipeHash.put("error","Couldn't find minutes.");
	}
    
	// If the recipe takes too long to prepare, we don't want it
	if (tooManyMinutes) 
	{
	    //System.out.println("Too many minutes.");
	    recipeHash.put("title","");
	    return recipeHash;

	}
    
	// Get the title of the recipe
	ptrn = Pattern.compile(titleRegex, Pattern.DOTALL);
	mtchr = ptrn.matcher(recipePage);

	matches = new ArrayList<>();
	while(mtchr.find())
        {
            tmpMatch1 = mtchr.group(1);
            if(tmpMatch1 != null)
                matches.add(tmpMatch1);
	}
     
	if (matches.size() > 0) 
	{
	    tmp = matches.get(0);	
	    tmp = StringEscapeUtils.unescapeHtml4(tmp);
	    recipeHash.put("title", tmp);
	} 
	// If we got here, we couldn't find the title for the recipe.
	else 
	{
	    //System.out.println("Couldn't find title.");
            recipeHash.put("error","Couldn't find title.");
	    recipeHash.put("title","");
	    return recipeHash;
	}
    
		
        // Collect all the ingredients
        ptrn = Pattern.compile(ingredientsRegex, Pattern.DOTALL);
        mtchr = ptrn.matcher(recipePage);

  
        while(mtchr.find())
        {
            tmpMatch1 = mtchr.group(1);
            tmpMatch2 = mtchr.group(2);
            if(tmpMatch1 != null && tmpMatch2 != null)
                ingredientArray.add( tmpMatch1 + " " + tmpMatch2);
        }
     
        // Save the collected information into a dictionary to be passed
        // back to the caller.
        recipeHash.put("ingredients", ingredientArray);
        recipeHash.put("page", recipePage.toString());
        recipeHash.put("url", url.toString());
    
    
        return recipeHash;
    }

}
