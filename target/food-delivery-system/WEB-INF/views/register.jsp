<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Sign up" scope="request"/>
<%@ include file="fragments/header.jspf" %>

<div class="auth-wrap">
    <div class="auth-card">
        <h1>Create your account</h1>
        <p class="muted">Order food, track deliveries, or list your restaurant.</p>

        <form method="post" action="${pageContext.request.contextPath}/register" class="form">
            <label>
                <span>Full name</span>
                <input type="text" name="name" required value="${name}">
            </label>
            <label>
                <span>Email</span>
                <input type="email" name="email" required value="${email}">
            </label>
            <label>
                <span>Phone</span>
                <input type="tel" name="phone" value="${phone}" placeholder="+91 ...">
            </label>
            <label>
                <span>Password</span>
                <input type="password" name="password" required minlength="6">
            </label>
            <label>
                <span>I am a</span>
                <select name="role">
                    <option value="CUSTOMER" ${role == 'RESTAURANT_OWNER' or role == 'DELIVERY_PARTNER' ? '' : 'selected'}>Customer</option>
                    <option value="RESTAURANT_OWNER" ${role == 'RESTAURANT_OWNER' ? 'selected' : ''}>Restaurant owner</option>
                    <option value="DELIVERY_PARTNER" ${role == 'DELIVERY_PARTNER' ? 'selected' : ''}>Delivery partner</option>
                </select>
            </label>
            <button type="submit" class="btn-primary btn-block">Create account</button>
        </form>

        <p class="form-foot">
            Already registered? <a href="${pageContext.request.contextPath}/login">Sign in</a>
        </p>
    </div>
</div>

<%@ include file="fragments/footer.jspf" %>
