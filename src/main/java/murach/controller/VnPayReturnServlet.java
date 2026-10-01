package murach.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import murach.business.Customer;
import murach.business.Order;
import murach.dibu.OrderDB;
import murach.dibu.ConfigVNPAY;

@WebServlet("/VnPayReturnServlet")
public class VnPayReturnServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        // 1. Lấy toàn bộ tham số VNPAY trả về
        Map<String, String> fields = new HashMap<>();
        for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements();) {
            String fieldName = URLEncoder.encode((String) params.nextElement(), StandardCharsets.US_ASCII.toString());
            String fieldValue = URLEncoder.encode(request.getParameter(fieldName), StandardCharsets.US_ASCII.toString());
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                fields.put(fieldName, fieldValue);
            }
        }

        // Tách chữ ký VNPAY gửi về để đối chiếu
        String vnp_SecureHash = request.getParameter("vnp_SecureHash");
        if (fields.containsKey("vnp_SecureHashType")) fields.remove("vnp_SecureHashType");
        if (fields.containsKey("vnp_SecureHash")) fields.remove("vnp_SecureHash");

        // 2. Tạo chữ ký (checksum) từ phía hệ thống của bạn để so sánh
        String signValue = ConfigVNPAY.hashAllFields(fields);

        // 3. Kiểm tra tính hợp lệ của dữ liệu
        if (signValue.equals(vnp_SecureHash)) {
            // Mã "00" nghĩa là VNPAY xác nhận giao dịch đã trừ tiền thành công
            if ("00".equals(request.getParameter("vnp_ResponseCode"))) {
                
                HttpSession session = request.getSession();
                Customer customer = (Customer) session.getAttribute("customer");
                
                if (customer != null) {
                    // BƯỚC 1: LƯU ĐƠN HÀNG VÀO DATABASE VÀ XÓA GIỎ HÀNG
                    Order createdOrder = OrderDB.checkout(customer);
                    
                    if (createdOrder != null) {
                        // BƯỚC 2: GỬI MAIL CONFIRM
                        sendVnPaySuccessEmail(customer);
                        
                        // BƯỚC 3: CHUYỂN HƯỚNG VỀ TRANG THANK.JSP
                        request.setAttribute("recentOrder", createdOrder);
                        request.getRequestDispatcher("thank.jsp").forward(request, response);
                        return; // Kết thúc luồng tại đây
                    } else {
                        request.setAttribute("error", "Thanh toán thành công nhưng có lỗi khi lưu đơn hàng vào hệ thống!");
                    }
                } else {
                    request.setAttribute("error", "Phiên đăng nhập đã hết hạn. Không thể lưu đơn hàng!");
                }
            } else {
                request.setAttribute("error", "Giao dịch VNPAY thất bại hoặc đã bị hủy bởi người dùng.");
            }
        } else {
            request.setAttribute("error", "Chữ ký VNPAY không hợp lệ (Cảnh báo giả mạo giao dịch)!");
        }

        // Nếu lọt xuống đây nghĩa là có lỗi, trả về trang giỏ hàng kèm thông báo lỗi
        request.getRequestDispatcher("cus_cart.jsp").forward(request, response);
    }

    // Hàm gửi email thông báo thanh toán VNPAY thành công
    private void sendVnPaySuccessEmail(Customer customer) {
        String from = ConfigVNPAY.getConfig("MAIL_SEND");
        if (from == null || from.isEmpty()) {
            from = "dunglt226@gmail.com";
        }
        
        String to = customer.getEmail();
        String subject = "Xác nhận đặt hàng và thanh toán thành công (VNPAY)";
        String body = "Xin chào " + customer.getFirst_name() + " " + customer.getLast_name() + ",\n\n"
                    + "Hệ thống đã nhận được khoản thanh toán qua cổng VNPAY cho đơn hàng mới nhất của bạn.\n"
                    + "Chúng tôi đang tiến hành đóng gói và sẽ sớm giao hàng đến bạn.\n\n"
                    + "Cảm ơn bạn đã mua sắm!\n"
                    + "Trân trọng,\n"
                    + "Đội ngũ hỗ trợ";
        try {
            MailUtilGmail.sendMail(to, from, subject, body, false);
        } catch (Exception e) {
            System.err.println("Lỗi gửi mail (VnPayReturnServlet): " + e.getMessage());
            e.printStackTrace();
        }
    }
}