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
import java.util.Map;

@WebServlet("/product/edit")
public class EditServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        int userId = (Integer) session.getAttribute("user_id");

        int id;
        try {
            id = Integer.parseInt(req.getParameter("id"));
        } catch (NumberFormatException e) {
            res.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }

        Map<String, Object> producto = null;
        try (Connection conn = DBConnection.get()) {
            ProductoRepositoryInterface repo = new JdbcProductoRepository(conn);
            producto = repo.getByIdAndUsuarioId(id, userId);
        } catch (SQLException e) {
            req.setAttribute("error", "Error al cargar el producto.");
        }

        if (producto == null) {
            res.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }

        generarCsrfToken(req);
        req.setAttribute("producto", producto);
        req.getRequestDispatcher("/views/edit.jsp").forward(req, res);
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

        int userId = (Integer) session.getAttribute("user_id");

        int id;
        try {
            id = Integer.parseInt(req.getParameter("id"));
        } catch (NumberFormatException e) {
            res.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }

        String nombre      = trim(req.getParameter("nombre"));
        String descripcion = trim(req.getParameter("descripcion"));
        String precioStr   = trim(req.getParameter("precio"));
        String stockStr    = trim(req.getParameter("stock"));

        if (nombre.isEmpty() || precioStr.isEmpty() || stockStr.isEmpty()) {
            req.setAttribute("error", "Nombre, precio y stock son obligatorios.");
            req.setAttribute("producto", Map.of(
                "id", id, "nombre", nombre,
                "descripcion", descripcion,
                "precio", precioStr, "stock", stockStr
            ));
            generarCsrfToken(req);
            req.getRequestDispatcher("/views/edit.jsp").forward(req, res);
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
            req.setAttribute("producto", Map.of(
                "id", id, "nombre", nombre,
                "descripcion", descripcion,
                "precio", precioStr, "stock", stockStr
            ));
            generarCsrfToken(req);
            req.getRequestDispatcher("/views/edit.jsp").forward(req, res);
            return;
        }

        try (Connection conn = DBConnection.get()) {
            ProductoRepositoryInterface repo = new JdbcProductoRepository(conn);
            repo.update(id, nombre, descripcion.isEmpty() ? null : descripcion,
                        precio, stock, userId);
        } catch (SQLException e) {
            req.setAttribute("error", "Error al actualizar el producto.");
            req.getRequestDispatcher("/views/edit.jsp").forward(req, res);
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
