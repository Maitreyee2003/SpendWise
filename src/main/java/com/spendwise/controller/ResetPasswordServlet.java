package com.spendwise.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.mindrot.jbcrypt.BCrypt;
import com.spendwise.util.DatabaseConnection;

@WebServlet("/resetPassword")
public class ResetPasswordServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
                          throws ServletException, IOException {

        // Step 1: Session check karo
        // OTP verify hua tha ya nahi
        HttpSession session = request.getSession(false);
        if (session == null ||
            session.getAttribute("otpVerified") == null ||
            session.getAttribute("resetEmail") == null) {
            // Direct yahan aa gaya bina OTP verify kiye
            // Wapas bhejo
            response.sendRedirect("forgot-password.html");
            return;
        }

        // Step 2: Form se naya password lo
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        // Step 3: Dono password same hain ya nahi check karo
        if (!newPassword.equals(confirmPassword)) {
            response.setContentType("text/html");
            response.getWriter().print(
                "<h3 style='color:red; text-align:center;'>" +
                "❌ Passwords do not match!</h3>" +
                "<p style='text-align:center;'>" +
                "<a href='reset-password.html'>Try Again</a></p>");
            return;
        }

        // Step 4: Session se email lo
        String email = (String) session.getAttribute("resetEmail");

        // Step 5: BCrypt se naya password hash karo
        // Same jo register mein kiya tha!
        // "newpass123" → "$2a$10$xxxxx..."
        String hashedPassword = BCrypt.hashpw(
            newPassword, BCrypt.gensalt());

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseConnection.getConnection();

            // Step 6: Database mein naya password update karo
            // OTP bhi clear karo security ke liye
            String sql = 
                "UPDATE users SET password = ?, " +
                "otp = NULL, " +      // OTP delete karo
                "otp_expiry = NULL " + // Expiry bhi delete karo
                "WHERE email = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, hashedPassword); // Naya hashed password
            stmt.setString(2, email);          // Kis user ka?
            stmt.executeUpdate();

            // Step 7: Session clear karo
            // Security ke liye session destroy karo
            session.invalidate();

            // Step 8: Login page pe bhejo success message ke saath
            response.setContentType("text/html");
            response.getWriter().print(
                "<div style='font-family:Arial; " +
                "text-align:center; margin-top:50px;'>" +
                "<h2 style='color:green;'>" +
                "✅ Password Reset Successful!</h2>" +
                "<p>Your password has been updated successfully.</p>" +
                "<a href='login.html' style='background:#00c6ff; " +
                "color:white; padding:10px 20px; " +
                "border-radius:8px; text-decoration:none;'>" +
                "Login Now</a>" +
                "</div>");

        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().print("Database Error: " + e.getMessage());
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}