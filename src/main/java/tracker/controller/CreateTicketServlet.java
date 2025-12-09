package tracker.controller;

import tracker.model.*;
import tracker.util.DB;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

import java.io.IOException;
import java.sql.*;

@WebServlet("/create")
public class CreateTicketServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.sendRedirect(req.getContextPath() + "/create.html");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        User user = (User) req.getSession().getAttribute("user");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login.html");
            return;
        }

        String title = req.getParameter("title");
        String description = req.getParameter("description");
        String type = req.getParameter("type");
        String priority = req.getParameter("priority");

        try (Connection conn = DB.getConnection()) {
            PreparedStatement st = conn.prepareStatement(
                "INSERT INTO tickets (title, description, type, priority, status, assignee_id, created_at, updated_at) VALUES (?, ?, ?, ?, 'OPEN', ?, NOW(), NOW())"
            );

            st.setString(1, title);
            st.setString(2, description);
            st.setString(3, type);
            st.setString(4, priority);
            st.setInt(5, user.id);

            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        resp.sendRedirect(req.getContextPath() + "/tickets");
    }
}
