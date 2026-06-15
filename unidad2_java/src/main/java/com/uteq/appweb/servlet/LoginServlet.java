package com.uteq.appweb.servlet;

import com.uteq.appweb.config.DBConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.mindrot.jbcrypt.BCrypt;
import java.io.IOException;
import java.security.SecureRandom;
import java.sql.*;
import java.util.Base64;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("user_id") != null) {
            res.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }
        generarCsrfToken(req);
        if ("1".equals(req.getParameter("registered"))) {
            req.setAttribute("success", "Registro exitoso. Ahora inicia sesión.");
        }
        req.getRequestDispatcher("/views/login.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        // 1. Validar CSRF
        HttpSession session = req.getSession(false);
        String tokenForm = req.getParameter("csrf_token");
        String tokenSes  = (session != null)
                ? (String) session.getAttribute("csrf_token")
                : null;
        if (tokenSes == null || !tokenSes.equals(tokenForm)) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "Token CSRF inválido");
            return;
        }

        String email    = req.getParameter("email");
        String password = req.getParameter("password");

        if (email == null || email.isBlank() ||
                password == null || password.isBlank()) {
            req.setAttribute("error", "Todos los campos son obligatorios.");
            generarCsrfToken(req);
            req.getRequestDispatcher("/views/login.jsp").forward(req, res);
            return;
        }

        // 2. Buscar usuario con PreparedStatement
        try (Connection conn = DBConnection.get()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT id, nombre, email, password_hash " +
                            "FROM usuarios WHERE email = ?"
            );
            ps.setString(1, email.trim());
            ResultSet rs = ps.executeQuery();

            // Mensaje genérico: no revelar si el email existe o no
            if (!rs.next() || !BCrypt.checkpw(password, rs.getString("password_hash"))) {
                req.setAttribute("error",
                        "Correo electrónico o contraseña incorrectos.");
                generarCsrfToken(req);
                req.getRequestDispatcher("/views/login.jsp").forward(req, res);
                return;
            }

            // 3. Crear sesión con nuevo token (previene session fixation)
            session = req.getSession(false);
            if (session != null) session.invalidate();
            session = req.getSession(true);
            session.setAttribute("user_id",     rs.getInt("id"));
            session.setAttribute("user_nombre", rs.getString("nombre"));
            session.setAttribute("user_email",  rs.getString("email"));
            session.setMaxInactiveInterval(1800); // 30 minutos

            // Regenerar token CSRF en nueva sesión
            generarCsrfToken(req);

        } catch (SQLException e) {
            req.setAttribute("error", "Error del servidor. Inténtalo de nuevo.");
            req.getRequestDispatcher("/views/login.jsp").forward(req, res);
            return;
        }

        res.sendRedirect(req.getContextPath() + "/dashboard");
    }

    private void generarCsrfToken(HttpServletRequest req) {
        HttpSession session = req.getSession(true);
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        session.setAttribute("csrf_token",
                Base64.getEncoder().encodeToString(bytes));
    }
}