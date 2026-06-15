<?php
declare(strict_types=1);

require_once __DIR__ . '/../config/db.php';

/**
 * OWASP A07: Registra un usuario nuevo con bcrypt (cost=12).
 * Retorna true en éxito, o string con mensaje de error.
 */
function registerUser(string $nombre, string $email, string $password): bool|string {
    // Validaciones básicas
    if (strlen($nombre) < 2 || strlen($nombre) > 100) {
        return 'El nombre debe tener entre 2 y 100 caracteres.';
    }
    if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
        return 'El formato del correo electrónico no es válido.';
    }
    if (strlen($password) < 8) {
        return 'La contraseña debe tener al menos 8 caracteres.';
    }

    $pdo = getConnection();

    // Verificar si el email ya existe (prepared statement)
    $stmt = $pdo->prepare('SELECT id FROM usuarios WHERE email = ?');
    $stmt->execute([$email]);
    if ($stmt->fetch()) {
        return 'El correo electrónico ya está registrado.';
    }

    // OWASP: Hashear con bcrypt, cost=12 (balance seguridad/velocidad)
    $hash = password_hash($password, PASSWORD_BCRYPT, ['cost' => 12]);

    // Insertar con prepared statement (anti SQL Injection)
    $stmt = $pdo->prepare(
        'INSERT INTO usuarios (nombre, email, password_hash) VALUES (?, ?, ?)'
    );
    $stmt->execute([$nombre, $email, $hash]);

    return true;
}

/**
 * OWASP A07: Autentica un usuario.
 * El mensaje de error es genérico (no revela si el email existe o no).
 */
function loginUser(string $email, string $password): bool|string {
    if (empty($email) || empty($password)) {
        return 'Todos los campos son obligatorios.';
    }

    $pdo = getConnection();

    $stmt = $pdo->prepare(
        'SELECT id, nombre, email, password_hash FROM usuarios WHERE email = ?'
    );
    $stmt->execute([$email]);
    $user = $stmt->fetch();

    // password_verify() es seguro contra timing attacks
    if (!$user || !password_verify($password, $user['password_hash'])) {
        return 'Correo electrónico o contraseña incorrectos.'; // mensaje genérico
    }

    // Regenerar ID de sesión ANTES de guardar datos (previene session fixation)
    session_regenerate_id(true);

    $_SESSION['user_id']     = $user['id'];
    $_SESSION['user_nombre'] = $user['nombre'];
    $_SESSION['user_email']  = $user['email'];

    return true;
}

/** Verifica si hay una sesión activa. */
function isLoggedIn(): bool {
    return !empty($_SESSION['user_id']);
}

/** Redirige al login si no hay sesión. Llamar al inicio de páginas protegidas. */
function requireLogin(): void {
    if (!isLoggedIn()) {
        header('Location: /unidad2_php/login.php');
        exit;
    }
}

/** Retorna el ID del usuario en sesión como entero. */
function currentUserId(): int {
    return (int)($_SESSION['user_id'] ?? 0);
}

/**
 * OWASP: Escapa un valor para salida HTML segura (previene XSS).
 * Equivalente a CGI::escapeHTML() en PERL y fn:escapeXml() en JSTL.
 */
function esc(mixed $val): string {
    return htmlspecialchars((string)$val, ENT_QUOTES | ENT_SUBSTITUTE, 'UTF-8');
}
