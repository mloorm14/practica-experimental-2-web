package com.uteq.appweb.servlet;

import com.uteq.appweb.config.DBConnection;
import com.uteq.appweb.repository.JdbcProductoRepository;
import com.uteq.appweb.repository.ProductoRepositoryInterface;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        int userId = (Integer) session.getAttribute("user_id");

        List<Map<String, Object>> productos = null;

        try (Connection conn = DBConnection.get()) {
            ProductoRepositoryInterface repo = new JdbcProductoRepository(conn);
            productos = repo.getAllByUsuarioId(userId);
        } catch (SQLException e) {
            req.setAttribute("error", "Error al cargar productos.");
        }

        // Generar/mantener CSRF token en sesión
        if (session.getAttribute("csrf_token") == null) {
            byte[] bytes = new byte[32];
            new SecureRandom().nextBytes(bytes);
            session.setAttribute("csrf_token",
                    Base64.getEncoder().encodeToString(bytes));
        }

        req.setAttribute("productos", productos);
        req.getRequestDispatcher("/views/dashboard.jsp").forward(req, res);
    }
}
