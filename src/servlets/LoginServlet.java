package servlets;

import utils.DBConnector;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;

public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try (Connection conn = DBConnector.getConnection()) {
            String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");

                HttpSession session = request.getSession();
                session.setAttribute("user_id", rs.getInt("user_id"));
                session.setAttribute("name", rs.getString("name"));
                session.setAttribute("role", role);

                switch (role.toLowerCase()) {
                    case "admin":
                        response.sendRedirect("GUI/admin_dashboard.html");
                        break;
                    case "incharge":
                        response.sendRedirect("GUI/incharge_dashboard.html");
                        break;
                    case "worker":
                        response.sendRedirect("GUI/worker_dashboard.html");
                        break;
                    case "student":
                        response.sendRedirect("GUI/student_dashboard.html");
                        break;
                    default:
                        out.println("<h3>Invalid role! Contact admin.</h3>");
                }

            } else {
                out.println("<h3>Invalid email or password!</h3>");
                RequestDispatcher rd = request.getRequestDispatcher("GUI/login.html");
                rd.include(request, response);
            }

        } catch (SQLException e) {
            e.printStackTrace(out);
        }
    }
}
