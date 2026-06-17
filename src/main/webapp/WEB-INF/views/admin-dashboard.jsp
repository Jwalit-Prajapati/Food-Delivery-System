<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Admin dashboard" scope="request"/>
<c:set var="autoRefreshSeconds" value="15" scope="request"/>
<%@ include file="fragments/header.jspf" %>

<div class="order-detail-head">
    <div>
        <h1>Admin dashboard</h1>
        <p class="muted">Platform health at a glance.</p>
    </div>
    <c:if test="${pendingCount > 0}">
        <a class="btn-primary" href="${pageContext.request.contextPath}/admin/restaurants?filter=pending">
            ${pendingCount} restaurant<c:if test="${pendingCount > 1}">s</c:if> awaiting approval →
        </a>
    </c:if>
</div>

<h2 class="section-title">Today</h2>
<div class="stats-grid">
    <div class="stat-card accent">
        <span class="stat-label">Revenue today</span>
        <span class="stat-value">₹<fmt:formatNumber value="${metrics.revenueToday}" minFractionDigits="2" maxFractionDigits="2"/></span>
    </div>
    <div class="stat-card">
        <span class="stat-label">Orders today</span>
        <span class="stat-value">${metrics.ordersToday}</span>
    </div>
    <div class="stat-card">
        <span class="stat-label">Awaiting accept</span>
        <span class="stat-value">${metrics.placedNow}</span>
    </div>
    <div class="stat-card">
        <span class="stat-label">In progress</span>
        <span class="stat-value">${metrics.activeOrders}</span>
    </div>
</div>

<h2 class="section-title">All time</h2>
<div class="stats-grid">
    <div class="stat-card">
        <span class="stat-label">Total revenue</span>
        <span class="stat-value">₹<fmt:formatNumber value="${metrics.revenueAllTime}" minFractionDigits="2" maxFractionDigits="2"/></span>
    </div>
    <div class="stat-card">
        <span class="stat-label">Total orders</span>
        <span class="stat-value">${metrics.totalOrders}</span>
    </div>
    <div class="stat-card">
        <span class="stat-label">Delivered</span>
        <span class="stat-value">${metrics.delivered}</span>
    </div>
</div>

<h2 class="section-title">People</h2>
<div class="stats-grid">
    <div class="stat-card">
        <span class="stat-label">Total users</span>
        <span class="stat-value">${metrics.totalUsers}</span>
    </div>
    <div class="stat-card">
        <span class="stat-label">Customers</span>
        <span class="stat-value">${metrics.customers}</span>
    </div>
    <div class="stat-card">
        <span class="stat-label">Restaurant owners</span>
        <span class="stat-value">${metrics.restaurantOwners}</span>
    </div>
    <div class="stat-card">
        <span class="stat-label">Delivery partners</span>
        <span class="stat-value">${metrics.deliveryPartners}</span>
    </div>
</div>

<h2 class="section-title">Restaurants</h2>
<div class="stats-grid">
    <div class="stat-card">
        <span class="stat-label">Total restaurants</span>
        <span class="stat-value">${metrics.totalRestaurants}</span>
    </div>
    <div class="stat-card">
        <span class="stat-label">Live (verified + active)</span>
        <span class="stat-value">${metrics.activeRestaurants}</span>
    </div>
    <div class="stat-card ${metrics.pendingRestaurants > 0 ? 'warn' : ''}">
        <span class="stat-label">Pending approval</span>
        <span class="stat-value">${metrics.pendingRestaurants}</span>
    </div>
</div>

<div class="quick-links">
    <a class="card pad" href="${pageContext.request.contextPath}/admin/users">
        <h3>👥 Manage users</h3>
        <p class="muted">Suspend or reactivate any account.</p>
    </a>
    <a class="card pad" href="${pageContext.request.contextPath}/admin/restaurants">
        <h3>🏪 Manage restaurants</h3>
        <p class="muted">Approve new partners or take listings offline.</p>
    </a>
</div>

<%@ include file="fragments/footer.jspf" %>
