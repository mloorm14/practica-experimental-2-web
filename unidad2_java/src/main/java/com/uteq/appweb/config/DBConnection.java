package com.uteq.appweb.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Proporciona conexiones JDBC a MySQL.
 * En producción reemplazar con un pool (HikariCP / DBCP2).
 */
public class DBConnection {

    private static final String URL =
            "jdbc:mysql://127.0.0.1:3306/appweb_unidad2" +
                    "?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8";
    private static final String USER = "root";
    private static final String PASS = "";   // XAMPP: vacío por defecto

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver MySQL no encontrado", e);
        }
    }

    /** Retorna una conexión nueva. Cerrar con try-with-resources. */
    public static Connection get() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    // Constructor privado: clase utilitaria, no instanciar
    private DBConnection() {}
}