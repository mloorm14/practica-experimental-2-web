<?php
declare(strict_types=1);
session_start();

require_once 'includes/headers.php';
require_once 'includes/csrf.php';
require_once 'includes/auth.php';
require_once 'repositories/PdoProductoRepository.php';

sendSecurityHeaders();
requireLogin();

$pdo = getConnection();
$repository = new PdoProductoRepository($pdo);

$userId = currentUserId();
$productos = $repository->getAllByUsuarioId($userId);

$csrfToken = generateCsrfToken();
?>

<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Dashboard — Mis Productos</title>
  <style>
    *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }
    body { font-family: system-ui, sans-serif; background: #f0f4f0; padding: 2rem; }
    header { background: #005c2f; color: white; padding: 1rem 2rem;
             border-radius: 8px; display: flex; justify-content: space-between;
             align-items: center; margin-bottom: 1.5rem; }
    header h1 { font-size: 1.4rem; }
    .btn { display: inline-block; padding: .5rem 1.1rem; border-radius: 5px;
           text-decoration: none; font-size: .9rem; cursor: pointer;
           border: none; font-family: inherit; }
    .btn-green  { background: #007a40; color: white; }
    .btn-blue   { background: #2575d0; color: white; }
    .btn-red    { background: #c0392b; color: white; }
    .btn-gray   { background: #6c757d; color: white; }
    table { width: 100%; border-collapse: collapse; background: white;
            border-radius: 8px; overflow: hidden;
            box-shadow: 0 2px 10px rgba(0,0,0,.08); }
    th { background: #005c2f; color: white; padding: .7rem 1rem;
         text-align: left; font-size: .9rem; }
    td { padding: .65rem 1rem; border-bottom: 1px solid #eee; font-size: .9rem; }
    tr:last-child td { border-bottom: none; }
    tr:hover td { background: #f5fbf7; }
    .acciones { display: flex; gap: .4rem; }
    .empty { text-align: center; padding: 2rem; color: #888; }
    .toolbar { display: flex; justify-content: space-between;
               align-items: center; margin-bottom: 1rem; }
  </style>
</head>
<body>
<header>
  <h1>🛒 Mis Productos <small style="font-weight:normal;font-size:.8rem">
      (<?= esc($_SESSION['user_nombre']) ?>)</small>
  </h1>
  <a href="logout.php" class="btn btn-gray">Cerrar sesión</a>
</header>

<div class="toolbar">
  <h2 style="color:#005c2f">Catálogo de productos</h2>
  <a href="create.php" class="btn btn-green">+ Nuevo producto</a>
</div>

<table>
  <thead>
    <tr>
      <th>#</th>
      <th>Nombre</th>
      <th>Descripción</th>
      <th>Precio</th>
      <th>Stock</th>
      <th>Registrado</th>
      <th>Acciones</th>
    </tr>
  </thead>
  <tbody>
    <?php if (empty($productos)): ?>
      <tr><td colspan="7" class="empty">No tienes productos registrados.
        <a href="create.php">Crea el primero</a>.</td></tr>
    <?php else: ?>
      <?php foreach ($productos as $p): ?>
        <tr>
          <td><?= esc($p['id']) ?></td>
          <td><strong><?= esc($p['nombre']) ?></strong></td>
          <td><?= esc($p['descripcion'] ?? '—') ?></td>
          <td>$<?= esc(number_format((float)$p['precio'], 2)) ?></td>
          <td><?= esc($p['stock']) ?></td>
          <td><?= esc(substr($p['created_at'], 0, 10)) ?></td>
          <td class="acciones">
            <a href="edit.php?id=<?= esc($p['id']) ?>" class="btn btn-blue">Editar</a>
            <!-- DELETE: usa form POST + CSRF (nunca GET para destruir datos) -->
            <form method="POST" action="delete.php"
                  onsubmit="return confirm('¿Eliminar este producto?')">
              <input type="hidden" name="csrf_token" value="<?= esc($csrfToken) ?>">
              <input type="hidden" name="id" value="<?= esc($p['id']) ?>">
              <button type="submit" class="btn btn-red">Eliminar</button>
            </form>
          </td>
        </tr>
      <?php endforeach; ?>
    <?php endif; ?>
  </tbody>
</table>
</body>
</html>
