package murach.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import murach.business.Customer;
import murach.business.Order;
import murach.dibu.OrderDB;

@WebServlet("/OrderHistoryServlet")
public class OrderHistoryServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Customer customer = (Customer) session.getAttribute("customer");

        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        List<Order> historyOrders = OrderDB.getOrdersByCustomer(customer.getCustomer_id());
        
        request.setAttribute("historyOrders", historyOrders);
        request.getRequestDispatcher("orderhistory.jsp").forward(request, response);
    }
}