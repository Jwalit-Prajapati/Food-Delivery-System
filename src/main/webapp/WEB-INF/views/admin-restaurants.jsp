<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Restaurants" scope="request"/>
<%@ include file="fragments/header.jspf" %>

<h1>Restaurant management</h1>
<p class="muted">Approve new partners. Suspend a listing to hide it from customers without deleting it.</p>

<div class="chips">
    <a class="chip ${filter == 'all' ? 'active' : ''}"     href="${pageContext.request.contextPath}/admin/restaurants?filter=all">All</a>
    <a class="chip ${filter == 'pending' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/restaurants?filter=pending">Pending approval</a>
    <a class="chip ${filter == 'active' ? 'active' : ''}"  href="${pageContext.request.contextPath}/admin/restaurants?filter=active">Live</a>
</div>

<c:if test="${empty restaurants}">
    <div class="empty">No restaurants matching that filter.</div>
</c:if>

<div class="grid">
    <c:forEach var="r" items="${restaurants}">
        <div class="card pad">
            <div class="order-top">
                <div>
                    <h3>${r.name}</h3>
                    <p class="muted">${r.cuisineType} &middot; Owner #${r.ownerId}</p>
                </div>
                <c:choose>
                    <c:when test="${not r.verified}">
                        <span class="order-status status-placed">Pending</span>
                    </c:when>
                    <c:when test="${r.active}">
                        <span class="order-status status-delivered">Live</span>
                    </c:when>
                    <c:otherwise>
                        <span class="order-status status-cancelled">Suspended</span>
                    </c:otherwise>
                </c:choose>
            </div>
            <p>${r.description}</p>
            <div class="meta">
                <span class="rating">★ <fmt:formatNumber value="${r.rating}" minFractionDigits="1" maxFractionDigits="1"/></span>
                <c:if test="${not empty r.phone}">
                    <span class="dot">•</span>
                    <span>${r.phone}</span>
                </c:if>
                <c:if test="${not empty r.opensAt}">
                    <span class="dot">•</span>
                    <span>${r.opensAt} - ${r.closesAt}</span>
                </c:if>
            </div>

            <div class="admin-actions">
                <c:choose>
                    <c:when test="${not r.verified}">
                        <form method="post" action="${pageContext.request.contextPath}/admin/restaurants/${r.id}/verify" style="display:inline">
                            <button type="submit" class="btn-primary btn-sm">Approve & publish</button>
                        </form>
                    </c:when>
                    <c:otherwise>
                        <form method="post" action="${pageContext.request.contextPath}/admin/restaurants/${r.id}/unverify" style="display:inline"
                              onsubmit="return confirm('Un-verify ${r.name}? It will hide from customers.')">
                            <button type="submit" class="link-btn">Un-verify</button>
                        </form>
                    </c:otherwise>
                </c:choose>

                <c:if test="${r.verified}">
                    <c:choose>
                        <c:when test="${r.active}">
                            <form method="post" action="${pageContext.request.contextPath}/admin/restaurants/${r.id}/toggle" style="display:inline"
                                  onsubmit="return confirm('Suspend ${r.name}?')">
                                <input type="hidden" name="active" value="false">
                                <button type="submit" class="link-btn">Suspend</button>
                            </form>
                        </c:when>
                        <c:otherwise>
                            <form method="post" action="${pageContext.request.contextPath}/admin/restaurants/${r.id}/toggle" style="display:inline">
                                <input type="hidden" name="active" value="true">
                                <button type="submit" class="btn-link btn-sm">Reactivate</button>
                            </form>
                        </c:otherwise>
                    </c:choose>
                </c:if>
            </div>
        </div>
    </c:forEach>
</div>

<%@ include file="fragments/footer.jspf" %>
