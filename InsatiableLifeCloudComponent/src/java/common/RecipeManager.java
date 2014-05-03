package common;

import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import web_utils.RecipeRequestConstructor;
import web_utils.AllRecipesProxy;
import web_utils.BingProxy;


/**
 * The Insatiable Life app is evolving.  It was originally up to the 
 * AllRecipiesProxy class to collect recipes one at a time in response
 * to a request from a client.  
 * 
 * For this iteration, the RecipeManager is a subclass of Thread.  The thread
 * uses the RecipeRequestConstructor, BingProxy and AllRecipesPoxy to keep a 
 * random list of recipes full.  When the ILMenuServlet needs a recipe, 
 * it uses this class to search the HashMap for recipes that match a client's
 * request.
 * 
 * The next iteration of the Insatiable Life app will implement a local recipe
 * database.
 * 
 * @author jazzdman
 */
public class RecipeManager implements Runnable
{
    /**
     * Used to pick out lists of recipes from the BingProxy.
     */
    private final Random rnd;
    
    /**
     * A temporary path to open files on the server
     */
    private final StringBuffer filePath;
    
    /**
     * The lists of dishes and ingredients used by 
     * the RecipeRequestConstructor
     */
    private List<String> dishes = null, ingredients = null;
    
    /**
     * The list of recipes that this class maintains
     */
    private final HashMap<String, HashMap<String,String>> recipeList;
      
    /**
     * An object to create a random recipe search for Bing.
     */
    private final RecipeRequestConstructor recipeRequestConstructor;
        
    /**
     * An object that calls to allrecipes.com with a URL created by the
     * BingProxy and returns a recipe.
     */
    private final AllRecipesProxy allRecipesProxy;
        
    /**
     * An object that sends the results from the RecipeRequestConstructor
     * to Bing and saves the allrecipes.com URLs.
     */
    private final BingProxy bingProxy;
    
    /**
     * This starts true and the Thread keeps running until this is false.
     */
    private boolean running;
    
    /**
     * An object to stop and start Threads that call this object, so we don't
     * try to simultaneously read and write to the recipeList or run into other
     * race conditions.
     */
    private final BusyFlag bf;
    
    /** 
     * The size of the recipeList
     */
    private static final int MAX_RECIPES = 1000;
    
    /**
     * The maximum number of recipes that we will try to 
     * pull out of the recipeList when getRecipes is called.
     */
    private static final int RECIPE_COUNT = 10;
    
    
    /**
     * The constructor for this class.  
     * Read in data to pass to the RecipeRequestConstructor.
     * Start the Thread.
     * Instantiate :
     *      1) a BingProxy
     *      2) a AllRecipesProxy 
     *      3) A RecipeRequestConstructor
     *      4) A BusyFlag
     *      5) The recipeList
     */
    public RecipeManager() throws IOException
    {
        rnd = new Random(System.currentTimeMillis());
        
        filePath = new StringBuffer();
        filePath.append(System.getenv("CATALINA_HOME")+"/webapps/InsatiableLifeCloudComponent/WEB-INF/conf/dishes.txt");
        running = true;
          
	dishes = Files.readAllLines(Paths.get(filePath.toString()),
					StandardCharsets.US_ASCII);
        filePath.delete(0, filePath.length());
            
        filePath.append(System.getenv("CATALINA_HOME")+"/webapps/InsatiableLifeCloudComponent/WEB-INF/conf/ingredients.txt");
	ingredients = Files.readAllLines(Paths.get(filePath.toString()),
					StandardCharsets.US_ASCII);
       
        
        // Instantiate member variables
        recipeRequestConstructor = 
                    new RecipeRequestConstructor(dishes, ingredients);
        allRecipesProxy = new AllRecipesProxy();
        bingProxy = new BingProxy();
        bf = new BusyFlag();
        recipeList = new HashMap();
        
        // Fill the recipeList if we have something to 
        fillRecipeList();
            
    }
    
    /**
     * Return the {@link HashMap} of recipes that this object has collected.
     */
    public HashMap<String, HashMap<String,String>> getRecipeList()
    {
        return recipeList;
    }
    
    /**
    * This class implements Runnable so that it doesn't clog up the start of
    * the web app.  This is where the recipeList is constructed.
    */
    @Override
    public void run()
    {
        String current_request_url, tmpTitle;
        HashMap<String, String> recipeHash;
        int rndIndex, sleepTime;
        
        // Continue until running is falase.
        while(running)
        {
            if(recipeList.size() == MAX_RECIPES)
            {
                sleepTime = 1000;
            } else
            {
                sleepTime = 100;
            }
                
                
            try
            {
                Thread.sleep(sleepTime);
            } catch (Exception e)
            {
                // If we can't sleep, it's no big deal
            }
            
            // Don't proceed if we have as many recipes as we want.
            // Sleep briefly so that this thread doesn't swallow up processor
            // resources.
            if(recipeList.size() == MAX_RECIPES)
            {  
                continue;
            }
                
            // Make sure no other thread can move while we do the following
            bf.getBusyFlag();
            
            try 
            {
                // Get the URL to send to Bing
                current_request_url = recipeRequestConstructor.getRequest(rnd.nextDouble(),
                                                                          rnd.nextDouble());
	
                // Get recipe URLs from Bing
                bingProxy.findRecipes(current_request_url);
                
                // Get a random number based on the number of search
                // results
                rndIndex = (int)rnd.nextDouble()*bingProxy.getSearchResults().size();
                bingProxy.filterRecipes(rndIndex);
                
                // If we have no recipe URLs for this loop, start again.
                if(bingProxy.getRecipeURLs().isEmpty())
                {
                    bf.freeBusyFlag();
                    continue;
                }
                    
                
                //  Use each of the URLs we found
                for(String url:bingProxy.getRecipeURLs())
                {
                    // Get a recipe from allrecipes.com
                    recipeHash = allRecipesProxy.generateRecipe(url, current_request_url);
                    
                    // Throw out recipes that have problems
                    if(recipeHash == null)
                    {
                        continue;
                    }
                    
                    tmpTitle = (String) recipeHash.get("title");
                    if(recipeHash.get("error") != null )
                    {
                        continue;
                    }
                       
                    if(tmpTitle.matches(""))
                    {
                        continue;
                    }
                    
                    // Only save recipes that are unique
                    if(!recipeList.containsKey(recipeHash.get("title")))
                    {
                        recipeList.put(recipeHash.get("title"), recipeHash);
                    }
                    
                    // If we have enough recipes, stop this inner loop.
                    if(recipeList.size() == MAX_RECIPES)
                    {
                        break;
                    }
                }
                
            } catch (Exception e)
            {
                // No need to do anything
                // We really don't care if one pass fails
            }
            
            // We are done searching for recipes, other threads can continue.
            bf.freeBusyFlag();
        }
            
    }
    
