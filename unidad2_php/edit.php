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
$error  = '';

$id = filter_input(INPUT_GET, 'id', FILTER_VALIDATE_INT);
if (!$id) {
    header('Location: dashboard.php');
    exit;
}

$producto = $repository->getByIdAndUsuarioId($id, $userId);
if (!$producto) {
    header('Location: dashboard.php');
    exit;
}

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $csrfToken = filter_input(INPUT_POST, 'csrf_token', FILTER_DEFAULT) ?? '';
    if (!validateCsrfToken($csrfToken)) {
        $error = 'Token CSRF inválido.';
    } else {
        $nombre      = trim(filter_input(INPUT_POST, 'nombre', FILTER_DEFAULT) ?? '');
        $descripcion = trim(filter_input(INPUT_POST, 'descripcion', FILTER_DEFAULT) ?? '');
        $precio      = filter_input(INPUT_POST, 'precio', FILTER_VALIDATE_FLOAT);
        $stock       = filter_input(INPUT_POST, 'stock', FILTER_VALIDATE_INT);

        if (empty($nombre)) { $error = 'El nombre es obligatorio.'; }
        elseif ($precio === false || $precio < 0) { $error = 'Precio inválido.'; }
        elseif ($stock === false || $stock < 0)   { $error = 'Stock inválido.'; }
        else {
            $descripcionVal = $descripcion !== '' ? $descripcion : null;
            $repository->update($id, $nombre, $descripcionVal, (float)$precio, (int)$stock, $userId);
            
            header('Location: dashboard.php');
            exit;
        }
    }
}

$csrfToken = generateCsrfToken();
?>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <title>Editar producto</title>
  <style>
    *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }
    body { font-family: system-ui, sans-serif; background: #f0f4f0;
           display: flex; justify-content: center; align-items: flex-start;
           min-height: 100vh; padding: 2rem; }
    .card { background: #fff; border-radius: 10px; padding: 2rem;
            width: 100%; max-width: 500px;
            box-shadow: 0 4px 20px rgba(0,0,0,.1); }
    h1 { color: #005c2f; margin-bottom: 1.5rem; }
    label { display: block; font-weight: 600; margin-bottom: .3rem; font-size: .9rem; }
    input, textarea { width: 100%; padding: .6rem .8rem; border: 1px solid #ccc;
                      border-radius: 6px; font-size: 1rem; margin-bottom: 1rem; }
    textarea { resize: vertical; min-height: 80px; }
    .row { display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; }
    button { padding: .7rem 1.5rem; background: #2575d0; color: white;
             border: none; border-radius: 6px; cursor: pointer; font-size: 1rem; }
    button:hover { background: #1a5db0; }
    a { color: #005c2f; margin-left: 1rem; }
    .alert-error { background: #fdecea; color: #c0392b; padding: .7rem 1rem;
                   border-left: 4px solid #c0392b; border-radius: 4px;
                   margin-bottom: 1rem; }
  </style>
</head>
<body>
<div class="card">
  <h1>✏️ Editar producto #<?= esc($producto['id']) ?></h1>
  <?php if ($error): ?><div class="alert-error"><?= esc($error) ?></div><?php endif; ?>

  <form method="POST" action="edit.php?id=<?= esc($id) ?>">
    <input type="hidden" name="csrf_token" value="<?= esc($csrfToken) ?>">

    <label>Nombre *</label>
    <input type="text" name="nombre" required maxlength="200"
           value="<?= esc($producto['nombre']) ?>">

    <label>Descripción</label>
    <textarea name="descripcion"><?= esc($producto['descripcion'] ?? '') ?></textarea>

    <div class="row">
      <div>
        <label>Precio ($) *</label>
        <input type="number" name="precio" required min="0" step="0.01"
               value="<?= esc($producto['precio']) ?>">
      </div>
      <div>
        <label>Stock *</label>
        <input type="number" name="stock" required min="0"
               value="<?= esc($producto['stock']) ?>">
      </div>
    </div>

    <button type="submit">Actualizar</button>
    <a href="dashboard.php">← Cancelar</a>
  </form>
</div>
</body>
</html>
