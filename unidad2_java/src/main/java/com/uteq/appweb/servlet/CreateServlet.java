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
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Base64;

@WebServlet("/product/create")
public class CreateServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        generarCsrfToken(req);
        req.getRequestDispatcher("/views/create.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        String tokenForm = req.getParameter("csrf_token");
        String tokenSes  = (session != null)
                ? (String) session.getAttribute("csrf_token")
                : null;

        // Validación CSRF en tiempo constante
        if (tokenSes == null || tokenForm == null ||
                !MessageDigest.isEqual(
                        tokenSes.getBytes(StandardCharsets.UTF_8),
                        tokenForm.getBytes(StandardCharsets.UTF_8))) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "Token CSRF inválido");
            return;
        }

        String nombre      = trim(req.getParameter("nombre"));
        String descripcion = trim(req.getParameter("descripcion"));
        String precioStr   = trim(req.getParameter("precio"));
        String stockStr    = trim(req.getParameter("stock"));

        if (nombre.isEmpty() || precioStr.isEmpty() || stockStr.isEmpty()) {
            req.setAttribute("error", "Nombre, precio y stock son obligatorios.");
            generarCsrfToken(req);
            req.getRequestDispatcher("/views/create.jsp").forward(req, res);
            return;
        }

        double precio;
        int    stock;
        try {
            precio = Double.parseDouble(precioStr);
            stock  = Integer.parseInt(stockStr);
            if (precio < 0 || stock < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            req.setAttribute("error", "Precio y stock deben ser números positivos.");
            generarCsrfToken(req);
            req.getRequestDispatcher("/views/create.jsp").forward(req, res);
            return;
        }

        int userId = (Integer) session.getAttribute("user_id");

        try (Connection conn = DBConnection.get()) {
            ProductoRepositoryInterface repo = new JdbcProductoRepository(conn);
            repo.create(nombre, descripcion.isEmpty() ? null : descripcion,
                        precio, stock, userId);
        } catch (SQLException e) {
            req.setAttribute("error", "Error al guardar el producto.");
            generarCsrfToken(req);
            req.getRequestDispatcher("/views/create.jsp").forward(req, res);
            return;
        }

        res.sendRedirect(req.getContextPath() + "/dashboard");
    }

    private void generarCsrfToken(HttpServletRequest req) {
        HttpSession session = req.getSession(true);
        if (session.getAttribute("csrf_token") == null) {
            byte[] bytes = new byte[32];
            new SecureRandom().nextBytes(bytes);
            session.setAttribute("csrf_token",
                    Base64.getEncoder().encodeToString(bytes));
        }
    }

    private String trim(String val) {
        return val == null ? "" : val.trim();
    }
}
