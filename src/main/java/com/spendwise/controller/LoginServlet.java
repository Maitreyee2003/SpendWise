package com.spendwise.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

// BCrypt import
import org.mindrot.jbcrypt.BCrypt;

import com.spendwise.util.DatabaseConnection;

@WebServlet("/loginUser")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, 
                          HttpServletResponse response) 
                          throws ServletException, IOException {

        // Step 1: Login form se email aur password lo
        String email = request.getParameter("userEmail");
        String password = request.getParameter("userPassword"); // plain text jo user ne type kiya

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            // Step 2: Database connection lo
            conn = DatabaseConnection.getConnection();

            // Step 3: Sirf email se user dhundo database mein
            // NOTICE: ab hum password WHERE clause mein NAHI daal rahe!
            // Kyunki database mein hashed password hai, plain text nahi
            String sql = "SELECT id, name, password FROM users WHERE email = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);

            rs = stmt.executeQuery();

            if (rs.next()) {
                // Step 4: Database se hashed password nikalo
                String storedHashedPassword = rs.getString("password");

                // Step 5: BCrypt.checkpw() se compare karo
                // yeh automatically user ka plain password ko
                // database ke hashed password se compare karta hai
                // "mypass123" vs "$2a$10$xJwL..." → TRUE ya FALSE return karta hai
                boolean passwordMatch = BCrypt.checkpw(password, storedHashedPassword);

                if (passwordMatch) {
                    // ✅ Password sahi hai!
                    int userId = rs.getInt("id");
                    String userName = rs.getString("name");

                    // Step 6: Session mein user ki info save karo
                    HttpSession session = request.getSession();
                    session.setAttribute("userId", userId);
                    session.setAttribute("userName", userName);

                    // Step 7: Dashboard par bhejo
                    response.sendRedirect("index.jsp");

                } else {
                    // ❌ Password galat hai!
                    response.setContentType("text/html");
                    response.getWriter().print("<h3 style='color:red; text-align:center;'>Invalid Email or Password!</h3>");
                    response.getWriter().print("<p style='text-align:center;'><a href='login.html'>Try Again</a></p>");
                }

            } else {
                // ❌ Email hi nahi mili database mein
                response.setContentType("text/html");
                response.getWriter().print("<h3 style='color:red; text-align:center;'>No account found with this email!</h3>");
                response.getWriter().print("<p style='text-align:center;'><a href='login.html'>Try Again</a></p>");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().print("Database Error: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}