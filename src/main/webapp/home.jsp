<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Trang Chủ - Cửa Hàng</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="header.jsp" />

    <h2 class="page-title">Danh sách Sản phẩm</h2>
    
    <table class="table-products">
        <thead>
            <tr>
                <th>Mã SP</th>
                <th>Tên Sản Phẩm</th>
                <th>Giá</th>
                <th>Hành động</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="product" items="${productList}">
                <tr>
                    <td>${product.product_id}</td>
                    <td>${product.variant_name}</td>
                    <td>${product.price} VNĐ</td>
                    <td>
                        <form action="CartServlet" method="POST">
                            <input type="hidden" name="action" value="add">
                            <input type="hidden" name="productId" value="${product.product_id}">
                            <button type="submit" class="btn-add">Thêm vào giỏ</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</body>
</html>