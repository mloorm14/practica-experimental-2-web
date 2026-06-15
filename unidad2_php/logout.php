<?php
declare(strict_types=1);
session_start();

require_once 'includes/headers.php';
sendSecurityHeaders();

// Destruir sesión completamente
$_SESSION = [];
if (ini_get('session.use_cookies')) {
    $params = session_get_cookie_params();
    setcookie(
        session_name(), '', time() - 42000,
        $params['path'], $params['domain'],
        $params['secure'], $params['httponly']
    );
}
session_destroy();

header('Location: login.php');
exit;
