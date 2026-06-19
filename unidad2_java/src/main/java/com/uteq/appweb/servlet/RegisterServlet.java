package com.uteq.appweb.servlet;

import com.uteq.appweb.config.DBConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.mindrot.jbcrypt.BCrypt;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.*;
import java.util.Base64;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        // Si ya está logueado, redirigir al dashboard
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("user_id") != null) {
            res.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }
        generarCsrfToken(req);
        req.getRequestDispatcher("/views/register.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        // 1. Validar CSRF token
        HttpSession session = req.getSession(false);
        String tokenForm = req.getParameter("csrf_token");
        String tokenSes  = (session != null)
                ? (String) session.getAttribute("csrf_token")
                : null;

        if (tokenSes == null || tokenForm == null ||
                !MessageDigest.isEqual(
                        tokenSes.getBytes(StandardCharsets.UTF_8),
                        tokenForm.getBytes(StandardCharsets.UTF_8))) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "Token CSRF inválido");
            return;
        }

        // 2. Leer y validar campos
        String nombre   = sanitize(req.getParameter("nombre"));
        String email    = sanitize(req.getParameter("email"));
        String password = req.getParameter("password");
        String confirm  = req.getParameter("confirm");

        String error = validarRegistro(nombre, email, password, confirm);
        if (error != null) {
            req.setAttribute("error", error);
            generarCsrfToken(req);
            req.getRequestDispatcher("/views/register.jsp").forward(req, res);
            return;
        }

        // 3. Hashear con BCrypt (cost=12)
        String hash = BCrypt.hashpw(password, BCrypt.gensalt(12));

        // 4. Insertar con PreparedStatement (anti SQL Injection)
        try (Connection conn = DBConnection.get()) {
            // Verificar si el email ya existe
            PreparedStatement check = conn.prepareStatement(
                    "SELECT id FROM usuarios WHERE email = ?"
            );
            check.setString(1, email);
            if (check.executeQuery().next()) {
                req.setAttribute("error", "El correo electrónico ya está registrado.");
                generarCsrfToken(req);
                req.getRequestDispatcher("/views/register.jsp").forward(req, res);
                return;
            }

            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO usuarios (nombre, email, password_hash) VALUES (?, ?, ?)"
            );
            ps.setString(1, nombre);
            ps.setString(2, email);
            ps.setString(3, hash);
            ps.executeUpdate();

        } catch (SQLException e) {
            req.setAttribute("error", "Error al registrar. Inténtalo de nuevo.");
            req.getRequestDispatcher("/views/register.jsp").forward(req, res);
            return;
        }

        // 5. Redirigir al login (Post-Redirect-Get)
        res.sendRedirect(req.getContextPath() + "/login?registered=1");
    }

    private String validarRegistro(String nombre, String email,
                                   String password, String confirm) {
        if (nombre.length() < 2 || nombre.length() > 100)
            return "El nombre debe tener entre 2 y 100 caracteres.";
        if (!email.matches("^[\\w.%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$"))
            return "El formato del correo no es válido.";
        if (password == null || password.length() < 8)
            return "La contraseña debe tener al menos 8 caracteres.";
        if (!password.equals(confirm))
            return "Las contraseñas no coinciden.";
        return null; // sin errores
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

    /** Elimina espacios y limita longitud para sanear entradas básicas. */
    private String sanitize(String val) {
        if (val == null) return "";
        return val.trim().substring(0, Math.min(val.trim().length(), 500));
    }
}