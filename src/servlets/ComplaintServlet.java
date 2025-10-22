package servlets;

import utils.DBConnector;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;
public class ComplaintServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        if (session == null || !"student".equals(session.getAttribute("role"))) {
            response.sendRedirect("GUI/login.html");
            return;
        }

        int studentId = (int) session.getAttribute("user_id");
        String category = request.getParameter("category");
        String complaintType = request.getParameter("complaintType");
        String floor = request.getParameter("floor");
        String room = request.getParameter("room");
        String description = request.getParameter("description");

        // Append location info to description
        description = description + " (Floor: " + floor + ", Room: " + room + ")";

        try (Connection conn = DBConnector.getConnection()) {
            String sql = "INSERT INTO complaints (student_id, title, description, category, status) VALUES (?, ?, ?, ?, 'Pending')";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, studentId);
            ps.setString(2, complaintType);    // Title
            ps.setString(3, description);      // Description (with floor/room appended)
            ps.setString(4, category);

            int result = ps.executeUpdate();

            if (result > 0) {
                out.println("<h3>Complaint submitted successfully!</h3>");
                response.sendRedirect("GUI/student_dashboard.html"); // redirect back to student page
            } else {
                out.println("<h3>Failed to submit complaint!</h3>");
            }

        } catch (SQLException e) {
            e.printStackTrace(out);
        }
    }
}
