package murach.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import murach.business.Customer;
import murach.dibu.CustomerDB;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String account = request.getParameter("account");
        String firstName = request.getParameter("first_name");
        String lastName = request.getParameter("last_name");
        String password = request.getParameter("password");
        String gender = request.getParameter("gender");
        String mail = request.getParameter("email");

        if (CustomerDB.getCustomerByAccount(account) != null) {
            request.setAttribute("error", "Tài khoản đã tồn tại. Vui lòng chọn tên khác!");
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }

        Customer customer = new Customer();
        customer.setAccount(account);
        customer.setFirst_name(firstName);
        customer.setLast_name(lastName);
        customer.setPassword(password); 
        customer.setGender(gender);
        customer.setEmail(mail);

        if (CustomerDB.insertCustomer(customer)) {
            request.setAttribute("message", "Đăng ký thành công! Vui lòng kiểm tra email và đăng nhập.");
            
            // Xử lý gửi email
            String to = mail;
            String from = "dung2k2k6@gmail.com"; 
            String subject = "Welcome to our email list";
            String body = "Dear " + firstName + ",\n\n"
                          + "Thanks for joining our email list. "
                          + "We'll make sure to send "
                          + "you announcements about new products "
                          + "and promotions. \n"
                          + "Have a great day and thanks again!\n\n"
                          + "Kelly Slivkoff\n"
                          + "Mike Murach & Associates";
            boolean isBodyHTML = false;
            
            try {
                MailUtilGmail.sendMail(to, from, subject, body, isBodyHTML);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Lỗi gửi email: " + e.getMessage());
            }
            
            request.getRequestDispatcher("login.jsp").forward(request, response);
            
        } else {
            request.setAttribute("error", "Lỗi hệ thống. Vui lòng thử lại!");
            request.getRequestDispatcher("register.jsp").forward(request, response);
        }
    }
}