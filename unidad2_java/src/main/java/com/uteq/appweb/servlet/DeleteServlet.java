package com.uteq.appweb.servlet;

import com.uteq.appweb.config.DBConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/product/delete")
public class DeleteServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        // Solo POST (nunca GET para eliminar datos)
        HttpSession session = req.getSession(false);
        String tokenForm = req.getParameter("csrf_token");
        String tokenSes  = (String) session.getAttribute("csrf_token");

        if (tokenSes == null || !tokenSes.equals(tokenForm)) {
            res.sendError(403, "Token CSRF inválido");
            return;
        }

        try {
            int id     = Integer.parseInt(req.getParameter("id"));
            int userId = (Integer) session.getAttribute("user_id");

            try (Connection conn = DBConnection.get()) {
                // AND usuario_id = ? previene IDOR
                PreparedStatement ps = conn.prepareStatement(
                        "DELETE FROM productos WHERE id = ? AND usuario_id = ?"
                );
                ps.setInt(1, id);
                ps.setInt(2, userId);
                ps.executeUpdate();
            }
        } catch (NumberFormatException | SQLException e) {
            // Ignorar silenciosamente y redirigir
        }

        res.sendRedirect(req.getContextPath() + "/dashboard");
    }
}