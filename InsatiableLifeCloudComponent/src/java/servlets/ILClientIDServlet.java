package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import common.ClientIDManager;
import javax.servlet.annotation.WebServlet;

/**
 *
 * This class returns a client ID to anyone who makes a request.
 * 
 * @author jazzdman
 */
@WebServlet(value="/clientID")
public class ILClientIDServlet extends HttpServlet {

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ClientIDManager cm = ClientIDManager.getInstance();
        response.setContentType("text/xml;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            out.println("<clientID>");
            out.println(cm.createClientID());
            out.println("</clientID>");            
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
