<?php
declare(strict_types=1);

// Credenciales de XAMPP por defecto
define('DB_HOST',    '127.0.0.1');
define('DB_PORT',    '3306');
define('DB_NAME',    'appweb_unidad2');
define('DB_USER',    'root');
define('DB_PASS',    '');          // XAMPP: contraseña vacía por defecto
define('DB_CHARSET', 'utf8mb4');

/**
 * Retorna la conexión PDO singleton.
 * PDO::ATTR_EMULATE_PREPARES = false → sentencias preparadas REALES (anti SQLi).
 */
function getConnection(): PDO {
    static $pdo = null;
    if ($pdo !== null) {
        return $pdo;
    }

    $dsn = sprintf(
        'mysql:host=%s;port=%s;dbname=%s;charset=%s',
        DB_HOST, DB_PORT, DB_NAME, DB_CHARSET
    );

    $options = [
        PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION,
        PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
        PDO::ATTR_EMULATE_PREPARES   => false,
    ];

    try {
        $pdo = new PDO($dsn, DB_USER, DB_PASS, $options);
    } catch (PDOException $e) {
        error_log('DB Error: ' . $e->getMessage());
        http_response_code(500);
        die('Error de conexión a la base de datos.');
    }

    return $pdo;
}
