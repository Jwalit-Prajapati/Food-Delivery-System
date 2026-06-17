<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="My profile" scope="request"/>
<%@ include file="fragments/header.jspf" %>

<h1>My profile</h1>

<div class="layout-two-col">
    <div>
        <div class="card pad">
            <h3>Account</h3>
            <div class="kv"><span class="muted">Name</span><span>${user.name}</span></div>
            <div class="kv"><span class="muted">Email</span><span>${user.email}</span></div>
            <div class="kv"><span class="muted">Phone</span><span>${not empty user.phone ? user.phone : '—'}</span></div>
            <div class="kv"><span class="muted">Role</span><span>${user.role}</span></div>
        </div>

        <div class="card pad">
            <h3>Saved addresses</h3>
            <c:if test="${empty addresses}">
                <p class="muted">No saved addresses yet.</p>
            </c:if>
            <c:forEach var="a" items="${addresses}">
                <div class="address-row">
                    <div>
                        <p>
                            ${a.street}, ${a.city}, ${a.state} - ${a.zipCode}
                            <c:if test="${a['default']}"><span class="badge-default">Default</span></c:if>
                        </p>
                        <c:if test="${not empty a.landmark}"><p class="muted">Near ${a.landmark}</p></c:if>
                    </div>
                    <form method="post" action="${pageContext.request.contextPath}/profile/addresses/delete" onsubmit="return confirm('Remove this address?')">
                        <input type="hidden" name="addressId" value="${a.id}">
                        <button type="submit" class="link-btn">Remove</button>
                    </form>
                </div>
            </c:forEach>
        </div>
    </div>

    <aside class="side">
        <div class="card pad">
            <h3>Add a new address</h3>
            <form method="post" action="${pageContext.request.contextPath}/profile/addresses/add" class="form">
                <label><span>Street</span><input type="text" name="street" required></label>
                <label><span>City</span><input type="text" name="city" required></label>
                <label><span>State</span><input type="text" name="state" required></label>
                <label><span>Zip / PIN</span><input type="text" name="zipCode" required></label>
                <label><span>Landmark (optional)</span><input type="text" name="landmark"></label>
                <label class="check"><input type="checkbox" name="isDefault" value="true"> Make this my default</label>
                <button type="submit" class="btn-primary btn-block">Save address</button>
            </form>
        </div>
    </aside>
</div>

<%@ include file="fragments/footer.jspf" %>
