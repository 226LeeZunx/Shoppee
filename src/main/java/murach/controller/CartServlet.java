/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package murach.controller;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import murach.business.Cart;
import murach.business.Customer;
import murach.dibu.CartDB;

/**
 *
 * @author dung2
 */
@WebServlet(name = "CartServlet", urlPatterns = {"/CartServlet"})
public class CartServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Customer customer = (Customer) session.getAttribute("customer");

        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/LoginServlet");
            return;
        }

        String action = request.getParameter("action");
        if (action == null) action = "view";

        Cart cart = CartDB.getCart(customer); 

        switch (action) {
            case "add":
                int productId = Integer.parseInt(request.getParameter("productId"));
                CartDB.addOrUpdateItem(cart, productId);
                break;
            case "update":
                {
                    int cartItemId = Integer.parseInt(request.getParameter("cartItemId"));
                    int qty = Integer.parseInt(request.getParameter("quantity"));
                    CartDB.updateQuantity(cartItemId, qty);
                    break;
                }
            case "remove":
                {
                    int cartItemId = Integer.parseInt(request.getParameter("cartItemId"));
                    CartDB.removeItem(cartItemId);
                    break;
                }
            default:
                break;
        }

        request.setAttribute("cartItems", CartDB.getCart(customer).getItems());
        request.getRequestDispatcher("cus_cart.jsp").forward(request, response);
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doPost(request, response); 
    }
}
