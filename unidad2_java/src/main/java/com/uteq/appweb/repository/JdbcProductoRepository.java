package com.uteq.appweb.repository;

import java.sql.*;
import java.util.*;

/**
 * Implementación JDBC del patrón Repository.
 * Traduce las operaciones de la interfaz a SQL con PreparedStatements parametrizados.
 * Ninguna consulta construye SQL por concatenación de strings (prevención SQLi).
 */
public final class JdbcProductoRepository implements ProductoRepositoryInterface {

    private final Connection conn;

    public JdbcProductoRepository(Connection conn) {
        this.conn = conn;
    }

    @Override
    public List<Map<String, Object>> getAllByUsuarioId(int usuarioId) throws SQLException {
        List<Map<String, Object>> productos = new ArrayList<>();
        PreparedStatement ps = conn.prepareStatement(
            "SELECT id, nombre, descripcion, precio, stock, created_at " +
            "FROM productos WHERE usuario_id = ? ORDER BY id DESC"
        );
        ps.setInt(1, usuarioId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Map<String, Object> p = new LinkedHashMap<>();
            p.put("id",          rs.getInt("id"));
            p.put("nombre",      rs.getString("nombre"));
            p.put("descripcion", rs.getString("descripcion"));
            p.put("precio",      rs.getBigDecimal("precio"));
            p.put("stock",       rs.getInt("stock"));
            Timestamp ts = rs.getTimestamp("created_at");
            p.put("created_at",  ts != null ? ts.toString().substring(0, 10) : "");
            productos.add(p);
        }
        return productos;
    }

    @Override
    public Map<String, Object> getByIdAndUsuarioId(int id, int usuarioId) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
            "SELECT id, nombre, descripcion, precio, stock " +
            "FROM productos WHERE id = ? AND usuario_id = ?"
        );
        ps.setInt(1, id);
        ps.setInt(2, usuarioId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            Map<String, Object> p = new LinkedHashMap<>();
            p.put("id",          rs.getInt("id"));
            p.put("nombre",      rs.getString("nombre"));
            p.put("descripcion", rs.getString("descripcion"));
            p.put("precio",      rs.getBigDecimal("precio"));
            p.put("stock",       rs.getInt("stock"));
            return p;
        }
        return null;
    }

    @Override
    public boolean create(String nombre, String descripcion,
                          double precio, int stock, int usuarioId) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
            "INSERT INTO productos (nombre, descripcion, precio, stock, usuario_id) " +
            "VALUES (?, ?, ?, ?, ?)"
        );
        ps.setString(1, nombre);
        ps.setString(2, descripcion);
        ps.setDouble(3, precio);
        ps.setInt(4, stock);
        ps.setInt(5, usuarioId);
        return ps.executeUpdate() == 1;
    }

    @Override
    public boolean update(int id, String nombre, String descripcion,
                          double precio, int stock, int usuarioId) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
            "UPDATE productos SET nombre = ?, descripcion = ?, precio = ?, stock = ? " +
            "WHERE id = ? AND usuario_id = ?"
        );
        ps.setString(1, nombre);
        ps.setString(2, descripcion);
        ps.setDouble(3, precio);
        ps.setInt(4, stock);
        ps.setInt(5, id);
        ps.setInt(6, usuarioId);
        return ps.executeUpdate() == 1;
    }

    @Override
    public boolean delete(int id, int usuarioId) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
            "DELETE FROM productos WHERE id = ? AND usuario_id = ?"
        );
        ps.setInt(1, id);
        ps.setInt(2, usuarioId);
        return ps.executeUpdate() == 1;
    }
}
