<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="${restaurant.name}" scope="request"/>
<%@ include file="fragments/header.jspf" %>

<div class="restaurant-hero">
    <div>
        <h1>${restaurant.name}</h1>
        <p class="muted">${restaurant.description}</p>
        <div class="meta">
            <span class="rating">★ <fmt:formatNumber value="${restaurant.rating}" minFractionDigits="1" maxFractionDigits="1"/></span>
            <span class="dot">•</span>
            <span>${restaurant.cuisineType}</span>
            <c:if test="${not empty restaurant.phone}">
                <span class="dot">•</span>
                <span>${restaurant.phone}</span>
            </c:if>
            <c:if test="${not empty restaurant.opensAt}">
                <span class="dot">•</span>
                <span>${restaurant.opensAt} - ${restaurant.closesAt}</span>
            </c:if>
        </div>
    </div>
</div>

<div class="layout-two-col">
    <div>
        <h2 class="section-title">Menu</h2>

        <div class="chips menu-filters">
            <a class="chip ${empty diet ? 'active' : ''}"
               href="${pageContext.request.contextPath}/restaurants/${restaurant.id}${not empty activeCategory ? '?category=' : ''}${activeCategory}">All</a>
            <a class="chip ${diet == 'veg' ? 'active' : ''}"
               href="${pageContext.request.contextPath}/restaurants/${restaurant.id}?diet=veg${not empty activeCategory ? '&category=' : ''}${activeCategory}">
                <span class="veg-mark veg" style="vertical-align:-1px"></span> Veg
            </a>
            <a class="chip ${diet == 'nonveg' ? 'active' : ''}"
               href="${pageContext.request.contextPath}/restaurants/${restaurant.id}?diet=nonveg${not empty activeCategory ? '&category=' : ''}${activeCategory}">
                <span class="veg-mark nonveg" style="vertical-align:-1px"></span> Non-veg
            </a>
        </div>

        <c:if test="${not empty allCategories}">
            <div class="chips menu-filters">
                <a class="chip ${empty activeCategory ? 'active' : ''}"
                   href="${pageContext.request.contextPath}/restaurants/${restaurant.id}${not empty diet ? '?diet=' : ''}${diet}">All categories</a>
                <c:forEach var="cat" items="${allCategories}">
                    <a class="chip ${activeCategory == cat ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/restaurants/${restaurant.id}?category=${cat}${not empty diet ? '&diet=' : ''}${diet}">${cat}</a>
                </c:forEach>
            </div>
        </c:if>

        <c:if test="${empty itemsByCategory}">
            <div class="empty">No items match those filters.</div>
        </c:if>
        <c:forEach var="entry" items="${itemsByCategory}">
            <h3 class="category-title">${entry.key}</h3>
            <div class="menu-list">
                <c:forEach var="item" items="${entry.value}">
                    <div class="menu-item">
                        <c:if test="${not empty item.imageUrl}">
                            <img class="menu-item-thumb" src="${item.imageUrl}" alt="${item.name}" loading="lazy">
                        </c:if>
                        <div class="menu-item-info">
                            <div class="menu-item-head">
                                <span class="veg-mark ${item.veg ? 'veg' : 'nonveg'}" title="${item.veg ? 'Veg' : 'Non-veg'}"></span>
                                <h4>${item.name}</h4>
                            </div>
                            <p class="muted">${item.description}</p>
                            <div class="price">₹<fmt:formatNumber value="${item.price}" minFractionDigits="2" maxFractionDigits="2"/></div>
                        </div>
                        <form method="post" action="${pageContext.request.contextPath}/cart/add" class="menu-item-action">
                            <input type="hidden" name="foodItemId" value="${item.id}">
                            <input type="hidden" name="restaurantId" value="${restaurant.id}">
                            <input type="hidden" name="quantity" value="1">
                            <button type="submit" class="btn-primary btn-sm">Add +</button>
                        </form>
                    </div>
                </c:forEach>
            </div>
        </c:forEach>
    </div>

    <aside class="side">
        <h2 class="section-title">Reviews</h2>
        <c:if test="${empty reviews}">
            <p class="muted">No reviews yet. Be the first!</p>
        </c:if>
        <c:forEach var="rv" items="${reviews}">
            <div class="review">
                <div class="rating-small">
                    <c:forEach begin="1" end="5" var="i">
                        <span class="${i <= rv.rating ? 'star-on' : 'star-off'}">★</span>
                    </c:forEach>
                </div>
                <p>${rv.comment}</p>
            </div>
        </c:forEach>

        <c:if test="${not empty currentUser and currentUser.role == 'CUSTOMER'}">
            <details class="review-form">
                <summary>Leave a review</summary>
                <form method="post" action="${pageContext.request.contextPath}/reviews/add" class="form">
                    <input type="hidden" name="restaurantId" value="${restaurant.id}">
                    <label>
                        <span>Rating</span>
                        <select name="rating">
                            <option value="5">5 - Excellent</option>
                            <option value="4">4 - Good</option>
                            <option value="3">3 - Average</option>
                            <option value="2">2 - Poor</option>
                            <option value="1">1 - Terrible</option>
                        </select>
                    </label>
                    <label>
                        <span>Comment</span>
                        <textarea name="comment" rows="3" maxlength="500"></textarea>
                    </label>
                    <button type="submit" class="btn-primary">Post review</button>
                </form>
            </details>
        </c:if>
    </aside>
</div>

<%@ include file="fragments/footer.jspf" %>
