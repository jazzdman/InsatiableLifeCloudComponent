/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web_utils;

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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import common.BusyFlag;

/**
 *
 * @author jazzdman
 */
public class RecipeManager extends Thread
{
    
    // A temporary path to open files used in this servlet 
    private StringBuffer filePath;
    
    // The lists of dishes and ingredients used by 
    // the RecipeRequestConstructor
    private List<String> dishes = null, ingredients = null;
    
    private ArrayList<HashMap<String,String>> recipeList;
        
    private RecipeRequestConstructor recipeRequestConstructor;
        
    private AllRecipesProxy allRecipesProxy;
        
    private BingProxy bingProxy;
    
    private boolean running;
    
    private BusyFlag bf;
    
    private static final int MAX_RECIPES = 1000;
    
    private static final int RECIPE_COUNT = 10;
    
    public RecipeManager(String directoryPath) throws IOException
    {
        filePath = new StringBuffer();
        
        filePath.append(directoryPath);
        filePath.append("WEB-INF/conf/dishes.txt");
	dishes = Files.readAllLines(Paths.get(filePath.toString()),
					StandardCharsets.US_ASCII);
        filePath.delete(0, filePath.length());
            
        filePath.append(directoryPath);
        filePath.append("WEB-INF/conf/ingredients.txt");
	ingredients = Files.readAllLines(Paths.get(filePath.toString()),
					StandardCharsets.US_ASCII);
            
        // Create the RecipeRequestConstructor
        recipeRequestConstructor = 
                    new RecipeRequestConstructor(dishes, ingredients);
            
        // The object used to get recipes from allrecipes.com
        allRecipesProxy = new AllRecipesProxy();

        // The object used to find URLs to feed to the AllRecipesProxy
        bingProxy = new BingProxy();
        
        running = true;
        
        bf = new BusyFlag();
        
        recipeList = new ArrayList();
        
        fillRecipeList(directoryPath);
            
        this.start();
    }
    
    public void run()
    {
        String current_request_url;
        HashMap<String, String> recipeHash;
        
        while(running)
        {
            
            if(recipeList.size() == MAX_RECIPES)
            {   try
                {
                    Thread.sleep(10);
                } catch (Exception e)
                {
                    // If we can't sleep, it's no big deal
                }
                continue;
            }
                
            bf.getBusyFlag();
            
            try 
            {
                // Get the URL to send to Bing
                current_request_url = recipeRequestConstructor.getRequest();
	
                // Get recipe URLs from Bing
                bingProxy.findRecipes(current_request_url);
                
                if(bingProxy.getRecipeURLs().isEmpty())
                    continue;
                
                for(String url:bingProxy.getRecipeURLs())
                {
                    recipeHash = allRecipesProxy.generateRecipe(url, current_request_url);
                            
                    if(recipeHash != null)
                    {
                        recipeList.add(recipeHash);
                    }
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
            
            bf.freeBusyFlag();
        }
            
    }
    
    public void end()
    {
        bf.getBusyFlag();
        running = false;
        bf.freeBusyFlag();
        
    }
    
    public ArrayList<HashMap<String,String>> getRecipes(int calories, int prepTime)
    {
        ArrayList<HashMap<String,String>> recipesToReturn = new ArrayList();
        int recipeCount=0;
        HashMap<String, String> recipe;
        String cal, pt;
        
        bf.getBusyFlag();
        
        while(recipeCount < RECIPE_COUNT)
        {
            for(int i = 0; i < recipeList.size(); i++)
            {
                recipe = recipeList.get(i);
                cal = recipe.get("calories");
                pt = recipe.get("preptime");
                
                if(cal.matches(new Integer(calories).toString()) &&
                   pt.matches(new Integer(prepTime).toString()) &&
                   recipe.get("error") == null)
                {
                    recipesToReturn.add(recipe);
                    recipeList.remove(i);
                    recipeCount++;
                }
                
            }
        }
        
        bf.freeBusyFlag();
        
        return recipesToReturn;
    }
    
    public void fillRecipeList(String directoryPath)
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        Document recipeDoc;
        Element rootElement;
        NodeList tempList;
        HashMap<String,String> tempHash;
        Node tempNode;
        
        try 
        {
            db = dbf.newDocumentBuilder();
            recipeDoc = db.parse(directoryPath+"/conf/recipelist.xml");
            rootElement = recipeDoc.getDocumentElement();
            tempList = rootElement.getElementsByTagName("recipe");
            
            for(int i = 0; i < tempList.getLength(); i++)
            {
                tempNode = tempList.item(i);
                tempHash = new HashMap();
                tempHash.put(tempNode.getNodeName(), tempNode.getNodeValue());
                recipeList.add(tempHash);
            }
        } catch (Exception e)
        {
            
        }
    }
    
    public void serializeRecipeList(String directoryPath)
    {
        BufferedWriter bw=null;
             
        try
        {
            bw = new BufferedWriter(
                 new FileWriter(
                 new File(directoryPath+"/conf/recipelist.xml")));
            
            bw.write("<recipes>\r\n");
        
            for(HashMap<String,String> recipe:recipeList)
            {
                bw.write("\t<recipe>\r\n");
                for(String key:recipe.keySet())
                {
                    bw.write("\t\t<"+ key+">"+recipe.get(key)+"</"+key+">\r\n");
                }
                    
                bw.write("\t</recipe>\r\n");
            }
        
            bw.write("</recipes>");
            
        } catch (IOException ioe)
        {
        }
    }
}
