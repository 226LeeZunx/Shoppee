package murach.controller;

import io.github.cdimascio.dotenv.Dotenv;
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
        
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
            
        HttpSession session = request.getSession();
        Customer customer = (Customer) session.getAttribute("customer");

        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        Order createdOrder = OrderDB.checkout(customer);
        if (createdOrder != null) {
            
            String from = null;
            try {
                Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
                from = dotenv.get("MAIL_SEND");
            } catch (Exception e) {}

            if (from == null || from.isEmpty()) {
                from = System.getenv("MAIL_SEND");
            }
            if (from == null || from.isEmpty()) {
                from = System.getProperty("MAIL_SEND");
            }
           

            String to = customer.getEmail();
            String subject = "Xác nhận đặt hàng thành công";
            String body = "Xin chào " + customer.getFirst_name() + " " + customer.getLast_name() + ",\n\n"
                        + "Cảm ơn bạn đã mua sắm tại hệ thống của chúng tôi. Đơn hàng của bạn đã được tiếp nhận và thanh toán thành công.\n"
                        + "Chúng tôi đang tiến hành đóng gói và sẽ sớm giao đến tay bạn.\n\n"
                        + "Chúc bạn một ngày tốt lành!\n"
                        + "Trân trọng,\n"
                        + "Đội ngũ hỗ trợ khách hàng";

            try {
                MailUtilGmail.sendMail(to, from, subject, body, false);
            } catch (Exception e) {
                // In lỗi ra console thay vì làm đứng trang web nếu mail gặp sự cố
                System.err.println("Thanh toán thành công nhưng gửi mail thất bại: " + e.getMessage());
                e.printStackTrace();
            }

            request.setAttribute("recentOrder", createdOrder);
            request.getRequestDispatcher("thank.jsp").forward(request, response);
        } else {
            request.setAttribute("error", "Giỏ hàng của bạn đang trống hoặc có lỗi xảy ra!");
            request.getRequestDispatcher("cus_cart.jsp").forward(request, response);
        }
    }
}