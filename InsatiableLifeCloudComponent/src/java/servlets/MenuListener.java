package servlets;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletContextEvent;
import web_utils.RecipeManager;
/**
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
        rm.end();
        rm.serializeRecipeList(servletContext.getRealPath("/"));
        
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
        
        try
        {
            rm = new RecipeManager(servletContext.getRealPath("/"));
            new Thread(rm).start();
        } catch (IOException e)
        {
            rm = null;
        }
        servletContext.setAttribute("rm", rm);
       
    }
}
