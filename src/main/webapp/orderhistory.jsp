<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Lịch Sử Mua Hàng</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="header.jsp" />

    <h2 class="page-title">Lịch Sử Mua Hàng Của Bạn</h2>

    <c:choose>
        <c:when test="${empty historyOrders}">
            <p style="text-align: center; color: gray;">Bạn chưa có đơn hàng nào.</p>
        </c:when>
        <c:otherwise>
            <c:forEach var="order" items="${historyOrders}">
                <div style="border: 1px solid #ccc; margin: 20px auto; width: 60%; padding: 15px; border-radius: 5px;">
                    <h3>Mã đơn hàng: #${order.order_id}</h3>
                    <table class="table-products" style="width: 100%; margin: 10px 0;">
                        <thead>
                            <tr>
                                <th>Tên sản phẩm</th>
                                <th>Số lượng</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="item" items="${order.items}">
                                <tr>
                                    <td>${item.item.variant_name}</td>
                                    <td>${item.quantity}</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:forEach>
        </c:otherwise>
    </c:choose>

</body>
</html>