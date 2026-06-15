<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"  uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <title>Dashboard — Java/JSP</title>
  <style>
    *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }
    body { font-family: system-ui, sans-serif; background: #fff5e6; padding: 2rem; }
    header { background: #c86014; color: white; padding: 1rem 2rem;
      border-radius: 8px; display: flex; justify-content: space-between;
      align-items: center; margin-bottom: 1.5rem; }
    .btn { display:inline-block; padding:.5rem 1.1rem; border-radius:5px;
      text-decoration:none; font-size:.9rem; border:none; cursor:pointer;
      font-family:inherit; }
    .btn-orange { background:#c86014; color:white; }
    .btn-blue   { background:#2575d0; color:white; }
    .btn-red    { background:#c0392b; color:white; }
    .btn-gray   { background:#6c757d; color:white; }
    table { width:100%; border-collapse:collapse; background:white;
      border-radius:8px; overflow:hidden;
      box-shadow:0 2px 10px rgba(0,0,0,.08); }
    th { background:#c86014; color:white; padding:.7rem 1rem; text-align:left; }
    td { padding:.65rem 1rem; border-bottom:1px solid #eee; font-size:.9rem; }
    .toolbar { display:flex; justify-content:space-between; align-items:center;
      margin-bottom:1rem; }
    .acciones { display:flex; gap:.4rem; }
  </style>
</head>
<body>
<header>
  <h1>☕ Dashboard Java/JSP &mdash;
    <small><c:out value="${sessionScope.user_nombre}"/></small>
  </h1>
  <a href="${pageContext.request.contextPath}/logout" class="btn btn-gray">
    Cerrar sesión
  </a>
</header>

<div class="toolbar">
  <h2 style="color:#c86014">Mis Productos</h2>
  <a href="${pageContext.request.contextPath}/product/create" class="btn btn-orange">
    + Nuevo
  </a>
</div>

<table>
  <thead>
  <tr>
    <th>#</th><th>Nombre</th><th>Descripción</th>
    <th>Precio</th><th>Stock</th><th>Fecha</th><th>Acciones</th>
  </tr>
  </thead>
  <tbody>
  <c:choose>
    <c:when test="${empty productos}">
      <tr>
        <td colspan="7" style="text-align:center;padding:2rem;color:#888">
          Sin productos. <a href="${pageContext.request.contextPath}/product/create">Crea uno</a>.
        </td>
      </tr>
    </c:when>
    <c:otherwise>
      <c:forEach var="p" items="${productos}">
        <tr>
          <td><c:out value="${p.id}"/></td>
          <td><strong><c:out value="${p.nombre}"/></strong></td>
          <td><c:out value="${p.descripcion != null ? p.descripcion : '—'}"/></td>
          <td>$<c:out value="${p.precio}"/></td>
          <td><c:out value="${p.stock}"/></td>
          <td><c:out value="${p.created_at}"/></td>
          <td class="acciones">
            <a href="${pageContext.request.contextPath}/product/edit?id=${p.id}"
               class="btn btn-blue">Editar</a>
            <form method="POST"
                  action="${pageContext.request.contextPath}/product/delete"
                  onsubmit="return confirm('¿Eliminar?')">
              <input type="hidden" name="csrf_token"
                     value="${fn:escapeXml(sessionScope.csrf_token)}">
              <input type="hidden" name="id" value="${p.id}">
              <button type="submit" class="btn btn-red">Eliminar</button>
            </form>
          </td>
        </tr>
      </c:forEach>
    </c:otherwise>
  </c:choose>
  </tbody>
</table>
</body>
</html>