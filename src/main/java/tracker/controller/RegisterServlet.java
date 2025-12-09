package tracker.controller;

import tracker.util.DB;
import tracker.model.Role;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
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

        resp.sendRedirect(req.getContextPath() + "/login.html");
    }
}
