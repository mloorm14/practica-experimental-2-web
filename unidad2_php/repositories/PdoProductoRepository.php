<?php
declare(strict_types=1);

require_once __DIR__ . '/ProductoRepositoryInterface.php';

class PdoProductoRepository implements ProductoRepositoryInterface {
    private PDO $pdo;

    public function __construct(PDO $pdo) {
        $this->pdo = $pdo;
    }

    public function getAllByUsuarioId(int $usuarioId): array {
        $stmt = $this->pdo->prepare(
            'SELECT id, nombre, descripcion, precio, stock, created_at 
             FROM productos WHERE usuario_id = ? ORDER BY id DESC'
        );
        $stmt->execute([$usuarioId]);
        return $stmt->fetchAll(PDO::FETCH_ASSOC) ?: [];
    }

    public function getByIdAndUsuarioId(int $id, int $usuarioId): array|false {
        $stmt = $this->pdo->prepare(
            'SELECT id, nombre, descripcion, precio, stock 
             FROM productos WHERE id = ? AND usuario_id = ?'
        );
        $stmt->execute([$id, $usuarioId]);
        return $stmt->fetch(PDO::FETCH_ASSOC);
    }

    public function create(string $nombre, ?string $descripcion, float $precio, int $stock, int $usuarioId): bool {
        $stmt = $this->pdo->prepare(
            'INSERT INTO productos (nombre, descripcion, precio, stock, usuario_id) 
             VALUES (?, ?, ?, ?, ?)'
        );
        return $stmt->execute([$nombre, $descripcion, $precio, $stock, $usuarioId]);
    }

    public function update(int $id, string $nombre, ?string $descripcion, float $precio, int $stock, int $usuarioId): bool {
        $stmt = $this->pdo->prepare(
            'UPDATE productos 
             SET nombre = ?, descripcion = ?, precio = ?, stock = ? 
             WHERE id = ? AND usuario_id = ?'
        );
        return $stmt->execute([$nombre, $descripcion, $precio, $stock, $id, $usuarioId]);
    }

    public function delete(int $id, int $usuarioId): bool {
        $stmt = $this->pdo->prepare('DELETE FROM productos WHERE id = ? AND usuario_id = ?');
        $stmt->execute([$id, $usuarioId]);
        return $stmt->rowCount() === 1; // true solo si se eliminó exactamente 1 fila
    }
}
