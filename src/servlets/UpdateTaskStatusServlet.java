package servlets;

import utils.DBConnector;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;

public class UpdateTaskStatusServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        if (session == null || !"worker".equals(session.getAttribute("role"))) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            out.println("Unauthorized");
            return;
        }

        String complaintIdStr = request.getParameter("complaint_id");
        String status = request.getParameter("status");

        if (complaintIdStr == null || status == null || status.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println("Invalid parameters");
            return;
        }

        int complaintId;
        try {
            complaintId = Integer.parseInt(complaintIdStr);
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println("Invalid complaint ID");
            return;
        }

        try (Connection conn = DBConnector.getConnection()) {
            String sql = "UPDATE complaints SET status = ?, updated_at = NOW() WHERE complaint_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, status);
            ps.setInt(2, complaintId);

            int updated = ps.executeUpdate();
            if (updated > 0) {
                out.println("Status updated successfully");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.println("Complaint not found");
            }

        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace(out);
        }
    }
}
