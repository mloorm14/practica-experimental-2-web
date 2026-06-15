<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <title>Error 404 — No Encontrado</title>
  <style>
    body { font-family: system-ui, sans-serif; background: #fff5e6; text-align: center; padding: 5rem; }
    h1 { color: #c86014; font-size: 3rem; margin-bottom: 1rem; }
    a { color: #c86014; text-decoration: none; font-weight: bold; }
  </style>
</head>
<body>
<h1>404</h1>
<h2>Página no encontrada</h2>
<p>El recurso que buscas no existe en este servidor.</p>
<br>
<a href="${pageContext.request.contextPath}/login">← Volver al inicio</a>
</body>
</html>