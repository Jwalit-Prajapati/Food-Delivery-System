<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Menu - ${restaurant.name}" scope="request"/>
<%@ include file="fragments/header.jspf" %>

<a class="back-link" href="${pageContext.request.contextPath}/owner/dashboard">&larr; Dashboard</a>
<h1>${restaurant.name} — Menu</h1>

<div class="layout-two-col">
    <div>
        <c:if test="${empty items}">
            <div class="empty">No menu items yet. Add your first item →</div>
        </c:if>
        <div class="menu-list">
            <c:forEach var="item" items="${items}">
                <div class="menu-item">
                    <div class="menu-item-info">
                        <div class="menu-item-head">
                            <span class="veg-mark ${item.veg ? 'veg' : 'nonveg'}"></span>
                            <h4>${item.name}</h4>
                            <c:if test="${not item.available}"><span class="badge muted-badge">Unavailable</span></c:if>
                        </div>
                        <p class="muted">${item.description}</p>
                        <div class="price">₹<fmt:formatNumber value="${item.price}" minFractionDigits="2" maxFractionDigits="2"/> &middot; <span class="muted">${item.category}</span></div>
                    </div>
                    <div class="menu-item-action">
                        <a class="btn-link btn-sm" href="${pageContext.request.contextPath}/owner/menu/${item.id}/edit">Edit</a>
                        <form method="post" action="${pageContext.request.contextPath}/owner/menu/${item.id}/toggle">
                            <input type="hidden" name="restaurantId" value="${restaurant.id}">
                            <input type="hidden" name="available" value="${!item.available}">
                            <button type="submit" class="btn-link btn-sm">${item.available ? 'Disable' : 'Enable'}</button>
                        </form>
                        <form method="post" action="${pageContext.request.contextPath}/owner/menu/${item.id}/delete" onsubmit="return confirm('Delete this item?')">
                            <input type="hidden" name="restaurantId" value="${restaurant.id}">
                            <button type="submit" class="link-btn">Delete</button>
                        </form>
                    </div>
                </div>
            </c:forEach>
        </div>
    </div>

    <aside class="side">
        <div class="card pad">
            <h3>Add menu item</h3>
            <form method="post" action="${pageContext.request.contextPath}/owner/restaurants/${restaurant.id}/menu/add" class="form">
                <label><span>Name</span><input type="text" name="name" required></label>
                <label><span>Description</span><textarea name="description" rows="2"></textarea></label>
                <label><span>Price (₹)</span><input type="number" step="0.01" name="price" required min="0"></label>
                <label><span>Category</span><input type="text" name="category" placeholder="e.g. Mains, Starters"></label>
                <label><span>Image URL (optional)</span><input type="url" name="imageUrl" placeholder="https://..."></label>
                <label class="check"><input type="checkbox" name="veg" value="true" checked> Vegetarian</label>
                <button type="submit" class="btn-primary btn-block">Add item</button>
            </form>
        </div>
    </aside>
</div>

<%@ include file="fragments/footer.jspf" %>
