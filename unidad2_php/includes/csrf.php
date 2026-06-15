<?php
declare(strict_types=1);

/**
 * OWASP: Genera un token CSRF único, aleatorio y vinculado a la sesión.
 * 64 caracteres hexadecimales = 256 bits de entropía.
 */
function generateCsrfToken(): string {
    if (empty($_SESSION['csrf_token'])) {
        $_SESSION['csrf_token'] = bin2hex(random_bytes(32));
    }
    return $_SESSION['csrf_token'];
}

/**
 * Valida el token enviado en el formulario contra el almacenado en sesión.
 * hash_equals() previene ataques de timing (comparación en tiempo constante).
 */
function validateCsrfToken(string $token): bool {
    if (empty($_SESSION['csrf_token'])) {
        return false;
    }
    return hash_equals($_SESSION['csrf_token'], $token);
}

/**
 * Regenera el token después de cada acción sensible (login exitoso, etc.).
 */
function regenerateCsrfToken(): void {
    $_SESSION['csrf_token'] = bin2hex(random_bytes(32));
}
