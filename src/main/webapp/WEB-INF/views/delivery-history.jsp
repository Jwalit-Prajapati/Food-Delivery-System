<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="Trip history" scope="request"/>
<%@ include file="fragments/header.jspf" %>

<a class="back-link" href="${pageContext.request.contextPath}/delivery/dashboard">&larr; Dashboard</a>
<h1>Your trips & earnings</h1>

<div class="stats-row">
    <div class="stat-card accent">
        <span class="stat-label">Total earnings</span>
        <span class="stat-value">₹<fmt:formatNumber value="${earnings}" minFractionDigits="2" maxFractionDigits="2"/></span>
    </div>
    <div class="stat-card">
        <span class="stat-label">Completed trips</span>
        <span class="stat-value">${tripCount}</span>
    </div>
    <div class="stat-card">
        <span class="stat-label">Average per trip</span>
        <span class="stat-value">
            <c:choose>
                <c:when test="${tripCount > 0}">₹<fmt:formatNumber value="${earnings / tripCount}" minFractionDigits="2" maxFractionDigits="2"/></c:when>
                <c:otherwise>₹0.00</c:otherwise>
            </c:choose>
        </span>
    </div>
</div>

<h2 class="section-title">Recent trips</h2>
<c:if test="${empty completed}">
    <div class="empty">
        <p>You haven't completed any deliveries yet.</p>
        <a class="btn-primary" href="${pageContext.request.contextPath}/delivery/dashboard">Grab one from the queue</a>
    </div>
</c:if>

<div class="trip-table-wrap">
    <c:if test="${not empty completed}">
        <table class="items-table">
            <thead>
            <tr><th>Order</th><th>From</th><th>To</th><th>Delivered</th><th>Earnings</th></tr>
            </thead>
            <tbody>
            <c:forEach var="o" items="${completed}">
                <tr>
                    <td><a href="${pageContext.request.contextPath}/orders/${o.id}">#${o.id}</a></td>
                    <td>${restaurantMap[o.restaurantId].name}</td>
                    <td>
                        <c:if test="${not empty addressMap[o.addressId]}">
                            ${addressMap[o.addressId].city}
                        </c:if>
                    </td>
                    <td>${o.deliveryDate}</td>
                    <td><strong>₹<fmt:formatNumber value="${o.deliveryFee * 0.75}" minFractionDigits="2" maxFractionDigits="2"/></strong></td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:if>
</div>

<%@ include file="fragments/footer.jspf" %>
