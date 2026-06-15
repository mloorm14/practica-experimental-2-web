<?php
declare(strict_types=1);

/**
 * OWASP: Envía cabeceras de seguridad HTTP en cada respuesta.
 * Llama a esta función al inicio de CADA página, ANTES de cualquier output.
 */
function sendSecurityHeaders(): void {
    // Previene que el navegador adivine el tipo MIME (MIME-sniffing)
    header('X-Content-Type-Options: nosniff');

    // Prohíbe que la página se incruste en un <iframe> (anti clickjacking)
    header('X-Frame-Options: DENY');

    // Activa el filtro XSS del navegador (navegadores antiguos)
    header('X-XSS-Protection: 1; mode=block');

    // Content Security Policy: solo recursos del mismo origen
    header(
        "Content-Security-Policy: " .
        "default-src 'self'; " .
        "script-src 'self'; " .
        "style-src 'self' 'unsafe-inline'; " .
        "img-src 'self' data:; " .
        "frame-ancestors 'none';"
    );

    // Limita la información del referenciador en requests externos
    header('Referrer-Policy: strict-origin-when-cross-origin');

    // Elimina la cabecera que revela la versión de PHP
    header_remove('X-Powered-By');

    // HABILITAR en producción con HTTPS:
    // header('Strict-Transport-Security: max-age=31536000; includeSubDomains; preload');
}
