<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Your cart" scope="request"/>
<%@ include file="fragments/header.jspf" %>

<h1>Your cart</h1>

<c:if test="${empty cartItems}">
    <div class="empty">
        <p>Your cart is empty.</p>
        <a class="btn-primary" href="${pageContext.request.contextPath}/home">Browse restaurants</a>
    </div>
</c:if>

<c:if test="${not empty cartItems}">
    <div class="layout-two-col">
        <div>
            <c:if test="${not empty restaurant}">
                <div class="card pad">
                    <strong>Ordering from</strong>
                    <a href="${pageContext.request.contextPath}/restaurants/${restaurant.id}">${restaurant.name}</a>
                </div>
            </c:if>

            <div class="cart-list">
                <c:forEach var="ci" items="${cartItems}">
                    <div class="cart-item">
                        <div>
                            <h4>${ci.foodItemName}</h4>
                            <div class="muted">₹<fmt:formatNumber value="${ci.price}" minFractionDigits="2" maxFractionDigits="2"/> each</div>
                        </div>
                        <form method="post" action="${pageContext.request.contextPath}/cart/update" class="qty-form">
                            <input type="hidden" name="itemId" value="${ci.id}">
                            <button type="submit" name="quantity" value="${ci.quantity - 1}" class="qty-btn">−</button>
                            <span class="qty-num">${ci.quantity}</span>
                            <button type="submit" name="quantity" value="${ci.quantity + 1}" class="qty-btn">+</button>
                        </form>
                        <div class="cart-item-total">
                            ₹<fmt:formatNumber value="${ci.lineTotal}" minFractionDigits="2" maxFractionDigits="2"/>
                        </div>
                        <form method="post" action="${pageContext.request.contextPath}/cart/remove">
                            <input type="hidden" name="itemId" value="${ci.id}">
                            <button type="submit" class="link-btn" title="Remove">✕</button>
                        </form>
                    </div>
                </c:forEach>
            </div>

            <form method="post" action="${pageContext.request.contextPath}/cart/clear" class="cart-actions">
                <button type="submit" class="btn-link">Clear cart</button>
            </form>
        </div>

        <aside class="side">
            <div class="card pad">
                <h3>Order summary</h3>
                <div class="row"><span>Subtotal</span><span>₹<fmt:formatNumber value="${subtotal}" minFractionDigits="2" maxFractionDigits="2"/></span></div>
                <div class="row"><span>Tax (5%)</span><span>₹<fmt:formatNumber value="${tax}" minFractionDigits="2" maxFractionDigits="2"/></span></div>
                <div class="row"><span>Delivery</span><span>₹<fmt:formatNumber value="${deliveryFee}" minFractionDigits="2" maxFractionDigits="2"/></span></div>
                <hr>
                <div class="row total"><strong>Total</strong><strong>₹<fmt:formatNumber value="${total}" minFractionDigits="2" maxFractionDigits="2"/></strong></div>
            </div>

            <div class="card pad">
                <h3>Delivery & payment</h3>
                <c:choose>
                    <c:when test="${empty addresses}">
                        <p class="muted">You don't have any saved addresses.</p>
                        <a class="btn-primary btn-block" href="${pageContext.request.contextPath}/profile">Add an address</a>
                    </c:when>
                    <c:otherwise>
                        <form method="post" action="${pageContext.request.contextPath}/orders/place" class="form">
                            <label>
                                <span>Deliver to</span>
                                <select name="addressId" required>
                                    <c:forEach var="a" items="${addresses}">
                                        <option value="${a.id}" ${a['default'] ? 'selected' : ''}>
                                            ${a.street}, ${a.city} - ${a.zipCode}${a['default'] ? ' (Default)' : ''}
                                        </option>
                                    </c:forEach>
                                </select>
                            </label>
                            <label>
                                <span>Payment</span>
                                <select name="paymentMethod">
                                    <option value="COD">Cash on delivery</option>
                                    <option value="CARD">Card</option>
                                    <option value="UPI">UPI</option>
                                </select>
                            </label>
                            <button type="submit" class="btn-primary btn-block">Place order</button>
                        </form>
                    </c:otherwise>
                </c:choose>
            </div>
        </aside>
    </div>
</c:if>

<%@ include file="fragments/footer.jspf" %>
