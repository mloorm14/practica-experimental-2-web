package com.uteq.appweb.servlet;

import com.uteq.appweb.config.DBConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.*;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        int userId = (Integer) session.getAttribute("user_id");

        // READ: obtener productos del usuario autenticado
        List<Map<String, Object>> productos = new ArrayList<>();

        try (Connection conn = DBConnection.get()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT id, nombre, descripcion, precio, stock, created_at " +
                            "FROM productos WHERE usuario_id = ? ORDER BY id DESC"
            );
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> p = new LinkedHashMap<>();
                p.put("id",          rs.getInt("id"));
                p.put("nombre",      rs.getString("nombre"));
                p.put("descripcion", rs.getString("descripcion"));
                p.put("precio",      rs.getBigDecimal("precio"));
                p.put("stock",       rs.getInt("stock"));
                p.put("created_at",  rs.getTimestamp("created_at").toString().substring(0,10));
                productos.add(p);
            }
        } catch (SQLException e) {
            req.setAttribute("error", "Error al cargar productos.");
        }

        // Generar/mantener CSRF token
        if (session.getAttribute("csrf_token") == null) {
            byte[] bytes = new byte[32];
            new java.security.SecureRandom().nextBytes(bytes);
            session.setAttribute("csrf_token",
                    java.util.Base64.getEncoder().encodeToString(bytes));
        }

        req.setAttribute("productos", productos);
        req.getRequestDispatcher("/views/dashboard.jsp").forward(req, res);
    }
}