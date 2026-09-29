<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Đăng Nhập</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="header.jsp" />

    <h2 class="page-title">Đăng Nhập</h2>
    <div class="message-error">${error}</div>
    <div class="message-success">${message}</div>

    <form action="LoginServlet" method="POST" class="auth-form">
        <div>
            <label>Tài khoản:</label>
            <input type="text" name="account" required>
        </div>
        <div>
            <label>Mật khẩu:</label>
            <input type="password" name="password" required>
        </div>
        <button type="submit" class="btn-submit">Đăng Nhập</button>
    </form>
</body>
</html>