package com.spendwise.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.spendwise.util.DatabaseConnection;

// 🌐 URL Mapping: Frontend se jab bhi JavaScript fetch() ke zariye '/addIncome' hit karega, toh yeh code chalega
@WebServlet("/addIncome")
public class AddIncomeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // 📩 doPost Method: Kyunki hum data submit (POST) kar rahe hain security aur safety ke liye
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // ⚙️ RESPONSE SETUP: Server se wapas jaane wale data ko JSON format me set kar rahe hain
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        // 🔒 SESSION CHECK: Pata kar rahe hain ki koi badmaash bina login kiye toh request nahi bhej raha
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            // Agar session nahi mila ya userId khali hai, toh turant "Unauthorized" ka error response bhej do
            out.print("{\"status\":\"error\",\"message\":\"Unauthorized access! Please login again.\"}");
            return; // Code ko aage badhne se yahi rok do
        }
        
        // 👤 USER ID EXTRACTION: Agar user valid hai, toh session se uski unique id nikal kar variable me save karo
        int userId = (int) session.getAttribute("userId");
        
        // 📥 PARAMETER EXTRACTION: Frontend popup se bheja hua Income Amount aur Month read kar rahe hain
        String incomeAmountStr = request.getParameter("incomeAmount");
        String monthStr = request.getParameter("incomeMonth");
        
        // 🛠️ VALIDATION FILTER: Check kar rahe hain ki parameters khali ya null toh nahi hain
        if (incomeAmountStr == null || monthStr == null || incomeAmountStr.trim().isEmpty() || monthStr.trim().isEmpty()) {
            out.print("{\"status\":\"error\",\"message\":\"Required fields are missing!\"}");
            return; // Kachra data handle na ho, isliye yahi se return ho jao
        }
        
        // 🔢 DATA TYPE CONVERSION: String format wale data ko mathematical Double aur Integer me convert kar rahe hain
        double incomeAmount = Double.parseDouble(incomeAmountStr);
        int monthNum = Integer.parseInt(monthStr);
        
        // 🗄️ DATABASE POINTERS: Connection aur PreparedStatement ko initialize kar rahe hain
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            // 🔌 CONNECTION ESTABLISH: Database utility class se database ka pipeline/connection active karo
            conn = DatabaseConnection.getConnection();
            
            // 💡 ADVANCED SQL LOGIC (UPSERT - INSERT ON DUPLICATE KEY UPDATE):
            // Yeh query pehle check karegi ki kya is 'user_id' aur 'month_num' ka record pehle se table me hai?
            // Agar record NAHI hai: Toh naya INSERT chalega.
            // Agar record PEHLE SE HAI (Unique Key break hogi): Toh automatic database use existing row me UPDATE kar dega.
            // Interviewer ko yeh query bohot attractive lagti hai!
            String sql = "INSERT INTO income (user_id, month_num, amount) VALUES (?, ?, ?) " +
                         "ON DUPLICATE KEY UPDATE amount = ?";
            
            // 🚀 QUERY COMPILATION: PreparedStatement me SQL query ko load kiya taaki SQL Injection attack se bacha ja sake
            stmt = conn.prepareStatement(sql);
            
            // 🎯 PARAMETER MAPPING: Ek-ek karke question marks (?) ki jagah real values fit kar rahe hain
            stmt.setInt(1, userId);          // Pehla (?) -> User ID
            stmt.setInt(2, monthNum);        // Doosra (?) -> Selected Month Number (1 se 12)
            stmt.setDouble(3, incomeAmount);  // Teesra (?) -> Income Amount (Insert case ke liye)
            stmt.setDouble(4, incomeAmount);  // Chautha (?) -> Income Amount (Update case ke liye)
            
            // ⚡ QUERY EXECUTION: Database me query fire karo aur check karo kitne rows par asar hua
            int rows = stmt.executeUpdate();
            
            // 📊 SUCCESS RESPONSE: Agar kamyabi se save ho gaya, toh frontend ko success JSON bhej do
            if (rows > 0) {
                out.print("{\"status\":\"success\",\"message\":\"Income updated successfully!\"}");
            } else {
                out.print("{\"status\":\"error\",\"message\":\"Failed to save income in database context.\"}");
            }
            
        } catch (SQLException e) {
            // 🚨 EXCEPTION HANDLING: Agar database me koi gadbad hui (jaise table nahi mili), toh error trace print karo
            e.printStackTrace();
            out.print("{\"status\":\"error\",\"message\":\"Database error: " + e.getMessage() + "\"}");
        } finally {
            // 🧹 RESOURCE CLEANUP: Kaam khatam hone ke baad connection aur statements ko band karna zaroori hai (No memory leaks)
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}