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
 
@WebServlet("/create")
public class CreateTicketServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html; charset=UTF-8");

        InputStream is = getServletContext().getResourceAsStream("/create.html");
        if (is == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String html = new String(is.readAllBytes(), StandardCharsets.UTF_8);

        StringBuilder userOptions = new StringBuilder();
        userOptions.append("<option value=\"\">Не назначено</option>");
         try (Connection conn = DB.getConnection()) {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT id, username FROM users");

            while (rs.next()) {
                userOptions.append("<option value=\"")
                        .append(rs.getInt("id"))
                        .append("\">")
                        .append(rs.getString("username"))
                        .append("</option>");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        html = html.replace("{{userOptions}}", userOptions.toString());

        resp.getWriter().write(html);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = (User) req.getSession().getAttribute("user");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String title = req.getParameter("title");
        String description = req.getParameter("description");
        String type = req.getParameter("type");
        String priority = req.getParameter("priority");
        String assigneeId = req.getParameter("assigneeId");
        

        try (Connection conn = DB.getConnection()) {
            PreparedStatement st = conn.prepareStatement(
                "INSERT INTO tickets (title, description, type, priority, status, assignee_id, created_at, updated_at) VALUES (?, ?, ?, ?, 'OPEN', ?, NOW(), NOW())"
            );

            st.setString(1, title);
            st.setString(2, description);
            st.setString(3, type);
            st.setString(4, priority);
            
            if (assigneeId == null || assigneeId.isEmpty()) {
                st.setNull(5, java.sql.Types.INTEGER);
            } else {
                st.setInt(5, Integer.parseInt(assigneeId));
            }
            
            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        resp.sendRedirect(req.getContextPath() + "/tickets");
    }
}
