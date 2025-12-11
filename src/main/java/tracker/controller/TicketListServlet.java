package tracker.controller; 

import tracker.model.*; 
import tracker.util.DB;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@WebServlet("/tickets")
public class TicketListServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html; charset=UTF-8");

        User user = (User) req.getSession().getAttribute("user");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String contextPath = req.getContextPath();
        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html>");
        html.append("<html lang=\"ru\">");
        html.append("<head>");
        html.append("<meta charset=\"UTF-8\">");
        html.append("<title>Список тикетов</title>");
        html.append("<link rel=\"stylesheet\" href=\"" + contextPath + "/style.css\" />");
        html.append("</head>");
        html.append("<body>");

        html.append("<h1>Список тикетов</h1>");
        html.append("<p>Добро пожаловать, ").append(user.username).append("</p>");
        html.append("<p>Роль: ").append(user.role).append("</p>");

        html.append("<a class=\"button\" href=\"" + contextPath + "/create\">Создать тикет</a>");
        if (user.role.toString().equals("MANAGER")) {
            html.append("<p><a href=\"" + contextPath + "/users\">Управление пользователями</a></p>");
        }

        html.append("<p><a href=\"" + contextPath + "/logout\">Выйти</a></p>");

        try (Connection conn = DB.getConnection()) {
            // Open Tickets
            html.append("<h2>Открытые тикеты</h2>");
            html.append("<table>");
            html.append("<tr><th>ID</th><th>Название</th><th>Тип</th><th>Ответственный</th><th>Создан</th><th>Последнее изменение</th><th>Действия</th></tr>");

            PreparedStatement st = conn.prepareStatement(
                "SELECT id, title, type, assignee_id, created_at, updated_at FROM tickets WHERE status='OPEN'"
            );
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String type = rs.getString("type");
                String assigneeId = rs.getString("assignee_id");
                String createdAt = rs.getString("created_at");
                String updatedAt = rs.getString("updated_at");
                
                PreparedStatement statement = conn.prepareStatement(
                    "SELECT username FROM users WHERE id=" + assigneeId
                );
                ResultSet res = statement.executeQuery();

                String assignee = "";
                while (res.next()) {
                    assignee = res.getString("username");
                }

                if (assignee == null || assignee == "") {
                    assignee = "Нет ответсвенного";
                }

                DateTimeFormatter dbFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss[.SSSSSS][.SSSSS][.SSSS][.SSS][.SS][.S]");
                LocalDateTime dateCreated = LocalDateTime.parse(createdAt, dbFormatter);
                LocalDateTime dateUpdated = LocalDateTime.parse(updatedAt, dbFormatter);

                DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

                createdAt = dateCreated.format(displayFormatter);
                updatedAt = dateUpdated.format(displayFormatter);

                html.append("<tr>");
                html.append("<td>").append(id).append("</td>");
                html.append("<td>").append(title).append("</td>");
                html.append("<td>").append(type).append("</td>");
                html.append("<td>").append(assignee).append("</td>");
                html.append("<td>").append(createdAt).append("</td>");
                html.append("<td>").append(updatedAt).append("</td>");

                if (user.role.toString().equals("USER") && (!user.username.equals(assignee))) {
                    html.append("<td>Нет доступных действий</td>");
                } else {
                    if (user.role.toString().equals("USER")) {
                        html.append("<td><a class=\"edit-link\" href=\"" + contextPath + "/edit?id=" + id + "\">Редактировать</a></td>");
                    } else {
                        html.append("<td><a class=\"edit-link\" href=\"" + contextPath + "/edit?id=" + id + "\">Редактировать</a><br><a href=\"delete?id=" + id + "\"onclick=\"return confirm('Точно удалить тикет?');\"style=\"color:red;\">Удалить</a></td>");
                    }
                }
                html.append("</tr>");
            }
            html.append("</table>");

            // Closed Tickets
            html.append("<h2>Закрытые тикеты</h2>");
            html.append("<table>");
            html.append("<tr><th>ID</th><th>Название</th><th>Действия</th></tr>");

            st = conn.prepareStatement(
                "SELECT id, title, type, assignee_id, created_at, updated_at FROM tickets WHERE status='CLOSED'"
            );
            rs = st.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String type = rs.getString("type");
                String assigneeId = rs.getString("assignee_id");
                String createdAt = rs.getString("created_at");
                String updatedAt = rs.getString("updated_at");

                PreparedStatement statement = conn.prepareStatement(
                    "SELECT username FROM users WHERE id=" + assigneeId
                );
                ResultSet res = statement.executeQuery();

                String assignee = "";
                while (res.next()) {
                    assignee = res.getString("username");
                }

                if (assignee == null || assignee == "") {
                    assignee = "Нет ответсвенного";
                }

                DateTimeFormatter dbFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss[.SSSSSS][.SSSSS][.SSSS][.SSS][.SS][.S]");
                LocalDateTime dateCreated = LocalDateTime.parse(createdAt, dbFormatter);
                LocalDateTime dateUpdated = LocalDateTime.parse(updatedAt, dbFormatter);

                DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

                createdAt = dateCreated.format(displayFormatter);
                updatedAt = dateUpdated.format(displayFormatter);

                html.append("<tr>");
                html.append("<td>").append(id).append("</td>");
                html.append("<td>").append(title).append("</td>");
                html.append("<td>").append(type).append("</td>");
                html.append("<td>").append(assignee).append("</td>");
                html.append("<td>").append(createdAt).append("</td>");
                html.append("<td>").append(updatedAt).append("</td>");
                
                if (user.role.toString().equals("USER") && (!user.username.equals(assignee))) {
                    html.append("<td>Нет доступных действий</td>");
                } else {
                    if (user.role.toString().equals("USER")) {
                        html.append("<td><a class=\"edit-link\" href=\"" + contextPath + "/edit?id=" + id + "\">Редактировать</a></td>");
                    } else {
                        html.append("<td><a class=\"edit-link\" href=\"" + contextPath + "/edit?id=" + id + "\">Редактировать</a><br><a href=\"delete?id=" + id + "\"onclick=\"return confirm('Точно удалить тикет?');\"style=\"color:red;\">Удалить</a></td>");
                    }
                }

                html.append("</tr>");
            }
            html.append("</table>");

        } catch (SQLException e) {
            e.printStackTrace();
            html.append("<p>Ошибка при загрузке тикетов.</p>");
            html.append(e.toString());
        }

        html.append("</body></html>");

        resp.getWriter().write(html.toString());
    }
}

