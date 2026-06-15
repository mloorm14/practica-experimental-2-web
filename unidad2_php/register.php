<?php
declare(strict_types=1);
session_start();

require_once 'includes/headers.php';
require_once 'includes/csrf.php';
require_once 'includes/auth.php';

sendSecurityHeaders();

// Si ya está logueado, redirigir
if (isLoggedIn()) {
    header('Location: dashboard.php');
    exit;
}

$error   = '';
$success = '';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    // 1. Validar token CSRF
    $csrfToken = filter_input(INPUT_POST, 'csrf_token', FILTER_DEFAULT) ?? '';
    if (!validateCsrfToken($csrfToken)) {
        $error = 'Token de seguridad inválido. Recarga la página.';
    } else {
        // 2. Sanear entradas (filter_input + trim)
        $nombre   = trim(filter_input(INPUT_POST, 'nombre',   FILTER_DEFAULT) ?? '');
        $email    = trim(filter_input(INPUT_POST, 'email',    FILTER_DEFAULT) ?? '');
        $password = filter_input(INPUT_POST, 'password',      FILTER_DEFAULT) ?? '';
        $confirm  = filter_input(INPUT_POST, 'confirm',       FILTER_DEFAULT) ?? '';

        if ($password !== $confirm) {
            $error = 'Las contraseñas no coinciden.';
        } else {
            // 3. Registrar usuario
            $result = registerUser($nombre, $email, $password);
            if ($result === true) {
                $success = '¡Registro exitoso! Ahora puedes iniciar sesión.';
                regenerateCsrfToken();
            } else {
                $error = $result;
            }
        }
    }
}

$csrfToken = generateCsrfToken();
?>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Registro — AppWeb Unidad II</title>
  <style>
    *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }
    body { font-family: system-ui, sans-serif; background: #f0f4f0;
           display: flex; justify-content: center; align-items: center;
           min-height: 100vh; padding: 1rem; }
    .card { background: #fff; border-radius: 10px; padding: 2rem;
            width: 100%; max-width: 420px;
            box-shadow: 0 4px 20px rgba(0,0,0,.12); }
    h1 { color: #005c2f; margin-bottom: 1.5rem; font-size: 1.5rem; text-align: center; }
    label { display: block; font-weight: 600; margin-bottom: .3rem;
            font-size: .9rem; color: #333; }
    input { width: 100%; padding: .65rem .8rem; border: 1px solid #ccc;
            border-radius: 6px; font-size: 1rem; margin-bottom: 1rem;
            transition: border-color .2s; }
    input:focus { outline: none; border-color: #005c2f; }
    button { width: 100%; padding: .75rem; background: #005c2f; color: #fff;
             border: none; border-radius: 6px; font-size: 1rem; cursor: pointer; }
    button:hover { background: #007a40; }
    .alert-error   { background: #fdecea; color: #c0392b; padding: .7rem 1rem;
                     border-left: 4px solid #c0392b; border-radius: 4px;
                     margin-bottom: 1rem; font-size: .9rem; }
    .alert-success { background: #eaf5ee; color: #1e6b3c; padding: .7rem 1rem;
                     border-left: 4px solid #1e6b3c; border-radius: 4px;
                     margin-bottom: 1rem; font-size: .9rem; }
    p.link { text-align: center; margin-top: 1rem; font-size: .9rem; }
    a { color: #005c2f; }
  </style>
</head>
<body>
<div class="card">
  <h1>Crear cuenta</h1>

  <?php if ($error):   ?><div class="alert-error"><?= esc($error) ?></div><?php endif; ?>
  <?php if ($success): ?><div class="alert-success"><?= esc($success) ?></div><?php endif; ?>

  <form method="POST" action="register.php">
    <!-- Token CSRF oculto -->
    <input type="hidden" name="csrf_token" value="<?= esc($csrfToken) ?>">

    <label for="nombre">Nombre completo *</label>
    <input type="text" id="nombre" name="nombre" required
           minlength="2" maxlength="100" autocomplete="name"
           placeholder="Ej: María García">

    <label for="email">Correo electrónico *</label>
    <input type="email" id="email" name="email" required
           maxlength="150" autocomplete="email"
           placeholder="usuario@dominio.com">

    <label for="password">Contraseña * (mínimo 8 caracteres)</label>
    <input type="password" id="password" name="password" required
           minlength="8" autocomplete="new-password">

    <label for="confirm">Confirmar contraseña *</label>
    <input type="password" id="confirm" name="confirm" required
           minlength="8" autocomplete="new-password">

    <button type="submit">Registrarse</button>
  </form>
  <p class="link"><a href="login.php">¿Ya tienes cuenta? Inicia sesión</a></p>
</div>
</body>
</html>
