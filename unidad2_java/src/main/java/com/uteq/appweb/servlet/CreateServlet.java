package com.uteq.appweb.servlet;

import com.uteq.appweb.config.DBConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;

@WebServlet("/product/create")
public class CreateServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.getRequestDispatcher("/views/create.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        // Validar CSRF
        HttpSession session = req.getSession(false);
        String tokenForm = req.getParameter("csrf_token");
        String tokenSes  = (String) session.getAttribute("csrf_token");
        if (tokenSes == null || !tokenSes.equals(tokenForm)) {
            res.sendError(403, "Token CSRF inválido");
            return;
        }

        String nombre      = req.getParameter("nombre");
        String descripcion = req.getParameter("descripcion");
        String precioStr   = req.getParameter("precio");
        String stockStr    = req.getParameter("stock");
        int    userId      = (Integer) session.getAttribute("user_id");

        // Validaciones básicas
        if (nombre == null || nombre.isBlank()) {
            req.setAttribute("error", "El nombre es obligatorio.");
            req.getRequestDispatcher("/views/create.jsp").forward(req, res);
            return;
        }

        try {
            BigDecimal precio = new BigDecimal(precioStr.trim());
            int        stock  = Integer.parseInt(stockStr.trim());

            if (precio.compareTo(BigDecimal.ZERO) < 0 || stock < 0) throw new NumberFormatException();

            try (Connection conn = DBConnection.get()) {
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO productos (nombre, descripcion, precio, stock, usuario_id) " +
                                "VALUES (?, ?, ?, ?, ?)"
                );
                ps.setString(1,     nombre.trim());
                ps.setString(2,     descripcion != null ? descripcion.trim() : null);
                ps.setBigDecimal(3, precio);
                ps.setInt(4,        stock);
                ps.setInt(5,        userId);
                ps.executeUpdate();
            }
        } catch (NumberFormatException | SQLException e) {
            req.setAttribute("error", "Datos de precio o stock inválidos.");
            req.getRequestDispatcher("/views/create.jsp").forward(req, res);
            return;
        }

        res.sendRedirect(req.getContextPath() + "/dashboard");
    }
}