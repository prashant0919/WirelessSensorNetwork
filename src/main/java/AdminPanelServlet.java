import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/adminpanel") // Servlet Mapping
public class AdminPanelServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/userdb";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "jumboy3300";

    public void init() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("INSERT IGNORE INTO users (id, username, password, full_name, email, phone, role, department) VALUES (1, 'admin', 'admin123', 'Administrator', 'admin@example.com', '1234567890', 'Admin', 'Management')");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String action = request.getParameter("action");

        if (action == null || action.trim().isEmpty()) {
            out.println("<h3>Error: Missing action parameter.</h3>");
            return;
        }

        try {
            switch (action) {
                case "login":
                    loginUser(request, response, out);
                    break;
                case "register":
                    registerEmployee(request, response, out);
                    break;
                case "delete":
                    deleteEmployee(request, response, out);
                    break;
                case "update":
                    updateEmployee(request, response, out);
                    break;
                case "logout":
                    logoutUser(request, response);
                    break;
                default:
                    out.println("<h3>Error: Invalid action '" + action + "'.</h3>");
            }
        } catch (Exception e) {
            out.println("<h3>Error: " + e.getMessage() + "</h3>");
            e.printStackTrace();
        }
    }

    private void loginUser(HttpServletRequest request, HttpServletResponse response, PrintWriter out) throws Exception {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
            response.sendRedirect("index.html?error=missing");
            return;
        }

        Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE username=? AND password=?");
        stmt.setString(1, username);
        stmt.setString(2, password);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            HttpSession session = request.getSession();
            session.setAttribute("username", username);
            response.sendRedirect("dashboard.jsp");
        } else {
            response.sendRedirect("index.html?error=invalid");
        }
        conn.close();
    }

    private void registerEmployee(HttpServletRequest request, HttpServletResponse response, PrintWriter out) throws Exception {
        String fullName = request.getParameter("full_name");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String role = request.getParameter("role");
        String department = request.getParameter("department");
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if ("admin".equals(username)) {
            out.println("<h3>Error: Cannot register admin.</h3>");
            return;
        }

        Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO users (full_name, email, phone, role, department, username, password) VALUES (?, ?, ?, ?, ?, ?, ?)"
        );

        stmt.setString(1, fullName);
        stmt.setString(2, email);
        stmt.setString(3, phone);
        stmt.setString(4, role);
        stmt.setString(5, department);
        stmt.setString(6, username);
        stmt.setString(7, password);

        stmt.executeUpdate();

        out.println("<h3>Registration successful for user: " + username + "</h3>");
        conn.close();
    }

    private void deleteEmployee(HttpServletRequest request, HttpServletResponse response, PrintWriter out) throws Exception {
        int id = Integer.parseInt(request.getParameter("id"));

        Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM users WHERE id=? AND username!='admin'");
        stmt.setInt(1, id);
        int rows = stmt.executeUpdate();

        if (rows > 0) {
            out.println("<h3>User deleted successfully.</h3>");
        } else {
            out.println("<h3>Error: User not found or cannot delete admin.</h3>");
        }
        conn.close();
    }

    private void updateEmployee(HttpServletRequest request, HttpServletResponse response, PrintWriter out) throws Exception {
    int id = Integer.parseInt(request.getParameter("id"));
    String fullName = request.getParameter("full_name");
    String email = request.getParameter("email");
    String phone = request.getParameter("phone");
    String role = request.getParameter("role");
    String department = request.getParameter("department");
    String username = request.getParameter("username");
    String password = request.getParameter("password");

    Connection conn = getConnection();
    String sql;
    PreparedStatement stmt;

    if (password == null || password.trim().isEmpty()) {
        sql = "UPDATE users SET full_name=?, email=?, phone=?, role=?, department=?, username=? WHERE id=? AND username!='admin'";
        stmt = conn.prepareStatement(sql);
        stmt.setString(1, fullName);
        stmt.setString(2, email);
        stmt.setString(3, phone);
        stmt.setString(4, role);
        stmt.setString(5, department);
        stmt.setString(6, username);
        stmt.setInt(7, id);
    } else {
        sql = "UPDATE users SET full_name=?, email=?, phone=?, role=?, department=?, username=?, password=? WHERE id=? AND username!='admin'";
        stmt = conn.prepareStatement(sql);
        stmt.setString(1, fullName);
        stmt.setString(2, email);
        stmt.setString(3, phone);
        stmt.setString(4, role);
        stmt.setString(5, department);
        stmt.setString(6, username);
        stmt.setString(7, password);
        stmt.setInt(8, id);
    }

    int rows = stmt.executeUpdate();
    if (rows > 0) {
        out.println("<h3>User updated successfully.</h3>");
    } else {
        out.println("<h3>Error: Cannot update admin or user not found.</h3>");
    }
    conn.close();
}

    private void logoutUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        session.invalidate();
        response.sendRedirect("index.html");
    }

    private Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }
}
