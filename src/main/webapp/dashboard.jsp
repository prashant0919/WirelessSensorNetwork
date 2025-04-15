<%@ page import="java.sql.*" %>
<%@ page import="jakarta.servlet.http.HttpSession" %>
<%
    HttpSession sessionObj = request.getSession(false);
    String loggedInUser = (sessionObj != null) ? (String) sessionObj.getAttribute("username") : null;
    if (loggedInUser == null) {
        response.sendRedirect("index.html");
        return;
    }
%>

<!DOCTYPE html>
<html>
<head>
    <title>Admin Dashboard</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body {
            padding: 20px;
        }
        .container {
            max-width: 900px;
            margin: auto;
        }
    </style>
</head>
<body>
    <div class="container">
        <nav class="navbar navbar-expand-lg navbar-dark bg-dark mb-4">
            <div class="container-fluid">
                <span class="navbar-brand">Admin Dashboard</span>
                <div class="d-flex">
                    <span class="navbar-text me-3">Welcome, <%= loggedInUser %>!</span>
                    <!-- Logout Form (POST) -->
                    <form action="adminpanel" method="post">
                        <input type="hidden" name="action" value="logout">
                        <button type="submit" class="btn btn-danger">Logout</button>
                    </form>
                </div>
            </div>
        </nav>

        <h3>Register New Employee</h3>
        <form action="adminpanel" method="post" class="mb-4">
            <input type="hidden" name="action" value="register">
            <div class="mb-3">
                <label class="form-label">Full Name</label>
                <input type="text" name="full_name" class="form-control" required>
            </div>
            <div class="mb-3">
                <label class="form-label">Email</label>
                <input type="email" name="email" class="form-control" required>
            </div>
            <div class="mb-3">
                <label class="form-label">Phone</label>
                <input type="text" name="phone" class="form-control" required>
            </div>
            <div class="mb-3">
                <label class="form-label">Role</label>
                <input type="text" name="role" class="form-control" required>
            </div>
            <div class="mb-3">
                <label class="form-label">Department</label>
                <input type="text" name="department" class="form-control" required>
            </div>
            <div class="mb-3">
                <label class="form-label">Username</label>
                <input type="text" name="username" class="form-control" required>
            </div>
            <div class="mb-3">
                <label class="form-label">Password</label>
                <input type="password" name="password" class="form-control" required>
            </div>
            <button type="submit" class="btn btn-primary">Register</button>
        </form>

        <h3>Employee List</h3>
        <table class="table table-bordered table-striped">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Full Name</th>
                    <th>Email</th>
                    <th>Phone</th>
                    <th>Role</th>
                    <th>Department</th>
                    <th>Username</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <%
                    Connection conn = null;
                    PreparedStatement stmt = null;
                    ResultSet rs = null;
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/userdb", "root", "jumboy3300");
                        stmt = conn.prepareStatement("SELECT * FROM users WHERE username != 'admin'");
                        rs = stmt.executeQuery();
                        while (rs.next()) {
                            int userId = rs.getInt("id");
                %>
                <tr>
                    <td><%= userId %></td>
                    <td><%= rs.getString("full_name") %></td>
                    <td><%= rs.getString("email") %></td>
                    <td><%= rs.getString("phone") %></td>
                    <td><%= rs.getString("role") %></td>
                    <td><%= rs.getString("department") %></td>
                    <td><%= rs.getString("username") %></td>
                    <td>
                        <!-- Delete Form -->
                        <form action="adminpanel" method="post" style="display:inline;">
                            <input type="hidden" name="action" value="delete">
                            <input type="hidden" name="id" value="<%= userId %>">
                            <button type="submit" class="btn btn-danger btn-sm">Delete</button>
                        </form>

                        <!-- Update Button (Opens Modal) -->
                        <button type="button" class="btn btn-warning btn-sm" data-bs-toggle="modal" data-bs-target="#updateModal<%= userId %>">
                            Update
                        </button>

                        <!-- Update Modal -->
                        <div class="modal fade" id="updateModal<%= userId %>" tabindex="-1" aria-labelledby="updateModalLabel" aria-hidden="true">
                            <div class="modal-dialog">
                                <div class="modal-content">
                                    <div class="modal-header">
                                        <h5 class="modal-title" id="updateModalLabel">Update Employee</h5>
                                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                    </div>
                                    <div class="modal-body">
                                        <form action="adminpanel" method="post">
                                            <input type="hidden" name="action" value="update">
                                            <input type="hidden" name="id" value="<%= userId %>">
                                            <div class="mb-3">
                                                <label class="form-label">Full Name</label>
                                                <input type="text" name="full_name" class="form-control" value="<%= rs.getString("full_name") %>" required>
                                            </div>
                                            <div class="mb-3">
                                                <label class="form-label">Email</label>
                                                <input type="email" name="email" class="form-control" value="<%= rs.getString("email") %>" required>
                                            </div>
                                            <div class="mb-3">
                                                <label class="form-label">Phone</label>
                                                <input type="text" name="phone" class="form-control" value="<%= rs.getString("phone") %>" required>
                                            </div>
                                            <div class="mb-3">
                                                <label class="form-label">Role</label>
                                                <input type="text" name="role" class="form-control" value="<%= rs.getString("role") %>" required>
                                            </div>
                                            <div class="mb-3">
                                                <label class="form-label">Department</label>
                                                <input type="text" name="department" class="form-control" value="<%= rs.getString("department") %>" required>
                                            </div>
                                            <div class="mb-3">
                                                <label class="form-label">Username</label>
                                                <input type="text" name="username" class="form-control" value="<%= rs.getString("username") %>" required>
                                            </div>
                                            <button type="submit" class="btn btn-primary">Update</button>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </td>
                </tr>
                <%
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (rs != null) rs.close();
                        if (stmt != null) stmt.close();
                        if (conn != null) conn.close();
                    }
                %>
            </tbody>
        </table>
    </div>

    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
