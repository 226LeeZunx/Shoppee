<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Đăng Ký</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="header.jsp" />

    <h2 class="page-title">Đăng Ký Tài Khoản</h2>
    <div class="message-error">${error}</div>

    <form action="RegisterServlet" method="POST" class="auth-form">
        <div>
            <label>Account:</label>
            <input type="text" name="account" required>
        </div>
        <div>
            <label>Password:</label>
            <input type="password" name="password" required>
        </div>
        <div>
            <label>First name::</label>
            <input type="text" name="first_name" required>
        </div>
        <div>
            <label>Last name:</label>
            <input type="text" name="last_name" required>
        </div>
         <div>
            <label>email address:</label>
            <input type="text" name="email" required>
        </div>
        <div>
            <label>Giới tính:</label>
            <select name="gender">
                <option value="Male">Nam</option>
                <option value="Female">Nữ</option>
            </select>
        </div>
        <button type="submit" class="btn-submit">Đăng Ký</button>
    </form>
</body>
</html>