    /**
     * Call this method to stop the run method.
     * We need to put this Thread on hold before we can actually stop it.
     */
    public void end()
    {
        bf.getBusyFlag();
        running = false;
        bf.freeBusyFlag();
        
    }

    /**
     *
     * This method searches the recipeList for those recipes that have 
     * no more than "calories" calories per serving and "prepTime" minutes
     * to prepare.  
     * 
     * @param calories
     * @param prepTime
     * @return
     */
    public ArrayList<HashMap<String,String>> getRecipes(int calories, int prepTime)
    {
        ArrayList<HashMap<String,String>> recipesToReturn = new ArrayList();
        ArrayList<String> itemsToRemove = new ArrayList();
        int recipeCount=0;
        HashMap<String,String> recipe;
        int cal, pt;
        
        // Stop other Threads while we do this.
        bf.getBusyFlag();
        
        // Search through each of the recipes 
        for(String title:recipeList.keySet())
        {
            // Get the calores and preptime for the recipe
            recipe = recipeList.get(title);
            cal = Integer.parseInt((String)recipe.get("calories"));
            pt = Integer.parseInt((String)recipe.get("preptime"));
                
            // Hold onto those recipes whose calories and prep time are less 
            // or equal to the input parameter values.
            if(cal <= calories &&
               pt <= prepTime &&
               recipe.get("error") == null)
            {
                recipesToReturn.add(recipe);
                itemsToRemove.add(title);
            }   
             
            // Only search RECIPE_COUNT times
            if(recipeCount++ == RECIPE_COUNT)
                break;
        }
        
        // Remove the recipes we found from the recipeList
        for(String item:itemsToRemove)
        {
            recipeList.remove(item);
        }
        
        // Free up other Threads when we are done here.
        bf.freeBusyFlag();
        
        return recipesToReturn;
    }
    
    /**
     * This method reads in the contents of the recipeList from a file.
     */
    private void fillRecipeList()
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        Document recipeDoc;
        Element rootElement;
        NodeList rcpList, recipeChildren;
        HashMap<String,String> tempHash;
        Node recipeNode, tempNode;
        String urlValue=null;
        
        // Read in the contents from an XML file.
        try 
        {
            db = dbf.newDocumentBuilder();
            recipeDoc = db.parse(System.getenv("CATALINA_HOME")+"/webapps/InsatiableLifeCloudComponent/WEB-INF/conf/recipelist.xml");
            rootElement = recipeDoc.getDocumentElement();
            rcpList = rootElement.getElementsByTagName("recipe");
            
            for(int i = 0; i < rcpList.getLength(); i++)
            {
                recipeNode = rcpList.item(i);
                recipeChildren = recipeNode.getChildNodes();
                tempHash = new HashMap();
                for(int j = 0; j < recipeChildren.getLength(); j++)
                {
                    tempNode = recipeChildren.item(j);
                    if(tempNode.getNodeName().matches("url"))
                        urlValue = tempNode.getNodeValue();
                    tempHash.put(tempNode.getNodeName(), tempNode.getNodeValue());
                }
                
                recipeList.put(urlValue, tempHash);
            }
        } catch (Exception e)
        {
            
        }
    }
    
    /**
     * This method saves the contents of the recipeList.
     * This method likely will only be called when the ILMenuServlet is stopped.
     */
    public void serializeRecipeList()
    {
        BufferedWriter bw;
        HashMap<String,String> recipe;
        File oldRecipeFile = new File(System.getenv("CATALINA_HOME")+"/webapps/InsatiableLifeCloudComponent/WEB-INF/conf/recipelist.xml");
        File newRecipeFile = new File(System.getenv("CATALINA_HOME")+"/webapps/InsatiableLifeCloudComponent/WEB-INF/conf/recipelist.xml");
        
        // Delete any file that already exists.  We want to overwrite the 
        // contents.
        if(oldRecipeFile.exists())
            oldRecipeFile.delete();
        
        // Write out the contents of the recipeList as an XML file.
        try 
        {
            bw = new BufferedWriter(new FileWriter(newRecipeFile));
            
            bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
            bw.write("<recipes>\r\n");
        
            for(String keyOne:recipeList.keySet())
            {
                bw.write("\t<recipe>\r\n");
                recipe = recipeList.get(keyOne);
                for(String keyTwo:recipe.keySet())
                {
                    bw.write("\t\t<"+ keyTwo+">"+recipe.get(keyTwo)+"</"+keyTwo+">\r\n");
                }
                    
                bw.write("\t</recipe>\r\n");
            }
        
            bw.write("</recipes>");
            bw.flush();
            bw.close();
            
        } catch (IOException ioe)
        {
        }
    }
}
