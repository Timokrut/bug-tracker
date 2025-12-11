package tracker.controller;

import tracker.model.User;
import tracker.util.DB;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.*;
import java.sql.*;

@WebServlet("/users")
public class UserListServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User current = (User) req.getSession().getAttribute("user");
        if (current == null) {
            resp.sendRedirect("login");
            return;
        }

        if (!"MANAGER".equals(current.getRole().toString())) {
            resp.sendError(403, "Недостаточно прав");
            return;
        }

        resp.setContentType("text/html; charset=UTF-8");
        PrintWriter w = resp.getWriter();

        StringBuilder table = new StringBuilder();

        table.append("<html><head>");
        table.append("<meta charset='UTF-8'>");
        table.append("<title>Управление пользователями</title>");
        table.append("<style>");
        table.append("body { font-family: Arial; background:#f2f2f2; }");
        table.append("table { width: 70%; margin: 40px auto; border-collapse: collapse; background:white; }");
        table.append("th, td { padding: 12px; border-bottom: 1px solid #ccc; }");
        table.append("th { background: #eee; text-align: left; }");
        table.append("a { text-decoration:none; color:#0066cc; }");
        table.append("a:hover { text-decoration:underline; }");
        table.append(".delete { color:red; }");
        table.append("</style>");
        table.append("</head><body>");

        table.append("<h2 style='text-align:center;'>Управление пользователями</h2>");

        table.append("<table>");
        table.append("<tr>");
        table.append("<th>Username</th>");
        table.append("<th>Редактировать</th>");
        table.append("<th>Удалить</th>");
        table.append("</tr>");


        try (Connection conn = DB.getConnection()) {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT id, username FROM users ORDER BY id");

            while (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");

                table.append("<tr>");
                table.append("<td>").append(username).append("</td>");
                table.append("<td><a href=\"edit-user?id=").append(id).append("\">Редактировать</a></td>");
                table.append("<td><a class=\"delete\" href=\"delete-user?id=").append(id).append("\" onclick=\"return confirm('Удалить пользователя?');\">Удалить</a></td>");
                table.append("</tr>");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        table.append("</table>");
        table.append("<p style='text-align:center;'><a href='/bug-tracker/tickets'>Назад</a></p>");
        table.append("</body></html>");

        w.write(table.toString());
    }
}
