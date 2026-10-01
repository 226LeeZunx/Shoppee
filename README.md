# Shoppee

**Công nghệ:** JSP/JSTL · Jakarta Servlet · JPA (`emailListPU`) · PostgreSQL (Neon) · Brevo API (gửi mail) · Deploy trên Render

## Cấu trúc

```
murach
├── controller  HomeServlet, LoginServlet, LogoutServlet, RegisterServlet,
│               CartServlet, CheckoutServlet, OrderHistoryServlet, MailUtilGmail
├── business    Customer, Product, Cart, CartItem, Order, OrderItem   (Entity)
└── dibu        DBUtil, CustomerDB, ProductDB, CartDB, OrderDB        (DAO)
```

## Class diagram (package `business`)

```mermaid
classDiagram
    direction LR
    class Customer {
        -int customer_id
        -String account
        -String first_name
        -String last_name
        -String password
        -String email
        -String gender
        -List~Order~ orders
        -Cart cart
    }
    class Product {
        -int product_id
        -String variant_name
        -double price
    }
    class Cart {
        -int cart_id
        -Customer customer
        -List~CartItem~ items
    }
    class CartItem {
        -int id
        -Cart cart
        -Product variant
        -int quantity
    }
    class Order {
        -int order_id
        -Customer customer
        -List~OrderItem~ items
    }
    class OrderItem {
        -int id
        -Order order
        -Product item
        -int quantity
    }

    Customer "1" --> "0..*" Order : orders
    Customer "1" --> "0..1" Cart : cart
    Cart "1" *-- "0..*" CartItem : items
    Order "1" *-- "1..*" OrderItem : items
    CartItem "*" --> "1" Product : variant
    OrderItem "*" --> "1" Product : item
```

## Luồng chức năng

| Chức năng | Luồng & phương thức chính |
|---|---|
| Trang chủ | `HomeServlet` → `ProductDB.getAllProducts()` → `home.jsp` |
| Đăng ký | `RegisterServlet` → `CustomerDB.getCustomerByAccount()` (kiểm tra trùng) → `insertCustomer()` → `MailUtilGmail.sendMail()` → `login.jsp` |
| Đăng nhập | `LoginServlet` → `CustomerDB.login()` → lưu `customer` vào session → redirect `HomeServlet` |
| Đăng xuất | `LogoutServlet`: `session.invalidate()` → về trang chủ |
| Giỏ hàng | `CartServlet` (`action` = add / update / remove) → `CartDB.getCart()` (tự tạo giỏ nếu chưa có), `addOrUpdateItem()`, `updateQuantity()` (qty ≤ 0 thì xóa), `removeItem()` → `cus_cart.jsp` |
| Thanh toán | `CheckoutServlet` → `OrderDB.checkout()` → gửi mail xác nhận → `thank.jsp` |
| Lịch sử | `OrderHistoryServlet` → `OrderDB.getOrdersByCustomer()` → `orderhistory.jsp` |


## Cấu hình (biến môi trường trên Render / file `.env`)

`DB_URL`, `DB_USER`, `DB_PASSWORD` (Neon) · `BREVO_API_KEY`, `MAIL_SEND` (gửi mail)

`DBUtil` tự nạp các biến này và để JPA tự tạo bảng (`schema-generation = create`).


