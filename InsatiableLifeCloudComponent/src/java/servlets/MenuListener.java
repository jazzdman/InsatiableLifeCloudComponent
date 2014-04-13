package servlets;

import java.io.IOException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletContextEvent;
import common.RecipeManager;
/**
 *
 * This class receives the servlet start and end events.  Upon startup, it 
 * starts a RecipeManager, and puts it into Application scope.  Upon shutdown,
 * it removes the RecipeManager from Application scope.
 * 
 * @author jazzdman
 */
public class MenuListener implements ServletContextListener
{

    /**
     *
     * @param sce
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce)
    {
        ServletContext servletContext = sce.getServletContext();
        RecipeManager rm = (RecipeManager)servletContext.getAttribute("rm");
        
        if(rm != null)
        {
            rm.end();
            rm.serializeRecipeList(servletContext.getRealPath("/"));
        }
            
        servletContext.removeAttribute("rm");
       
            
    }
    
    /**
     *
     * @param sce
     */
    @Override
    public void contextInitialized(ServletContextEvent sce)
    {
        ServletContext servletContext = sce.getServletContext();
        RecipeManager rm;
        Thread th;
        
        try
        {
            rm = new RecipeManager(servletContext.getRealPath("/"));
            th = new Thread(rm);
            th.setDaemon(true);
            th.start();
        } catch (IOException e)
        {
            rm = null;
        }
        
        if (rm != null)
        {
            servletContext.setAttribute("rm", rm);
        }
    }
}
