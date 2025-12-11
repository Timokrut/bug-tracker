package tracker.controller;

import tracker.model.User;
import tracker.util.DB;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.*;
import java.sql.*;
import java.nio.charset.StandardCharsets;

@WebServlet("/edit-user")
public class EditUserServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User current = (User) req.getSession().getAttribute("user");
        if (current == null || !"MANAGER".equals(current.getRole().toString())) {
            resp.sendError(403);
            return;
        }

        int id = Integer.parseInt(req.getParameter("id"));
        String username = "";
        String role = "";

        try (Connection conn = DB.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT username, role FROM users WHERE id=?");
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                username = rs.getString("username");
                role = rs.getString("role");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        InputStream in = getServletContext().getResourceAsStream("/edituser.html");
        String html = new String(in.readAllBytes(), StandardCharsets.UTF_8);

        html = html.replace("{{id}}", String.valueOf(id));
        html = html.replace("{{username}}", username);

        html = html.replace("{{r_user}}", role.equals("USER") ? "selected" : "");
        html = html.replace("{{r_manager}}", role.equals("MANAGER") ? "selected" : "");

        resp.getWriter().write(html);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");

        int id = Integer.parseInt(req.getParameter("id"));
        String username = req.getParameter("username");
        String role = req.getParameter("role");
        String password = req.getParameter("password");

        try (Connection conn = DB.getConnection()) {
            if (password != null && !password.isBlank()) {
                PreparedStatement ps = conn.prepareStatement(
                    "UPDATE users SET username=?, password=?, role=? WHERE id=?");
                ps.setString(1, username);
                ps.setString(2, password);
                ps.setString(3, role);
                ps.setInt(4, id);
                ps.executeUpdate();
            } else {
                PreparedStatement ps = conn.prepareStatement(
                    "UPDATE users SET username=?, role=? WHERE id=?");
                ps.setString(1, username);
                ps.setString(2, role);
                ps.setInt(3, id);
                ps.executeUpdate();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        resp.sendRedirect("/bug-tracker/users");
    }
}
