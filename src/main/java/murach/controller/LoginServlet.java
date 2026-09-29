package murach.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import murach.business.Customer;
import murach.dibu.CustomerDB;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
            
        String account = request.getParameter("account");
        String pass = request.getParameter("password");
        
        Customer customer = CustomerDB.login(account, pass);
        
        if (customer != null) {
            HttpSession session = request.getSession();
            session.setAttribute("customer", customer);
            
            response.sendRedirect(request.getContextPath() + "/HomeServlet");
        } else {
            request.setAttribute("error", "Sai tài khoản hoặc mật khẩu!");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
}