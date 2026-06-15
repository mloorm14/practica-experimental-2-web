<?php
declare(strict_types=1);
session_start();

require_once 'includes/headers.php';
require_once 'includes/csrf.php';
require_once 'includes/auth.php';

sendSecurityHeaders();

if (isLoggedIn()) {
    header('Location: dashboard.php');
    exit;
}

$error = '';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $csrfToken = filter_input(INPUT_POST, 'csrf_token', FILTER_DEFAULT) ?? '';
    if (!validateCsrfToken($csrfToken)) {
        $error = 'Token de seguridad inválido.';
    } else {
        $email    = trim(filter_input(INPUT_POST, 'email',    FILTER_DEFAULT) ?? '');
        $password = filter_input(INPUT_POST, 'password',      FILTER_DEFAULT) ?? '';

        $result = loginUser($email, $password);
        if ($result === true) {
            regenerateCsrfToken();
            header('Location: dashboard.php');
            exit;
        } else {
            $error = $result;
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
  <title>Iniciar sesión — AppWeb Unidad II</title>
  <style>
    *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }
    body { font-family: system-ui, sans-serif; background: #f0f4f0;
           display: flex; justify-content: center; align-items: center;
           min-height: 100vh; padding: 1rem; }
    .card { background: #fff; border-radius: 10px; padding: 2rem;
            width: 100%; max-width: 400px;
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
    .alert-error { background: #fdecea; color: #c0392b; padding: .7rem 1rem;
                   border-left: 4px solid #c0392b; border-radius: 4px;
                   margin-bottom: 1rem; font-size: .9rem; }
    p.link { text-align: center; margin-top: 1rem; font-size: .9rem; }
    a { color: #005c2f; }
  </style>
</head>
<body>
<div class="card">
  <h1>Iniciar sesión</h1>

  <?php if ($error): ?><div class="alert-error"><?= esc($error) ?></div><?php endif; ?>

  <form method="POST" action="login.php">
    <input type="hidden" name="csrf_token" value="<?= esc($csrfToken) ?>">

    <label for="email">Correo electrónico</label>
    <input type="email" id="email" name="email" required
           autocomplete="email" placeholder="usuario@dominio.com">

    <label for="password">Contraseña</label>
    <input type="password" id="password" name="password" required
           autocomplete="current-password">

    <button type="submit">Entrar</button>
  </form>
  <p class="link"><a href="register.php">¿No tienes cuenta? Regístrate</a></p>
</div>
</body>
</html>
