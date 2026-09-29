<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<header >
    <div>
        <a href="HomeServlet">Trang Chủ</a>
    </div>
    <div>
        <c:choose>
            <c:when test="${not empty sessionScope.customer}">
                <span>Xin chào, <b>${sessionScope.customer.first_name} ${sessionScope.customer.last_name}</b>!</span>
                <a href="CartServlet"><button>Giỏ hàng</button></a>
                <a href="OrderHistoryServlet"><button>Lịch sử mua hàng</button></a>
                <a href="LogoutServlet"><button>Đăng xuất</button></a>
            </c:when>
            <c:otherwise>
                <a href="login.jsp"><button>Đăng nhập</button></a>
                <a href="register.jsp"><button>Đăng ký</button></a>
            </c:otherwise>
        </c:choose>
    </div>
</header>