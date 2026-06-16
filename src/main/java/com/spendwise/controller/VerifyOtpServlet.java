package com.spendwise.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.spendwise.util.DatabaseConnection;

@WebServlet("/verifyOtp")
public class VerifyOtpServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
                          throws ServletException, IOException {

        // Step 1: User ne jo OTP dala woh lo
        String enteredOtp = request.getParameter("otp");

        // Step 2: Session se email lo
        // ForgotPasswordServlet ne save ki thi
        HttpSession session = request.getSession(false);
        if (session == null || 
            session.getAttribute("resetEmail") == null) {
            // Session nahi hai matlab seedha 
            // yahan aa gaya — wapas bhejo
            response.sendRedirect("forgot-password.html");
            return;
        }

        // Session se email nikalo
        String email = (String) session.getAttribute("resetEmail");

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();

            // Step 3: Database se OTP aur expiry time lo
            String sql = 
                "SELECT otp, otp_expiry FROM users WHERE email = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            rs = stmt.executeQuery();

            if (rs.next()) {
                // Database se OTP nikalo
                String savedOtp = rs.getString("otp");
                
                // Database se expiry time nikalo
                Timestamp expiryTime = rs.getTimestamp("otp_expiry");
                
                // Current time
                Timestamp currentTime = 
                    new Timestamp(System.currentTimeMillis());

                // Step 4: Check karo OTP expire hua ya nahi
                // currentTime > expiryTime matlab expire ho gaya!
                if (currentTime.after(expiryTime)) {
                    // OTP expire ho gaya!
                    response.setContentType("text/html");
                    response.getWriter().print(
                        "<h3 style='color:red; text-align:center;'>" +
                        "⏰ OTP Expired! Please request a new one.</h3>" +
                        "<p style='text-align:center;'>" +
                        "<a href='forgot-password.html'>Get New OTP</a></p>");
                    return;
                }

                // Step 5: Check karo OTP sahi hai ya nahi
                if (savedOtp.equals(enteredOtp)) {
                    // ✅ OTP sahi hai!
                    // Session mein mark karo ki OTP verify ho gaya
                    session.setAttribute("otpVerified", true);
                    
                    // Reset password page pe bhejo
                    response.sendRedirect("reset-password.html");
                } else {
                    // ❌ OTP galat hai!
                    response.setContentType("text/html");
                    response.getWriter().print(
                        "<h3 style='color:red; text-align:center;'>" +
                        "❌ Invalid OTP! Please try again.</h3>" +
                        "<p style='text-align:center;'>" +
                        "<a href='verify-otp.html'>Try Again</a></p>");
                }
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