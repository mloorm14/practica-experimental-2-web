<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <title>Error 403 — Acceso Denegado</title>
  <style>
    body { font-family: system-ui, sans-serif; background: #fff5e6; text-align: center; padding: 5rem; }
    h1 { color: #c0392b; font-size: 3rem; margin-bottom: 1rem; }
    a { color: #c86014; text-decoration: none; font-weight: bold; }
  </style>
</head>
<body>
<h1>403</h1>
<h2>Acceso Denegado / Token CSRF Inválido</h2>
<p>No tienes permiso para realizar esta acción o tu sesión ha expirado.</p>
<br>
<a href="${pageContext.request.contextPath}/login">← Volver al inicio</a>
</body>
</html>