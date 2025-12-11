package tracker.controller;

import tracker.model.User;
import tracker.util.DB;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.*;
import java.sql.*;

@WebServlet("/delete-user")
public class DeleteUserServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User current = (User) req.getSession().getAttribute("user");

        if (current == null || !"MANAGER".equals(current.getRole().toString())) {
            resp.sendError(403);
            return;
        }

        int id = Integer.parseInt(req.getParameter("id"));

        try (Connection conn = DB.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM users WHERE id=?");
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        resp.sendRedirect("/bug-tracker/users");
    }
}
