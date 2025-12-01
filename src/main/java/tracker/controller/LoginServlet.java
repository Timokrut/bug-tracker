package main.java.tracker.controller; 

import main.java.tracker.model.*; 
import main.java.tracker.util.DB;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

import java.io.IOException;
import java.sql.*;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    @Override 
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        try (Connection conn = DB.getConnection()) {
            PreparedStatement st = conn.prepareStatement(
                "SELECT id, username, password, role FROM users WHERE username = ?"
            );

            st.setString(1, username);
            ResultSet rs = st.executeQuery();

            if (!rs.next()) {
                resp.getWriter().write("<h1>User not found</h1>");
                return;
            }

            String dbPassword = rs.getString("password");
            if (!dbPassword.equals(password)) {
                resp.getWriter().write("<h1>Wrong password</h1>");
            }

            User u = new User();
            u.id = rs.getInt("id");
            u.username = rs.getString("username");
            u.password = dbPassword;
            u.role = Role.valueOf(rs.getString("role"));

            HttpSession session = req.getSession();
            session.setAttribute("user", u);
            resp.sendRedirect("/tickets");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
