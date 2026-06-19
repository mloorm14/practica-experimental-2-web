package com.uteq.appweb.repository;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Contrato abstracto del patrón Repository para la entidad Producto.
 * La lógica de negocio depende únicamente de esta interfaz (principio DIP de SOLID).
 */
public interface ProductoRepositoryInterface {

    /** Retorna todos los productos del usuario, ordenados por ID descendente. */
    List<Map<String, Object>> getAllByUsuarioId(int usuarioId) throws SQLException;

    /** Retorna un producto por su ID y usuario propietario, o null si no existe. */
    Map<String, Object> getByIdAndUsuarioId(int id, int usuarioId) throws SQLException;

    /** Inserta un producto nuevo. Retorna true si se insertó exactamente 1 fila. */
    boolean create(String nombre, String descripcion,
                   double precio, int stock, int usuarioId) throws SQLException;

    /** Actualiza un producto. Retorna true si se actualizó exactamente 1 fila. */
    boolean update(int id, String nombre, String descripcion,
                   double precio, int stock, int usuarioId) throws SQLException;

    /** Elimina un producto. Retorna true si se eliminó exactamente 1 fila. */
    boolean delete(int id, int usuarioId) throws SQLException;
}
