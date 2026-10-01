<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Giỏ Hàng</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="header.jsp" />

    <h2 class="page-title">Giỏ hàng của bạn</h2>
    
    <table class="table-products">
        <thead>
            <tr>
                <th>Tên sản phẩm</th>
                <th>Giá</th>
                <th>Số lượng</th>
                <th>Hành động</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="item" items="${cartItems}">
                <tr>
                    <!-- Đã khôi phục lại .variant chuẩn với CartItem -->
                    <td>${item.variant.variant_name}</td>
                    <td>${item.variant.price} VNĐ</td>
                    <td>
                        <form action="CartServlet" method="POST" class="form-inline">
                            <input type="hidden" name="action" value="update">
                            <input type="hidden" name="cartItemId" value="${item.id}">
                            <input type="number" name="quantity" value="${item.quantity}" min="1">
                            <button type="submit" class="btn-update">Cập nhật</button>
                        </form>
                    </td>
                    <td>
                        <form action="CartServlet" method="POST" class="form-inline">
                            <input type="hidden" name="action" value="remove">
                            <input type="hidden" name="cartItemId" value="${item.id}">
                            <button type="submit" class="btn-remove">Xóa</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty cartItems}">
                <tr><td colspan="4">Giỏ hàng của bạn đang trống!</td></tr>
            </c:if>
        </tbody>
    </table>
    
    <div class="cart-actions" style="margin-top: 20px;">
        <a href="${pageContext.request.contextPath}/"><button class="btn-update" type="button">Tiếp tục mua sắm</button></a>
        
        <c:if test="${not empty cartItems}">
            <form action="CheckoutServlet" method="POST" class="payment-options">
                <label for="paymentMethod">Phương thức thanh toán:</label>
                <select name="paymentMethod" id="paymentMethod" required>
                    <option value="COD">Thanh toán khi nhận hàng (COD)</option>
                    <option value="VNPAY">Thanh toán qua VNPAY</option>
                </select>
                <button type="submit" class="btn-checkout">Xác nhận Đặt hàng</button>
            </form>
        </c:if>
    </div>
</body>
</html>