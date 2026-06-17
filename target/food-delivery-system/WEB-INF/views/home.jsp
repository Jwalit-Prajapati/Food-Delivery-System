<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Restaurants" scope="request"/>
<%@ include file="fragments/header.jspf" %>

<section class="hero">
    <h1>What are you craving today?</h1>
    <form method="get" action="${pageContext.request.contextPath}/home" class="search-bar">
        <input type="search" name="search" placeholder="Search restaurants..." value="${search}">
        <button type="submit" class="btn-primary">Search</button>
    </form>
    <div class="chips">
        <a class="chip ${empty cuisine and empty search ? 'active' : ''}" href="${pageContext.request.contextPath}/home">All</a>
        <c:forEach var="c" items="${['Indian','Italian','Chinese','Mexican','American','Thai']}">
            <a class="chip ${cuisine == c ? 'active' : ''}" href="${pageContext.request.contextPath}/home?cuisine=${c}">${c}</a>
        </c:forEach>
    </div>
</section>

<section>
    <h2 class="section-title">
        <c:choose>
            <c:when test="${not empty search}">Results for "${search}"</c:when>
            <c:when test="${not empty cuisine}">${cuisine} cuisine</c:when>
            <c:otherwise>Popular near you</c:otherwise>
        </c:choose>
    </h2>

    <c:if test="${empty restaurants}">
        <div class="empty">No restaurants found. Try a different search.</div>
    </c:if>

    <div class="grid">
        <c:forEach var="r" items="${restaurants}">
            <a class="card restaurant-card" href="${pageContext.request.contextPath}/restaurants/${r.id}">
                <div class="card-cover" style="background: linear-gradient(135deg, #fde68a, #fb923c);">
                    <span class="cuisine-tag">${r.cuisineType}</span>
                </div>
                <div class="card-body">
                    <h3>${r.name}</h3>
                    <p class="muted">${r.description}</p>
                    <div class="meta">
                        <span class="rating">★ <fmt:formatNumber value="${r.rating}" minFractionDigits="1" maxFractionDigits="1"/></span>
                        <span class="dot">•</span>
                        <c:choose>
                            <c:when test="${not empty r.opensAt and not empty r.closesAt}">
                                <span>${r.opensAt} - ${r.closesAt}</span>
                            </c:when>
                            <c:otherwise><span>Open now</span></c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </a>
        </c:forEach>
    </div>
</section>

<%@ include file="fragments/footer.jspf" %>
