package tracker.controller;

import tracker.model.*;
import tracker.util.DB;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/edit")
public class EditTicketServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = (User) req.getSession().getAttribute("user");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        
        resp.setContentType("text/html; charset=UTF-8");
        PrintWriter w = resp.getWriter();

        int id = Integer.parseInt(req.getParameter("id"));

        String title = "";
        String description = "";
        TicketType type = null;
        TicketStatus status = null;
        Integer assigneeId = null;
        TicketPriority priority = null;

        List<User> users = new ArrayList<>();

        try (Connection conn = DB.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM tickets WHERE id=?");
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                title = rs.getString("title");
                description = rs.getString("description");
                type = TicketType.valueOf(rs.getString("type"));
                status = TicketStatus.valueOf(rs.getString("status"));
                priority = TicketPriority.valueOf(rs.getString("priority"));

                int a = rs.getInt("assignee_id");
                assigneeId = rs.wasNull() ? null : a;
            }

            Statement st = conn.createStatement();
            rs = st.executeQuery("SELECT id, username FROM users");

            while (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setUsername(rs.getString("username"));
                users.add(u);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        InputStream in = getServletContext().getResourceAsStream("/edit.html");
        String html = new String(in.readAllBytes(), "UTF-8");

        html = html.replace("{{id}}", String.valueOf(id));
        html = html.replace("{{title}}", title);
        html = html.replace("{{description}}", description);

        StringBuilder typeOps = new StringBuilder();
        for (TicketType t : TicketType.values()) {
            typeOps.append("<option value='").append(t).append("'")
                .append(t == type ? " selected" : "")
                .append(">").append(t).append("</option>");
        }
        html = html.replace("{{typeOptions}}", typeOps.toString());

        StringBuilder priorityOps = new StringBuilder();
        for (TicketPriority t : TicketPriority.values()) {
            priorityOps.append("<option value='").append(t).append("'")
                .append(t == priority ? " selected" : "")
                .append(">").append(t).append("</option>");
        }
        html = html.replace("{{priorityOptions}}", priorityOps.toString());

        StringBuilder statusOps = new StringBuilder();
        for (TicketStatus s : TicketStatus.values()) {
            statusOps.append("<option value='").append(s).append("'")
                    .append(s == status ? " selected" : "")
                    .append(">").append(s).append("</option>");
        }
        html = html.replace("{{statusOptions}}", statusOps.toString());

        StringBuilder userOps = new StringBuilder();
        
        for (User u : users) {
            userOps.append("<option value='").append(u.getId()).append("'");

            if (assigneeId != null && assigneeId == u.getId()) {
                userOps.append(" selected");
            }

            userOps.append(">").append(u.getUsername()).append("</option>");
        }

        html = html.replace("{{userOptions}}", userOps.toString());

        w.write(html);
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");

        int id = Integer.parseInt(req.getParameter("id"));
        String title = req.getParameter("title");
        String description = req.getParameter("description");
        String type = req.getParameter("type");
        String status = req.getParameter("status");
        String priority = req.getParameter("priority");
        String assigneeId = req.getParameter("assigneeId");

        try (Connection conn = DB.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "UPDATE tickets SET title=?, description=?, type=?, status=?, priority=?, assignee_id=?, updated_at=NOW() WHERE id=?");

            ps.setString(1, title);
            ps.setString(2, description);
            ps.setString(3, type);
            ps.setString(4, status);
            ps.setString(5, priority);

            if (assigneeId == null || assigneeId.isEmpty()) {
                ps.setNull(6, Types.INTEGER);
            } else {
                ps.setInt(6, Integer.parseInt(assigneeId));
            }

            ps.setInt(7, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        resp.sendRedirect("/bug-tracker/tickets");
    }
}
