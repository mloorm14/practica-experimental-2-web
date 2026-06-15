<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"  uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Nuevo producto</title>
    <style>
        *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }
        body { font-family: system-ui, sans-serif; background: #fff5e6;
            display: flex; justify-content: center; align-items: flex-start;
            min-height: 100vh; padding: 2rem; }
        .card { background: #fff; border-radius: 10px; padding: 2rem;
            width: 100%; max-width: 500px;
            box-shadow: 0 4px 20px rgba(0,0,0,.1); }
        h1 { color: #c86014; margin-bottom: 1.5rem; }
        label { display: block; font-weight: 600; margin-bottom: .3rem; font-size: .9rem; }
        input, textarea { width: 100%; padding: .6rem .8rem; border: 1px solid #ccc;
            border-radius: 6px; font-size: 1rem; margin-bottom: 1rem; }
        textarea { resize: vertical; min-height: 80px; }
        .row { display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; }
        button { padding: .7rem 1.5rem; background: #c86014; color: white;
            border: none; border-radius: 6px; cursor: pointer; font-size: 1rem; }
        button:hover { background: #a0500f; }
        a { color: #c86014; margin-left: 1rem; }
        .alert-error { background: #fdecea; color: #c0392b; padding: .7rem 1rem;
            border-left: 4px solid #c0392b; border-radius: 4px;
            margin-bottom: 1rem; }
    </style>
</head>
<body>
<div class="card">
    <h1>☕ + Nuevo producto</h1>
    <c:if test="${not empty error}">
        <div class="alert-error"><c:out value="${error}"/></div>
    </c:if>

    <form method="POST" action="${pageContext.request.contextPath}/product/create">
        <input type="hidden" name="csrf_token" value="${fn:escapeXml(sessionScope.csrf_token)}">

        <label>Nombre del producto *</label>
        <input type="text" name="nombre" required maxlength="200">

        <label>Descripción</label>
        <textarea name="descripcion"></textarea>

        <div class="row">
            <div>
                <label>Precio ($) *</label>
                <input type="number" name="precio" required min="0" step="0.01">
            </div>
            <div>
                <label>Stock *</label>
                <input type="number" name="stock" required min="0">
            </div>
        </div>

        <button type="submit">Guardar</button>
        <a href="${pageContext.request.contextPath}/dashboard">← Cancelar</a>
    </form>
</div>
</body>
</html>