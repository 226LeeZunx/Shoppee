<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Cảm ơn bạn</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="header.jsp" />

    <div class="bill-box">
        <h1>Cảm ơn bạn đã mua hàng!</h1>
        <p>Đơn hàng của bạn đã được xử lý thành công.</p>
        <hr>
        
        <c:if test="${not empty recentOrder}">
            <h3>Hóa đơn chi tiết</h3>
            <div style="text-align: left; display: inline-block; width: 80%;">
                <p><b>Khách hàng:</b> ${recentOrder.customer.first_name} ${recentOrder.customer.last_name}</p>
                
                <table class="table-products">
                    <tr>
                        <th>Sản phẩm</th>
                        <th>Số lượng</th>
                    </tr>
                    <c:forEach var="oItem" items="${recentOrder.items}">
                        <tr>
                            <td>${oItem.item.variant_name}</td>
                            <td>${oItem.quantity}</td>
                        </tr>
                    </c:forEach>
                </table>
            </div>
        </c:if>

        <div style="margin-top: 20px;">
            <a href="${pageContext.request.contextPath}/"><button class="btn-checkout">Quay về trang chủ</button></a>
        </div>
    </div>
</body>
</html>