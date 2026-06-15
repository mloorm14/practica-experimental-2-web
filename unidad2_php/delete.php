<?php
declare(strict_types=1);
session_start();

require_once 'includes/headers.php';
require_once 'includes/csrf.php';
require_once 'includes/auth.php';
require_once 'repositories/PdoProductoRepository.php';

sendSecurityHeaders();
requireLogin();

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    header('Location: dashboard.php');
    exit;
}

$csrfToken = filter_input(INPUT_POST, 'csrf_token', FILTER_DEFAULT) ?? '';
if (!validateCsrfToken($csrfToken)) {
    http_response_code(403);
    die('Token CSRF inválido.');
}

$id = filter_input(INPUT_POST, 'id', FILTER_VALIDATE_INT);
if (!$id) {
    header('Location: dashboard.php');
    exit;
}

$pdo = getConnection();
$repository = new PdoProductoRepository($pdo);
$repository->delete($id, currentUserId());

header('Location: dashboard.php');
exit;