-- Ejecutar en phpMyAdmin o en terminal MySQL:
-- mysql -u root -p < database/schema.sql

CREATE DATABASE IF NOT EXISTS appweb_unidad2
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE appweb_unidad2;

-- Tabla de usuarios (autenticación)
CREATE TABLE IF NOT EXISTS usuarios (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    nombre        VARCHAR(100)  NOT NULL,
    email         VARCHAR(150)  NOT NULL UNIQUE,
    password_hash VARCHAR(255)  NOT NULL,
    created_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tabla de productos (módulo CRUD)
CREATE TABLE IF NOT EXISTS productos (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(200)   NOT NULL,
    descripcion TEXT,
    precio      DECIMAL(10,2)  NOT NULL DEFAULT 0.00,
    stock       INT            NOT NULL DEFAULT 0,
    usuario_id  INT            NOT NULL,
    created_at  TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP
                               ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_producto_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX IF NOT EXISTS idx_productos_usuario ON productos(usuario_id);
