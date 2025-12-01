package tracker.controller; 

import tracker.model.*; 
import tracker.util.DB;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

import java.io.IOException;
import java.sql.*;

@WebServlet("/tickets")
public class TicketListServlet extends HttpServlet {
    @Override 
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html; charset=UTF-8");

        User user = (User) req.getSession().getAttribute("user");
        if (user == null) {
            resp.sendRedirect("/login.html");
            return;
        }

        StringBuilder html = new StringBuilder();
        html.append("<html><head><title>Tickets</title></head><body>");
        html.append("<h1>Welcome, " + user.username + "</h1>");

        html.append("<a href='/create.html'>Create ticket</a><br><br>");

        // OPEN TICKETS 
        html.append("<h2>Open Tickets</h2>");
        try (Connection conn = DB.getConnection()) {
            PreparedStatement st = conn.prepareStatement(
                "SELECT * FROM tickets WHERE status='OPEN'"
            );
            ResultSet rs = st.executeQuery();

            html.append("<ul>");
            while (rs.next()) {
                int id = rs.getInt("id");
                html.append("<li><a href='/edit?id=" + id + "'>" + rs.getString("title") + "</a></li>");
            }
            html.append("</ul>");

            // CLOSED TICKETS
            html.append("<h2>Closed Tickets</h2>");
            st = conn.prepareStatement(
                "SELECT * FROM ticket WHERE status='CLOSED'"
            );
            rs = st.executeQuery();

            html.append("<ul>");
            while (rs.next()) {
                int id = rs.getInt("id");
                html.append("<li><a href='/edit?id=" + id + "'>" + rs.getString("title") + "</a></li>");
            }
            html.append("</ul>");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        html.append("</body></html>");
        resp.getWriter().write(html.toString());
    }
}
