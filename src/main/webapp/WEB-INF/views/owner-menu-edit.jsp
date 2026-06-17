<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Edit menu item" scope="request"/>
<%@ include file="fragments/header.jspf" %>

<a class="back-link" href="${pageContext.request.contextPath}/owner/restaurants/${restaurant.id}/menu">&larr; Back to menu</a>
<h1>Edit menu item</h1>
<p class="muted">${restaurant.name}</p>

<div class="layout-two-col">
    <div>
        <div class="card pad">
            <form method="post" action="${pageContext.request.contextPath}/owner/menu/${item.id}/edit" class="form">
                <label>
                    <span>Name</span>
                    <input type="text" name="name" required value="${item.name}">
                </label>
                <label>
                    <span>Description</span>
                    <textarea name="description" rows="3">${item.description}</textarea>
                </label>
                <div class="form-row">
                    <label>
                        <span>Price (₹)</span>
                        <input type="number" step="0.01" name="price" required min="0"
                               value="<fmt:formatNumber value="${item.price}" minFractionDigits="2" maxFractionDigits="2"/>">
                    </label>
                    <label>
                        <span>Category</span>
                        <input type="text" name="category" value="${item.category}">
                    </label>
                </div>
                <label>
                    <span>Image URL</span>
                    <input type="url" name="imageUrl" value="${item.imageUrl}" placeholder="https://...">
                </label>
                <label class="check">
                    <input type="checkbox" name="veg" value="true" ${item.veg ? 'checked' : ''}>
                    Vegetarian
                </label>

                <div class="form-buttons">
                    <button type="submit" class="btn-primary">Save changes</button>
                    <a class="btn-link" href="${pageContext.request.contextPath}/owner/restaurants/${restaurant.id}/menu">Cancel</a>
                </div>
            </form>
        </div>
    </div>

    <aside class="side">
        <div class="card pad">
            <h3>Preview</h3>
            <c:if test="${not empty item.imageUrl}">
                <img src="${item.imageUrl}" alt="${item.name}" style="width:100%; border-radius:8px; margin-bottom:12px;">
            </c:if>
            <h4>${item.name}</h4>
            <p class="muted">${item.description}</p>
            <p class="price">₹<fmt:formatNumber value="${item.price}" minFractionDigits="2" maxFractionDigits="2"/></p>
            <p class="muted">
                <span class="veg-mark ${item.veg ? 'veg' : 'nonveg'}" style="vertical-align:-1px"></span>
                ${item.veg ? 'Vegetarian' : 'Non-vegetarian'}
                <c:if test="${not empty item.category}"> &middot; ${item.category}</c:if>
            </p>
        </div>
    </aside>
</div>

<%@ include file="fragments/footer.jspf" %>
