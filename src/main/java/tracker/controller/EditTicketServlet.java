package tracker.controller;

import tracker.model.*;
import tracker.util.DB;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;

@WebServlet("/edit")
public class EditTicketServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = (User) req.getSession().getAttribute("user");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login.html");
            return;
        }

        int id = Integer.parseInt(req.getParameter("id"));

        String title = "";
        String description = "";
        String status = "";

        try (Connection conn = DB.getConnection()) {
            PreparedStatement st = conn.prepareStatement(
                "SELECT * FROM tickets WHERE id=?"
            );
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();

            if (!rs.next()) {
                resp.getWriter().write("Ticket not found");
                return;
            }

            title = rs.getString("title");
            description = rs.getString("description");
            status = rs.getString("status");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String html = new String(Files.readAllBytes(Paths.get(
            req.getServletContext().getRealPath("/edit.html")
        )));

        html = html.replace("{{id}}", String.valueOf(id))
            .replace("{{title}}", title)
            .replace("{{description}}", description)
            .replace("{{openSelected}}", status.equals("OPEN") ? "selected" : "")
            .replace("{{closedSelected}}", status.equals("CLOSED") ? "selected" : "");

        resp.setContentType("text/html; charset=UTF-8");
        resp.getWriter().write(html);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        User user = (User) req.getSession().getAttribute("user");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login.html");
            return;
        }

        int id = Integer.parseInt(req.getParameter("id"));
        String title = req.getParameter("title");
        String description = req.getParameter("description");
        String status = req.getParameter("status");

        try (Connection conn = DB.getConnection()) {
            PreparedStatement st = conn.prepareStatement(
                "UPDATE tickets SET title=?, description=?, status=?, updated_at=NOW() WHERE id=?"
            );

            st.setString(1, title);
            st.setString(2, description);
            st.setString(3, status);
            st.setInt(4, id);

            st.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        resp.sendRedirect(req.getContextPath() + "/tickets");
    }
}
