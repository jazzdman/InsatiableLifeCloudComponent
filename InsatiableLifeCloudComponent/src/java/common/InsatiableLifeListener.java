/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package common;

import web_utils.RecipeManager;

import javax.servlet.ServletContextListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContext;

/**
 *
 * @author jazzdman
 */
public class InsatiableLifeListener implements ServletContextListener
{
    
    public void contextInitialized(ServletContextEvent sce)
    {
        ServletContext application = sce.getServletContext();
        
        try
        {
            RecipeManager rm = new RecipeManager(sce.getServletContext().getRealPath("/"));
            application.setAttribute("rm", rm);
            
        } catch(Exception e)
        {
            application.setAttribute("rm", null);
        }
     
        
    }
    
    public void contextDestroyed(ServletContextEvent sce)
    {
        ServletContext application = sce.getServletContext();
        RecipeManager rm = (RecipeManager)application.getAttribute("rm");
        if(rm != null)
        {    
            rm.end();
            rm.serializeRecipeList(sce.getServletContext().getRealPath("/"));
        }
        
        application.removeAttribute("rm");
    }
}
