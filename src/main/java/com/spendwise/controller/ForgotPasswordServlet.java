package com.spendwise.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Random;

// ✅ Yeh sahi imports hain JavaMail ke liye
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.spendwise.util.DatabaseConnection;

@WebServlet("/forgotPassword")
public class ForgotPasswordServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Yeh SpendWise ka Gmail hai jo OTP bhejega
    private static final String SENDER_EMAIL = "spendwise.notify@gmail.com";
    
    // Yeh App Password hai jo humne generate kiya tha
 // PURANA - Dangerous! ❌
    //private static final String APP_PASSWORD = "lonbynynwyplpoua";

    // NAYA - Safe! ✅
    private static final String APP_PASSWORD = System.getenv("GMAIL_APP_PASSWORD") != null ? 
        System.getenv("GMAIL_APP_PASSWORD") : "lonbynynwyplpoua";

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
                          throws ServletException, IOException {

        // Step 1: User ne jo email daali woh lo
        String userEmail = request.getParameter("userEmail");

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            // Step 2: Database se check karo 
            // kya yeh email registered hai ya nahi
            conn = DatabaseConnection.getConnection();
            String checkSql = "SELECT id FROM users WHERE email = ?";
            stmt = conn.prepareStatement(checkSql);
            stmt.setString(1, userEmail);
            rs = stmt.executeQuery();

            if (!rs.next()) {
                // Email database mein nahi mili
                // User ko error page pe bhejo
                response.setContentType("text/html");
                response.getWriter().print(
                    "<h3 style='color:red; text-align:center;'>" +
                    "❌ No account found with this email!</h3>" +
                    "<p style='text-align:center;'>" +
                    "<a href='forgot-password.html'>Try Again</a></p>");
                return;
            }

            // Step 3: 6 digit random OTP banao
            // Example: 847291
            Random random = new Random();
            // 100000 se 999999 ke beech ka number
            int otpNumber = 100000 + random.nextInt(900000);
            String otp = String.valueOf(otpNumber);

            // Step 4: OTP expiry time set karo
            // Current time + 10 minutes
            // java.sql.Timestamp → database mein time save karne ke liye
            long currentTime = System.currentTimeMillis();
            long expiryTime = currentTime + (10 * 60 * 1000);
            // 10 min × 60 sec × 1000 milliseconds
            java.sql.Timestamp expiryTimestamp = 
                new java.sql.Timestamp(expiryTime);

            // Step 5: OTP aur expiry database mein save karo
            String updateSql = 
                "UPDATE users SET otp = ?, otp_expiry = ? WHERE email = ?";
            stmt = conn.prepareStatement(updateSql);
            stmt.setString(1, otp);           // OTP save karo
            stmt.setTimestamp(2, expiryTimestamp); // Expiry time save karo
            stmt.setString(3, userEmail);     // Kis user ka? Email se dhundo
            stmt.executeUpdate();

         // try-catch se wrap karo
            try {
                // Gmail se OTP email bhejo
                sendOtpEmail(userEmail, otp);
            } catch (MessagingException e) {
                // Agar email send nahi hui toh error do
                e.printStackTrace();
                response.setContentType("text/html");
                response.getWriter().print(
                    "<h3 style='color:red; text-align:center;'>" +
                    "❌ Failed to send OTP email!" +
                    " Please try again.</h3>" +
                    "<p style='text-align:center;'>" +
                    "<a href='forgot-password.html'>Try Again</a></p>"
                );
                return;
            }

            // Step 7: User ki email session mein save karo
            // Taaki verify-otp page pe bhi pata rahe
            // kaun verify kar raha hai
            HttpSession session = request.getSession();
            session.setAttribute("resetEmail", userEmail);

            // Step 8: OTP verify karne wale page pe bhejo
            response.sendRedirect("verify-otp.html");

        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().print("Database Error: " + e.getMessage());
        } finally {
            // Database connection band karo
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Yeh method Gmail se OTP email bhejta hai
    private void sendOtpEmail(String toEmail, String otp) 
        throws MessagingException {

    	// PURANI properties hatao aur YEH dalo
    	Properties props = new Properties();

    	// Gmail ka server
    	props.put("mail.smtp.host", "smtp.gmail.com");

    	// Port 465 use karo - SSL ke liye
    	// 587 ki jagah 465!
    	props.put("mail.smtp.port", "465");

    	// Password se login
    	props.put("mail.smtp.auth", "true");

    	// SSL enable karo
    	// starttls ki jagah SSL!
    	props.put("mail.smtp.ssl.enable", "true");

    	// SSL protocols
    	props.put("mail.smtp.ssl.protocols", "TLSv1.2");

    	// Trust Gmail server
    	props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        // Step 2: Gmail mein login karo
        // App Password use kar rahe hain real password nahi!
        Session mailSession = Session.getInstance(props,
            new Authenticator() {
                protected PasswordAuthentication 
                    getPasswordAuthentication() {
                    return new PasswordAuthentication(
                        SENDER_EMAIL,  // spendwise.notify@gmail.com
                        APP_PASSWORD   // lonbynynwyplpoua
                    );
                }
            });

        // Step 3: Email banao
        Message message = new MimeMessage(mailSession);

        // Sender kaun hai
        message.setFrom(new InternetAddress(SENDER_EMAIL));

        // Receiver kaun hai
        message.setRecipient(Message.RecipientType.TO,
            new InternetAddress(toEmail));

        // Email ka subject
        message.setSubject("🔐 SpendWise Password Reset OTP");

        // Email ka body matter
        // HTML format mein likha hai taaki
        // email sundar dikhe
        String emailBody = 
            "<div style='font-family:Arial; max-width:500px; margin:auto;'>" +
            "<div style='background:#0a0a1a; padding:20px; text-align:center;'>" +
            "<h2 style='color:#00c6ff;'>💰 SpendWise</h2>" +
            "</div>" +
            "<div style='padding:30px; background:#f9f9f9;'>" +
            "<h3>Password Reset Request</h3>" +
            "<p>Your OTP for password reset is:</p>" +
            // OTP bada aur bold dikhega
            "<div style='background:#00c6ff; color:white; " +
            "font-size:32px; font-weight:bold; " +
            "text-align:center; padding:20px; " +
            "border-radius:8px; letter-spacing:10px;'>" +
            otp +
            "</div>" +
            "<p style='color:red; margin-top:20px;'>" +
            "⚠️ This OTP is valid for <b>10 minutes only!</b></p>" +
            "<p>If you didn't request this, " +
            "please ignore this email.</p>" +
            "</div>" +
            "<div style='background:#0a0a1a; padding:15px; " +
            "text-align:center;'>" +
            "<p style='color:#aaa; font-size:12px;'>" +
            "© 2026 SpendWise — Personal Finance Manager</p>" +
            "</div>" +
            "</div>";

        // HTML format set karo
        message.setContent(emailBody, "text/html");

        // Step 4: Email bhej do!
        Transport.send(message);
    }
}