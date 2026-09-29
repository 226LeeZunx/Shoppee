package murach.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import murach.business.Customer;
import murach.business.Order;
import murach.dibu.OrderDB;

@WebServlet("/CheckoutServlet")
public class CheckoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/CartServlet");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
            
        HttpSession session = request.getSession();
        Customer customer = (Customer) session.getAttribute("customer");

        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        Order createdOrder = OrderDB.checkout(customer);
        if (createdOrder != null) {
            request.setAttribute("recentOrder", createdOrder);
            request.getRequestDispatcher("thank.jsp").forward(request, response);
        } else {
            request.setAttribute("error", "Giỏ hàng của bạn đang trống hoặc có lỗi xảy ra!");
            request.getRequestDispatcher("cus_cart.jsp").forward(request, response);
        }
    }
}