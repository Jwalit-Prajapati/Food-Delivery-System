<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="Your orders" scope="request"/>
<%@ include file="fragments/header.jspf" %>

<h1>Your orders</h1>

<c:if test="${empty orders}">
    <div class="empty">
        <p>You haven't placed any orders yet.</p>
        <a class="btn-primary" href="${pageContext.request.contextPath}/home">Order something</a>
    </div>
</c:if>

<div class="order-list">
    <c:forEach var="o" items="${orders}">
        <a class="card pad order-card" href="${pageContext.request.contextPath}/orders/${o.id}">
            <div class="order-top">
                <div>
                    <h3>Order #${o.id}</h3>
                    <p class="muted">
                        <c:choose>
                            <c:when test="${not empty restaurantMap[o.restaurantId]}">
                                ${restaurantMap[o.restaurantId].name}
                            </c:when>
                            <c:otherwise>Restaurant #${o.restaurantId}</c:otherwise>
                        </c:choose>
                    </p>
                </div>
                <div class="order-status status-${fn:toLowerCase(o.status)}">${o.status}</div>
            </div>
            <div class="order-meta">
                <span>Placed: ${o.orderDate}</span>
                <span class="dot">•</span>
                <span>Payment: ${o.paymentStatus}</span>
                <span class="dot">•</span>
                <strong>₹<fmt:formatNumber value="${o.totalAmount}" minFractionDigits="2" maxFractionDigits="2"/></strong>
            </div>
        </a>
    </c:forEach>
</div>

<%@ include file="fragments/footer.jspf" %>
