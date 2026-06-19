<?php
declare(strict_types=1);
session_set_cookie_params([
    'lifetime' => 0,          // Expira al cerrar el navegador
    'path'     => '/',
    'domain'   => '',
    'secure'   => false,      // Cambiar a true en producción con HTTPS
    'httponly' => true,       // Inaccesible desde JavaScript (anti XSS)
    'samesite' => 'Strict',   // Bloquea envío cross-site (anti CSRF)
]);
session_start();

require_once 'includes/auth.php';

if (isLoggedIn()) {
    header('Location: dashboard.php');
} else {
    header('Location: login.php');
}
exit;
