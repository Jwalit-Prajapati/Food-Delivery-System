<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="Delivery dashboard" scope="request"/>
<%@ include file="fragments/header.jspf" %>

<div class="order-detail-head">
    <div>
        <h1>Hi ${currentUser.name} 🛵</h1>
        <p class="muted">Pick up an order from the queue, then mark it delivered when you're done.</p>
    </div>
    <a class="btn-link" href="${pageContext.request.contextPath}/delivery/history">Trip history →</a>
</div>

<h2 class="section-title">Your active deliveries
    <c:if test="${not empty active}"><span class="badge muted-badge">${fn:length(active)}</span></c:if>
</h2>
<c:if test="${empty active}">
    <p class="muted">You don't have any active deliveries right now. Grab one from the queue below.</p>
</c:if>
<div class="order-list">
    <c:forEach var="o" items="${active}">
        <div class="card pad">
            <div class="order-top">
                <div>
                    <h3>Order #${o.id}</h3>
                    <p class="muted">
                        From <strong>${restaurantMap[o.restaurantId].name}</strong>
                    </p>
                </div>
                <span class="order-status status-${fn:toLowerCase(o.status)}">${fn:replace(o.status, '_', ' ')}</span>
            </div>

            <div class="delivery-meta">
                <div>
                    <span class="muted">Picked up:</span>
                    <strong>${o.pickedUpAt}</strong>
                </div>
                <c:if test="${not empty addressMap[o.addressId]}">
                    <div>
                        <span class="muted">Deliver to:</span>
                        <strong>${addressMap[o.addressId].street}, ${addressMap[o.addressId].city}</strong>
                        <c:if test="${not empty addressMap[o.addressId].landmark}">
                            <span class="muted">(near ${addressMap[o.addressId].landmark})</span>
                        </c:if>
                    </div>
                </c:if>
                <div>
                    <span class="muted">Your cut:</span>
                    <strong>₹<fmt:formatNumber value="${o.deliveryFee * 0.75}" minFractionDigits="2" maxFractionDigits="2"/></strong>
                </div>
            </div>

            <form method="post" action="${pageContext.request.contextPath}/delivery/orders/${o.id}/delivered" style="margin-top:10px"
                  onsubmit="return confirm('Mark order #${o.id} as delivered?')">
                <button type="submit" class="btn-primary">Mark as delivered</button>
            </form>
        </div>
    </c:forEach>
</div>

<h2 class="section-title">Pickup queue
    <c:if test="${not empty queue}"><span class="badge muted-badge">${fn:length(queue)} waiting</span></c:if>
</h2>
<c:if test="${empty queue}">
    <div class="empty">No orders ready for pickup right now. Check back in a moment.</div>
</c:if>
<div class="order-list">
    <c:forEach var="o" items="${queue}">
        <div class="card pad">
            <div class="order-top">
                <div>
                    <h3>Order #${o.id}</h3>
                    <p class="muted">From <strong>${restaurantMap[o.restaurantId].name}</strong></p>
                </div>
                <span class="order-status status-${fn:toLowerCase(o.status)}">${fn:replace(o.status, '_', ' ')}</span>
            </div>

            <div class="delivery-meta">
                <c:if test="${not empty addressMap[o.addressId]}">
                    <div>
                        <span class="muted">Drop-off:</span>
                        <strong>${addressMap[o.addressId].street}, ${addressMap[o.addressId].city}</strong>
                    </div>
                </c:if>
                <div>
                    <span class="muted">Earns:</span>
                    <strong>₹<fmt:formatNumber value="${o.deliveryFee * 0.75}" minFractionDigits="2" maxFractionDigits="2"/></strong>
                </div>
                <div>
                    <span class="muted">Ordered:</span>
                    <strong>${o.orderDate}</strong>
                </div>
            </div>

            <form method="post" action="${pageContext.request.contextPath}/delivery/orders/${o.id}/accept" style="margin-top:10px">
                <button type="submit" class="btn-primary">Accept delivery</button>
            </form>
        </div>
    </c:forEach>
</div>

<%@ include file="fragments/footer.jspf" %>
