package tracker.controller;

import tracker.util.DB;
import tracker.model.Role;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.sql.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html; charset=UTF-8");

        InputStream is = getServletContext().getResourceAsStream("/register.html");
        if (is == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            PrintWriter writer = resp.getWriter()) {
            String line;
            while ((line = reader.readLine()) != null) {
                writer.println(line);
            }
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String role = req.getParameter("role");

        try (Connection conn = DB.getConnection()) {
            PreparedStatement st = conn.prepareStatement(
                "INSERT INTO users (username, password, role) VALUES (?, ?, ?)"
            );

            st.setString(1, username);
            st.setString(2, password);
            st.setString(3, role);

            st.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        resp.sendRedirect(req.getContextPath() + "/login");
    }
}
