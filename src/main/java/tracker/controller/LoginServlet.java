package tracker.controller; 

import tracker.model.*; 
import tracker.util.DB;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

import java.io.IOException;
import java.sql.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html; charset=UTF-8");

        InputStream is = getServletContext().getResourceAsStream("/login.html");
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
                return;
            }

            User u = new User();
            u.id = rs.getInt("id");
            u.username = rs.getString("username");
            u.password = dbPassword;
            u.role = Role.valueOf(rs.getString("role"));

            HttpSession session = req.getSession();
            session.setAttribute("user", u);
            resp.sendRedirect(req.getContextPath() + "/tickets");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
