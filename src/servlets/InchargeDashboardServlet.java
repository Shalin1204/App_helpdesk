package servlets;

import utils.DBConnector;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;
import java.util.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class InchargeDashboardServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        if (session == null || !"incharge".equals(session.getAttribute("role"))) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"error\": \"Unauthorized\"}");
            return;
        }

        try (Connection conn = DBConnector.getConnection()) {
            String sql = "SELECT complaint_id, assigned_to, floor, room, status FROM complaints ORDER BY complaint_id DESC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            JSONArray complaintsArray = new JSONArray();

            while (rs.next()) {
                JSONObject obj = new JSONObject();
                obj.put("complaint_id", rs.getInt("complaint_id"));
                obj.put("assigned_to", rs.getString("assigned_to") != null ? rs.getString("assigned_to") : "");
                obj.put("floor", rs.getString("floor") != null ? rs.getString("floor") : "");
                obj.put("room", rs.getString("room") != null ? rs.getString("room") : "");
                obj.put("status", rs.getString("status") != null ? rs.getString("status") : "Pending");

                complaintsArray.put(obj);
            }

            out.print(complaintsArray.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"Database error\"}");
        }
    }
}
