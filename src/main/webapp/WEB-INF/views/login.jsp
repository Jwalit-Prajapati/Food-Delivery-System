<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Login" scope="request"/>
<%@ include file="fragments/header.jspf" %>

<div class="auth-wrap">
    <div class="auth-card">
        <h1>Welcome back</h1>
        <p class="muted">Sign in to order from your favourite restaurants.</p>

        <form method="post" action="${pageContext.request.contextPath}/login" class="form">
            <label>
                <span>Email</span>
                <input type="email" name="email" required value="${email}" autofocus>
            </label>
            <label>
                <span>Password</span>
                <input type="password" name="password" required>
            </label>
            <button type="submit" class="btn-primary btn-block">Sign in</button>
        </form>

        <p class="form-foot">
            New here? <a href="${pageContext.request.contextPath}/register">Create an account</a>
        </p>

        <div class="hint">
            <strong>Demo accounts (seeded)</strong>
            <span>Customer: <code>john@example.com</code> / <code>password</code></span>
            <span>Owner: <code>raj@restaurant.com</code> / <code>password</code></span>
        </div>
    </div>
</div>

<%@ include file="fragments/footer.jspf" %>
