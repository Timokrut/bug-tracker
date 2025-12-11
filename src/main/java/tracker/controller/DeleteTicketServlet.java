package tracker.controller;

import tracker.model.User;
import tracker.util.DB;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/delete")
public class DeleteTicketServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = (User) req.getSession().getAttribute("user");

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        if (user.role.toString().equals("USER")) {
            resp.sendError(403, "Только менеджер может удалять тикеты");
            return;
        }

        int id = Integer.parseInt(req.getParameter("id"));

        try (Connection conn = DB.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM tickets WHERE id=?");
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        resp.sendRedirect(req.getContextPath() + "/tickets");
    }
}
