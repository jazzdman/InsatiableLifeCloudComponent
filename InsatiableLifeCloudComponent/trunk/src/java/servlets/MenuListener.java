package servlets;

import common.ClientIDManager;
import common.GroceryListManager;
import common.PantryListManager;
import java.io.IOException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletContextEvent;
import common.RecipeManager;
/**
 *
 * This class receives the servlet start and end events.  Upon startup, it 
 * starts a RecipeManager, and puts it into Application scope.  Upon shutdown,
 * it removes the RecipeManager from Application scope and save the contents
 * of the ClientIDManager.
 * 
 * @author jazzdman
 */
public class MenuListener implements ServletContextListener
{

    /**
     *
     * @param sce the object from which we get information about the 
     * InsatiableLifeCloudComponent application.
     * 
     * This method is called when the application is closed.  It removes
     * the RecipeManager from the application context.  It also saves the 
     * contents of the ClientIDManager to file.
     * 
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce)
    {
        ServletContext servletContext = sce.getServletContext();
        String filePath = servletContext.getRealPath("/");
        
        //Get the RecipeManager, stop the thread that is running it, save
        // the contents of the RecipeManager and remove the RecipeManager from
        // the application context.
        RecipeManager rm = (RecipeManager)servletContext.getAttribute("rm");
        if(rm == null)
        {
            return;
        }
        rm.end();
        servletContext.removeAttribute("rm");
        
        // Save the contents of the various managers
        try
        {
            rm.serializeRecipeList(filePath);
            ClientIDManager.getInstance().serializeClientList(filePath);
            GroceryListManager.getInstance().serializeGroceryList(filePath);
            PantryListManager.getInstance().serializePantryList(filePath);
        } catch (Exception e)
        {
        }
           
    }
    
    /**
     *
     * @param sce the object from which we get information about the 
     * InsatiableLifeCloudComponent application.
     * 
     * This method is called when the application is started.  It adds
     * the RecipeManager to the application context.  
     * 
     */
    @Override
    public void contextInitialized(ServletContextEvent sce)
    {
        ServletContext servletContext = sce.getServletContext();
        RecipeManager rm;
        Thread th;
        String filePath = servletContext.getRealPath("/");
        
        // Create the RecipeManager, start the RecipeManager with a thread.  
        // Use this method instead of having RecipeManager extend Thread.  
        // Extending Thread seems to hold up web application start up.
        try
        {
            rm = new RecipeManager();
            rm.fillRecipeList(filePath);
            th = new Thread(rm);
            th.setDaemon(true);
            th.start();
        } catch (Exception e)
        {
            rm = null;
        }
        
        try 
        {
            GroceryListManager.getInstance().fillGroceryList(filePath);
            PantryListManager.getInstance().fillPantryList(filePath);
            ClientIDManager.getInstance().fillClientList(filePath);
            
        } catch (Exception e)
        {
            
        }
        
        // If we were able to instantiate the RecipeManager, add it to the 
        // application context.
        if (rm != null)
        {
            servletContext.setAttribute("rm", rm);
        }
        
    }
}
