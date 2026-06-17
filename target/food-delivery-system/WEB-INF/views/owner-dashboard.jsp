<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="Owner dashboard" scope="request"/>
<%@ include file="fragments/header.jspf" %>

<h1>Owner dashboard</h1>
<p class="muted">Hi ${currentUser.name}, manage your restaurants below.</p>

<div class="layout-two-col">
    <div>
        <h2 class="section-title">Your restaurants</h2>
        <c:if test="${empty restaurants}">
            <div class="empty">No restaurants yet. Add your first one →</div>
        </c:if>
        <div class="grid">
            <c:forEach var="r" items="${restaurants}">
                <div class="card pad">
                    <h3>${r.name}</h3>
                    <p class="muted">${r.cuisineType}</p>
                    <div class="meta">
                        <span class="rating">★ <fmt:formatNumber value="${r.rating}" minFractionDigits="1" maxFractionDigits="1"/></span>
                        <span class="dot">•</span>
                        <span>${r.active ? 'Active' : 'Inactive'}</span>
                    </div>
                    <div class="actions">
                        <a class="btn-primary btn-sm" href="${pageContext.request.contextPath}/owner/restaurants/${r.id}/menu">Manage menu</a>
                    </div>
                </div>
            </c:forEach>
        </div>

        <h2 class="section-title">Recent orders</h2>
        <c:if test="${empty recentOrders}">
            <p class="muted">No orders yet.</p>
        </c:if>
        <div class="order-list">
            <c:forEach var="o" items="${recentOrders}">
                <div class="card pad order-card">
                    <div class="order-top">
                        <div>
                            <h3>Order #${o.id}</h3>
                            <p class="muted">User #${o.userId} &middot; ${o.orderDate}</p>
                        </div>
                        <span class="order-status status-${fn:toLowerCase(o.status)}">${o.status}</span>
                    </div>
                    <div class="order-meta">
                        <strong>₹<fmt:formatNumber value="${o.totalAmount}" minFractionDigits="2" maxFractionDigits="2"/></strong>
                        <span class="dot">•</span>
                        <span>Payment: ${o.paymentStatus}</span>
                    </div>
                    <c:if test="${o.status == 'PLACED'}">
                        <div class="inline-form">
                            <form method="post" action="${pageContext.request.contextPath}/owner/orders/${o.id}/accept" style="display:inline">
                                <button type="submit" class="btn-primary btn-sm">Accept</button>
                            </form>
                            <form method="post" action="${pageContext.request.contextPath}/owner/orders/${o.id}/reject" style="display:inline" onsubmit="return confirm('Reject this order?')">
                                <button type="submit" class="btn-danger btn-sm">Reject</button>
                            </form>
                        </div>
                    </c:if>
                    <c:if test="${o.status == 'CONFIRMED'}">
                        <div class="inline-form">
                            <form method="post" action="${pageContext.request.contextPath}/owner/orders/${o.id}/status" style="display:inline">
                                <input type="hidden" name="status" value="PREPARING">
                                <button type="submit" class="btn-primary btn-sm">Start preparing</button>
                            </form>
                        </div>
                    </c:if>
                    <c:if test="${o.status == 'PREPARING'}">
                        <div class="inline-form">
                            <form method="post" action="${pageContext.request.contextPath}/owner/orders/${o.id}/ready" style="display:inline">
                                <button type="submit" class="btn-primary btn-sm">Mark ready for pickup</button>
                            </form>
                        </div>
                    </c:if>
                    <c:if test="${o.status == 'READY_FOR_PICKUP'}">
                        <p class="muted" style="margin-top:10px">Waiting for a delivery partner to claim this order.</p>
                    </c:if>
                </div>
            </c:forEach>
        </div>
    </div>

    <aside class="side">
        <div class="card pad">
            <h3>Add a new restaurant</h3>
            <form method="post" action="${pageContext.request.contextPath}/owner/restaurants/add" class="form">
                <label><span>Name</span><input type="text" name="name" required></label>
                <label><span>Description</span><textarea name="description" rows="2"></textarea></label>
                <label><span>Cuisine</span><input type="text" name="cuisineType" placeholder="e.g. Italian"></label>
                <label><span>Phone</span><input type="tel" name="phone"></label>
                <div class="form-row">
                    <label><span>Opens at</span><input type="time" name="opensAt"></label>
                    <label><span>Closes at</span><input type="time" name="closesAt"></label>
                </div>

                <h4 style="margin:14px 0 6px">Restaurant address</h4>
                <label><span>Street</span><input type="text" name="street" required></label>
                <div class="form-row">
                    <label><span>City</span><input type="text" name="city" required></label>
                    <label><span>State</span><input type="text" name="state" required></label>
                </div>
                <div class="form-row">
                    <label><span>ZIP code</span><input type="text" name="zipCode" required></label>
                    <label><span>Country</span><input type="text" name="country" value="India"></label>
                </div>
                <label><span>Landmark (optional)</span><input type="text" name="landmark"></label>

                <button type="submit" class="btn-primary btn-block">Create restaurant</button>
            </form>
        </div>
    </aside>
</div>

<%@ include file="fragments/footer.jspf" %>
