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

// BCrypt import - yahi password ko scramble karega
import org.mindrot.jbcrypt.BCrypt;

import com.spendwise.util.DatabaseConnection;

@WebServlet("/registerUser")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, 
                          HttpServletResponse response) 
                          throws ServletException, IOException {

        // Step 1: Form se user ki details lo
        String name = request.getParameter("fullName");
        String email = request.getParameter("userEmail");
        String password = request.getParameter("userPassword");

        // Step 2: BCrypt se password ko hash karo (scramble karo)
        // gensalt() = extra security layer add karta hai
        // hashpw() = actual scrambling karta hai
        // Example: "mypass123" → "$2a$10$xJwL5v5Jz..."
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            // Step 3: Database connection lo
            conn = DatabaseConnection.getConnection();

            // Step 4: Database mein save karo
            // NOTICE: ab hum hashedPassword save kar rahe hain
            // plain password NAHI!
            String sql = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, hashedPassword); // ✅ hashed password save ho raha hai

            int rowsInserted = stmt.executeUpdate();

            if (rowsInserted > 0) {
                // Registration successful → login page par bhejo
                response.sendRedirect("login.html");
            } else {
                response.getWriter().print("Registration failed. Please try again.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
         // Purana code — boring error
           // response.getWriter().print("Error: Account with this email might already exist!");

            // ✅ Naya code — proper popup message
            response.setContentType("text/html");
            response.getWriter().print(
                "<!DOCTYPE html>" +
                "<html><head>" +
                "<title>SpendWise - Already Registered</title>" +
                "<style>" +
                "body { font-family: Arial; background: linear-gradient(135deg, #0a0a1a, #0d1b2a);" +
                "display:flex; justify-content:center; align-items:center; height:100vh; margin:0;}" +
                // Popup box
                ".box { background: rgba(255,255,255,0.05);" +
                "border: 1px solid rgba(0,198,255,0.3);" +
                "border-radius: 16px; padding: 40px;" +
                "text-align: center; max-width: 400px;}" +
                // Warning icon
                ".icon { font-size: 60px; margin-bottom: 20px;}" +
                // Heading
                "h2 { color: #ff4444; margin-bottom: 10px;}" +
                // Message
                "p { color: #aaa; margin-bottom: 25px;}" +
                // Buttons
                ".btn-login { background: linear-gradient(135deg, #00c6ff, #0072ff);" +
                "color: white; padding: 12px 25px; border-radius: 8px;" +
                "text-decoration: none; font-weight: bold; margin: 5px; display:inline-block;}" +
                ".btn-forgot { background: transparent;" +
                "border: 1px solid #00c6ff; color: #00c6ff;" +
                "padding: 12px 25px; border-radius: 8px;" +
                "text-decoration: none; font-weight: bold; margin: 5px; display:inline-block;}" +
                "</style></head><body>" +
                "<div class='box'>" +
                // Warning icon
                "<div class='icon'>⚠️</div>" +
                // Heading
                "<h2>Already Registered!</h2>" +
                // Message
                "<p>An account with this email already exists.<br>" +
                "Please login or reset your password.</p>" +
                // Two buttons
                "<a href='login.html' class='btn-login'>Login Now</a>" +
                "<a href='forgot-password.html' class='btn-forgot'>Forgot Password?</a>" +
                "</div>" +
                "</body></html>"
            );
        }
    }
}
