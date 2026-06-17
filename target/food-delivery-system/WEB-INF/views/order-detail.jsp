<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="Order #${order.id}" scope="request"/>
<%@ include file="fragments/header.jspf" %>

<a class="back-link" href="${pageContext.request.contextPath}/orders">&larr; All orders</a>

<div class="order-detail-head">
    <div>
        <h1>Order #${order.id}</h1>
        <p class="muted">
            <c:if test="${not empty restaurant}">From <strong>${restaurant.name}</strong> &middot; </c:if>
            Placed on ${order.orderDate}
        </p>
    </div>
    <span class="order-status status-${fn:toLowerCase(order.status)}">${order.status}</span>
</div>

<div class="layout-two-col">
    <div>
        <div class="card pad">
            <h3>Items</h3>
            <table class="items-table">
                <thead>
                <tr><th>Item</th><th>Qty</th><th>Price</th><th>Subtotal</th></tr>
                </thead>
                <tbody>
                <c:forEach var="it" items="${order.items}">
                    <tr>
                        <td>${it.foodItemName}</td>
                        <td>${it.quantity}</td>
                        <td>₹<fmt:formatNumber value="${it.price}" minFractionDigits="2" maxFractionDigits="2"/></td>
                        <td>₹<fmt:formatNumber value="${it.subtotal}" minFractionDigits="2" maxFractionDigits="2"/></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>

        <div class="card pad">
            <h3>Status timeline</h3>
            <ol class="timeline">
                <c:forEach var="s" items="${['PLACED','CONFIRMED','PREPARING','READY_FOR_PICKUP','OUT_FOR_DELIVERY','DELIVERED']}">
                    <c:set var="active" value="false"/>
                    <c:choose>
                        <c:when test="${s == 'PLACED'}"><c:set var="active" value="true"/></c:when>
                        <c:when test="${s == 'CONFIRMED' and (order.status == 'CONFIRMED' or order.status == 'PREPARING' or order.status == 'READY_FOR_PICKUP' or order.status == 'OUT_FOR_DELIVERY' or order.status == 'DELIVERED')}"><c:set var="active" value="true"/></c:when>
                        <c:when test="${s == 'PREPARING' and (order.status == 'PREPARING' or order.status == 'READY_FOR_PICKUP' or order.status == 'OUT_FOR_DELIVERY' or order.status == 'DELIVERED')}"><c:set var="active" value="true"/></c:when>
                        <c:when test="${s == 'READY_FOR_PICKUP' and (order.status == 'READY_FOR_PICKUP' or order.status == 'OUT_FOR_DELIVERY' or order.status == 'DELIVERED')}"><c:set var="active" value="true"/></c:when>
                        <c:when test="${s == 'OUT_FOR_DELIVERY' and (order.status == 'OUT_FOR_DELIVERY' or order.status == 'DELIVERED')}"><c:set var="active" value="true"/></c:when>
                        <c:when test="${s == 'DELIVERED' and order.status == 'DELIVERED'}"><c:set var="active" value="true"/></c:when>
                    </c:choose>
                    <li class="${active ? 'done' : ''}">${fn:replace(s, '_', ' ')}</li>
                </c:forEach>
                <c:if test="${order.status == 'CANCELLED'}">
                    <li class="cancelled">Cancelled</li>
                </c:if>
                <c:if test="${order.status == 'REJECTED'}">
                    <li class="cancelled">Rejected by restaurant</li>
                </c:if>
            </ol>
        </div>
    </div>

    <aside class="side">
        <div class="card pad">
            <h3>Bill</h3>
            <div class="row"><span>Subtotal</span><span>₹<fmt:formatNumber value="${order.totalAmount - order.taxAmount - order.deliveryFee}" minFractionDigits="2" maxFractionDigits="2"/></span></div>
            <div class="row"><span>Tax</span><span>₹<fmt:formatNumber value="${order.taxAmount}" minFractionDigits="2" maxFractionDigits="2"/></span></div>
            <div class="row"><span>Delivery</span><span>₹<fmt:formatNumber value="${order.deliveryFee}" minFractionDigits="2" maxFractionDigits="2"/></span></div>
            <hr>
            <div class="row total"><strong>Total</strong><strong>₹<fmt:formatNumber value="${order.totalAmount}" minFractionDigits="2" maxFractionDigits="2"/></strong></div>
            <p class="muted">Payment: ${order.paymentMethod} (${order.paymentStatus})</p>
        </div>

        <c:if test="${not empty address}">
            <div class="card pad">
                <h3>Delivery address</h3>
                <p>
                    ${address.street}<br>
                    ${address.city}, ${address.state} - ${address.zipCode}
                    <c:if test="${not empty address.landmark}"><br><span class="muted">Near ${address.landmark}</span></c:if>
                </p>
            </div>
        </c:if>

        <c:if test="${order.status != 'DELIVERED' and order.status != 'CANCELLED' and order.status != 'OUT_FOR_DELIVERY'}">
            <form method="post" action="${pageContext.request.contextPath}/orders/${order.id}/cancel" onsubmit="return confirm('Cancel this order?')">
                <button type="submit" class="btn-danger btn-block">Cancel order</button>
            </form>
        </c:if>
    </aside>
</div>

<%@ include file="fragments/footer.jspf" %>
