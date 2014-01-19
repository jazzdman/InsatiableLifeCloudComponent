package web_utils;

import java.util.List;
import java.util.Random;

/************************************************************
 *
 * This class is responsible for creating a search URL for Bing.
 *
 ************************************************************/
public class RecipeRequestConstructor
{
    // A string that will hold the results created by this class
    private StringBuffer request;
    // A random number generator used to define a random search URL
    private final Random rnd;
    // A list of dishes and ingredients used to create the URL
    private final List<String> dishes, ingredients;

    // The constructor for this class
    // Initialize the list of dishes and ingredients, the random 
    // number generator, and the string buffer into which the URL
    // will be added.
    public RecipeRequestConstructor(List<String> d, List<String> i)
    {
	request = new StringBuffer();
	rnd = new Random(System.currentTimeMillis());

	dishes = d;
	ingredients = i;
    }

    // This method will construct a search request using Bing.
    // The method returns that search request to the caller
    public String getRequest()
    {
	int tmpIndex;
	StringBuilder ingredientString = new StringBuilder();
	String searchString = 
	    "http://www.bing.com/search?q=%i+site%3Aallrecipes.com";
	String tmp;
       
	// Make sure the request is empty to begin with
	if(request.length() > 0)
	{
	    request.delete(0, request.length());
	}
    
	// Add in the basic search string
	request.append(searchString);
    
	// Create a set of search values from the array of 
	// ingredients and dishes
	tmpIndex = (int)(rnd.nextDouble()*ingredients.size());
	ingredientString.append(ingredients.get(tmpIndex));
	ingredientString.append("+");
	tmpIndex = (int)(rnd.nextDouble()*dishes.size());
	ingredientString.append(dishes.get(tmpIndex));    
	    
	// Put those search values into the search string
	tmp = request.toString();
	tmp = tmp.replaceAll("%i", ingredientString.toString());
	request = new StringBuffer(tmp);
    
	// Return the created query URL
	return request.toString();
    }

}
