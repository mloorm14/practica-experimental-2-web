<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"  uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Iniciar sesión — AppWeb Java</title>
    <style>
        *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }
        body { font-family: system-ui, sans-serif; background: #fff5e6;
            display: flex; justify-content: center; align-items: center;
            min-height: 100vh; padding: 1rem; }
        .card { background: #fff; border-radius: 10px; padding: 2rem;
            width: 100%; max-width: 400px;
            box-shadow: 0 4px 20px rgba(0,0,0,.12); }
        h1 { color: #c86014; margin-bottom: 1.5rem; text-align: center; }
        label { display: block; font-weight: 600; margin-bottom: .3rem; font-size: .9rem; }
        input { width: 100%; padding: .65rem .8rem; border: 1px solid #ccc;
            border-radius: 6px; font-size: 1rem; margin-bottom: 1rem; }
        input:focus { outline: none; border-color: #c86014; }
        button { width: 100%; padding: .75rem; background: #c86014; color: #fff;
            border: none; border-radius: 6px; font-size: 1rem; cursor: pointer; }
        button:hover { background: #a0500f; }
        .alert-error   { background:#fdecea; color:#c0392b; padding:.7rem 1rem;
            border-left:4px solid #c0392b; border-radius:4px; margin-bottom:1rem; }
        .alert-success { background:#eaf5ee; color:#1e6b3c; padding:.7rem 1rem;
            border-left:4px solid #1e6b3c; border-radius:4px; margin-bottom:1rem; }
        p.link { text-align:center; margin-top:1rem; font-size:.9rem; }
        a { color:#c86014; }
    </style>
</head>
<body>
<div class="card">
    <h1>☕ Java/JSP — Login</h1>

    <%-- c:out escapa automáticamente: previene XSS --%>
    <c:if test="${not empty error}">
        <div class="alert-error"><c:out value="${error}"/></div>
    </c:if>
    <c:if test="${not empty success}">
        <div class="alert-success"><c:out value="${success}"/></div>
    </c:if>

    <form method="POST" action="${pageContext.request.contextPath}/login">
        <%-- Token CSRF oculto --%>
        <input type="hidden" name="csrf_token"
               value="${fn:escapeXml(sessionScope.csrf_token)}">

        <label for="email">Correo electrónico</label>
        <input type="email" id="email" name="email" required
               autocomplete="email" placeholder="usuario@dominio.com">

        <label for="password">Contraseña</label>
        <input type="password" id="password" name="password" required
               autocomplete="current-password">

        <button type="submit">Entrar</button>
    </form>
    <p class="link">
        <a href="${pageContext.request.contextPath}/register">¿Sin cuenta? Regístrate</a>
    </p>
</div>
</body>
</html>