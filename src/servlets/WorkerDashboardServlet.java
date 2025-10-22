package servlets;

import utils.DBConnector;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WorkerDashboardServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !"worker".equals(session.getAttribute("role"))) {
            response.sendRedirect("GUI/login.html");
            return;
        }

        int workerId = (int) session.getAttribute("user_id");
        List<Task> tasks = new ArrayList<>();

        try (Connection conn = DBConnector.getConnection()) {
            String sql = "SELECT complaint_id, floor, room, title, description, status, created_at, updated_at " +
                         "FROM complaints WHERE assigned_to = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, workerId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Task t = new Task();
                t.setId(rs.getInt("complaint_id"));
                t.setTitle(rs.getString("title"));
                t.setFloor(rs.getString("floor"));
                t.setRoom(rs.getString("room"));
                t.setDescription(rs.getString("description"));
                t.setStatus(rs.getString("status"));
                t.setCreatedAt(rs.getTimestamp("created_at"));
                t.setUpdatedAt(rs.getTimestamp("updated_at"));
                tasks.add(t);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Set tasks as request attribute
        request.setAttribute("tasks", tasks);

        // Forward to worker dashboard HTML/JSP
        RequestDispatcher dispatcher = request.getRequestDispatcher("/GUI/worker_dashboard.jsp");
        dispatcher.forward(request, response);
    }

    // Simple Task inner class
    public static class Task {
        private int id;
        private String title, floor, room, description, status;
        private Timestamp createdAt, updatedAt;

        // Getters and setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getFloor() { return floor; }
        public void setFloor(String floor) { this.floor = floor; }
        public String getRoom() { return room; }
        public void setRoom(String room) { this.room = room; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public Timestamp getCreatedAt() { return createdAt; }
        public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
        public Timestamp getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
    }
}
