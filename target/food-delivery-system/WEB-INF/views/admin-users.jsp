<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="Users" scope="request"/>
<%@ include file="fragments/header.jspf" %>

<h1>User management</h1>
<p class="muted">Suspend or reactivate any user account.</p>

<div class="chips">
    <a class="chip ${empty filterRole ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/users">All</a>
    <a class="chip ${filterRole == 'CUSTOMER' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/users?role=CUSTOMER">Customers</a>
    <a class="chip ${filterRole == 'RESTAURANT_OWNER' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/users?role=RESTAURANT_OWNER">Restaurant owners</a>
    <a class="chip ${filterRole == 'DELIVERY_PARTNER' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/users?role=DELIVERY_PARTNER">Drivers</a>
    <a class="chip ${filterRole == 'ADMIN' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/users?role=ADMIN">Admins</a>
</div>

<c:if test="${empty users}">
    <div class="empty">No users matching that filter.</div>
</c:if>

<c:if test="${not empty users}">
    <div class="trip-table-wrap">
        <table class="items-table">
            <thead>
            <tr><th>Name</th><th>Email</th><th>Phone</th><th>Role</th><th>Status</th><th style="text-align:right">Actions</th></tr>
            </thead>
            <tbody>
            <c:forEach var="u" items="${users}">
                <tr>
                    <td><strong>${u.name}</strong></td>
                    <td>${u.email}</td>
                    <td>${not empty u.phone ? u.phone : '—'}</td>
                    <td><span class="badge muted-badge">${fn:replace(u.role, '_', ' ')}</span></td>
                    <td>
                        <c:choose>
                            <c:when test="${u.active}"><span class="order-status status-delivered">Active</span></c:when>
                            <c:otherwise><span class="order-status status-cancelled">Suspended</span></c:otherwise>
                        </c:choose>
                    </td>
                    <td style="text-align:right">
                        <c:choose>
                            <c:when test="${currentUser.id == u.id}">
                                <span class="muted">— you —</span>
                            </c:when>
                            <c:when test="${u.active}">
                                <form method="post" action="${pageContext.request.contextPath}/admin/users/${u.id}/toggle" style="display:inline"
                                      onsubmit="return confirm('Suspend ${u.name}?')">
                                    <input type="hidden" name="active" value="false">
                                    <button type="submit" class="link-btn">Suspend</button>
                                </form>
                            </c:when>
                            <c:otherwise>
                                <form method="post" action="${pageContext.request.contextPath}/admin/users/${u.id}/toggle" style="display:inline">
                                    <input type="hidden" name="active" value="true">
                                    <button type="submit" class="btn-link btn-sm">Reactivate</button>
                                </form>
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</c:if>

<%@ include file="fragments/footer.jspf" %>
