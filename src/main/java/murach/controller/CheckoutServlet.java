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
import java.text.SimpleDateFormat;
import java.util.*;

import murach.business.Customer;
import murach.business.Order;
import murach.business.CartItem; 
import murach.business.Cart;
import murach.dibu.OrderDB;
import murach.dibu.CartDB;

import murach.dibu.ConfigVNPAY;
import murach.controller.MailUtilGmail;

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
        
        // Cấu hình encoding để không bị lỗi font tiếng Việt
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
            
        HttpSession session = request.getSession();
        Customer customer = (Customer) session.getAttribute("customer");

        // Bắt buộc đăng nhập trước khi thanh toán
        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String paymentMethod = request.getParameter("paymentMethod");

        // RẼ NHÁNH 1: THANH TOÁN KHI NHẬN HÀNG (COD)
        if ("COD".equals(paymentMethod)) {
            Order createdOrder = OrderDB.checkout(customer);
            if (createdOrder != null) {
                sendConfirmationEmail(customer, "Đặt hàng thành công (COD)", 
                        "Đơn hàng của bạn đã được ghi nhận. Vui lòng thanh toán tiền mặt khi nhận hàng.");
                
                request.setAttribute("recentOrder", createdOrder);
                request.getRequestDispatcher("thank.jsp").forward(request, response);
            } else {
                request.setAttribute("error", "Giỏ hàng trống hoặc có lỗi xảy ra trong quá trình lưu đơn!");
                request.getRequestDispatcher("cus_cart.jsp").forward(request, response);
            }
        } 
        
        // RẼ NHÁNH 2: THANH TOÁN QUA VNPAY
        else if ("VNPAY".equals(paymentMethod)) {
            
            // 1. Lấy thông tin cấu hình từ ConfigVNPAY
            String vnp_TmnCode = ConfigVNPAY.getConfig("VNP_TMN_CODE");
            String vnp_HashSecret = ConfigVNPAY.getConfig("VNP_HASH_SECRET");
            String vnp_PayUrl = ConfigVNPAY.getConfig("VNP_PAY_URL");
            String vnp_ReturnUrl = ConfigVNPAY.getConfig("VNP_RETURN_URL");

            if (vnp_TmnCode == null || vnp_HashSecret == null || vnp_PayUrl == null || vnp_ReturnUrl == null) {
                request.setAttribute("error", "Hệ thống thanh toán đang bảo trì (Lỗi thiếu cấu hình VNPAY)!");
                request.getRequestDispatcher("cus_cart.jsp").forward(request, response);
                return;
            }

            // 2. Tính tổng tiền động từ giỏ hàng bằng CartDB
            murach.business.Cart cart = murach.dibu.CartDB.getCart(customer);
            List<CartItem> cartItems = (cart != null) ? cart.getItems() : null;
            
            double totalCartPrice = 0;
            if (cartItems != null) {
                for (CartItem item : cartItems) {
                    if (item.getVariant() != null) {
                        totalCartPrice += item.getVariant().getPrice() * item.getQuantity();
                    }
                }
            }

            long amount = (long) (totalCartPrice * 100);

            if (amount <= 0) {
                request.setAttribute("error", "Giỏ hàng trống hoặc số tiền không hợp lệ!");
                request.getRequestDispatcher("cus_cart.jsp").forward(request, response);
                return;
            }

            // 3. Chuẩn bị tham số cho VNPAY
            String vnp_Version = "2.1.0";
            String vnp_Command = "pay";
            String vnp_OrderInfo = "Thanh toan don hang cho: " + customer.getAccount();
            String orderType = "other";
            
            // Dùng hàm sinh mã ngẫu nhiên để tránh trùng lặp mã giao dịch
            String vnp_TxnRef = vnp_TmnCode + System.currentTimeMillis(); 
            String vnp_IpAddr = request.getRemoteAddr();

            Map<String, String> vnp_Params = new HashMap<>();
            vnp_Params.put("vnp_Version", vnp_Version);
            vnp_Params.put("vnp_Command", vnp_Command);
            vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
            vnp_Params.put("vnp_Amount", String.valueOf(amount));
            vnp_Params.put("vnp_CurrCode", "VND");
            
            // Đã xóa vnp_BankCode để VNPAY hiển thị cổng chọn phương thức
            vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
            vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
            vnp_Params.put("vnp_OrderType", orderType);
            vnp_Params.put("vnp_Locale", "vn");
            vnp_Params.put("vnp_ReturnUrl", vnp_ReturnUrl);
            vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

            Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            vnp_Params.put("vnp_CreateDate", formatter.format(cld.getTime()));
            
            cld.add(Calendar.MINUTE, 15);
            vnp_Params.put("vnp_ExpireDate", formatter.format(cld.getTime()));

            List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
            Collections.sort(fieldNames);
            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();
            
            Iterator<String> itr = fieldNames.iterator();
            while (itr.hasNext()) {
                String fieldName = (String) itr.next();
                String fieldValue = (String) vnp_Params.get(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    
                    if (itr.hasNext()) {
                        query.append('&');
                        hashData.append('&');
                    }
                }
            }

            // Gọi hash HMAC SHA512
            String vnp_SecureHash = ConfigVNPAY.hmacSHA512(vnp_HashSecret, hashData.toString());
            query.append("&vnp_SecureHash=").append(vnp_SecureHash);
            
            // Redirect sang VNPAY
            String paymentUrl = vnp_PayUrl + "?" + query.toString();
            response.sendRedirect(paymentUrl);
            
        } 
        else {
            request.setAttribute("error", "Vui lòng chọn phương thức thanh toán!");
            request.getRequestDispatcher("cus_cart.jsp").forward(request, response);
        }
    }

    private void sendConfirmationEmail(Customer customer, String subjectPrefix, String customMessage) {
        String from = ConfigVNPAY.getConfig("MAIL_SEND");
        
        if (from == null || from.isEmpty()) {
            System.err.println("CẢNH BÁO: Chưa cấu hình MAIL_SEND trong file cấu hình!");
            return;
        }
        
        String to = customer.getEmail();
        String body = "Xin chào " + customer.getFirst_name() + " " + customer.getLast_name() + ",\n\n"
                    + customMessage + "\n"
                    + "Chúng tôi đang tiến hành đóng gói và sẽ sớm giao đến tay bạn.\n\n"
                    + "Trân trọng,\nĐội ngũ hỗ trợ";
        try {
            MailUtilGmail.sendMail(to, from, subjectPrefix, body, false);
        } catch (Exception e) {
            System.err.println("Lỗi gửi mail: " + e.getMessage());
            e.printStackTrace();
        }
    }
}