<?php
declare(strict_types=1);

interface ProductoRepositoryInterface {
    public function getAllByUsuarioId(int $usuarioId): array;
    public function getByIdAndUsuarioId(int $id, int $usuarioId): array|false;
    public function create(string $nombre, ?string $descripcion, float $precio, int $stock, int $usuarioId): bool;
    public function update(int $id, string $nombre, ?string $descripcion, float $precio, int $stock, int $usuarioId): bool;
    public function delete(int $id, int $usuarioId): bool;
}
