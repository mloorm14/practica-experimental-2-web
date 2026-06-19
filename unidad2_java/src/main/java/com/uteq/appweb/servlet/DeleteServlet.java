package com.uteq.appweb.servlet;

import com.uteq.appweb.config.DBConnection;
import com.uteq.appweb.repository.JdbcProductoRepository;
import com.uteq.appweb.repository.ProductoRepositoryInterface;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet("/product/delete")
public class DeleteServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        // Solo POST (nunca GET para eliminar datos)
        HttpSession session = req.getSession(false);
        String tokenForm = req.getParameter("csrf_token");
        String tokenSes  = (session != null)
                ? (String) session.getAttribute("csrf_token")
                : null;

        // Validación CSRF en tiempo constante (previene timing attacks)
        if (tokenSes == null || tokenForm == null ||
                !MessageDigest.isEqual(
                        tokenSes.getBytes(StandardCharsets.UTF_8),
                        tokenForm.getBytes(StandardCharsets.UTF_8))) {
            res.sendError(403, "Token CSRF inválido");
            return;
        }

        try {
            int id     = Integer.parseInt(req.getParameter("id"));
            int userId = (Integer) session.getAttribute("user_id");

            try (Connection conn = DBConnection.get()) {
                // AND usuario_id = ? previene IDOR (Insecure Direct Object Reference)
                ProductoRepositoryInterface repo = new JdbcProductoRepository(conn);
                repo.delete(id, userId);
            }
        } catch (NumberFormatException | SQLException e) {
            // Ignorar silenciosamente y redirigir
        }

        res.sendRedirect(req.getContextPath() + "/dashboard");
    }
}
