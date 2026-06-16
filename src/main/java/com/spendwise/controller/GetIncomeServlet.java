package com.spendwise.controller;

import java.io.IOException;
import java.io.PrintWriter;
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
import com.spendwise.util.DatabaseConnection;

// 🌐 URL Mapping: Frontend se jab fetch(`getIncome?month=X`) chalega, toh yeh servlet data supply karega
@WebServlet("/getIncome")
public class GetIncomeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // 📤 doGet Method: Kyunki hum database se data read/fetch (GET) kar rahe hain
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // ⚙️ RESPONSE SETUP: Output ko pure JSON format me configure kar rahe hain
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        // 🔒 SECURITY CHECK: Check karo ki valid active logged-in user hi data mang raha hai na
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            out.print("{\"status\":\"error\",\"message\":\"Unauthorized!\"}");
            return; // Secure block exit
        }
        
        // 👤 USER ID EXTRACTION: Logged-in user ki primary id fetch karo
        int userId = (int) session.getAttribute("userId");
        
        // 📥 PARAMETER EXTRACTION: URL query string se active month parameter read karo (e.g., ?month=1)
        String monthStr = request.getParameter("month");
        
        // 🛡️ EDGE CASE PROTECTION: Agar dropdown ka month kisi wajah se khali aaya, toh default 0.0 income bhej do
        if (monthStr == null || monthStr.trim().isEmpty()) {
            out.print("{\"amount\": 0.0}");
            return;
        }
        
        // 🔢 CONVERSION: Month string ko integer me badlo
        int monthNum = Integer.parseInt(monthStr);
        
        // 🗄️ DATABASE OBJECT INSTANCES
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;          // Pointer table se aane wale database records rows ko hold karne ke liye
        double incomeAmount = 0.0;    // Default income container variable
        
        try {
            // 🔌 DB CONNECTION: Database pipeline open kijiye
            conn = DatabaseConnection.getConnection();
            
            // 🔍 SQL SELECT QUERY: Is active user aur is selected month ka single income row select karo
            String sql = "SELECT amount FROM income WHERE user_id = ? AND month_num = ?";
            
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);   // Pehla (?) -> Logged In User
            stmt.setInt(2, monthNum); // Doosra (?) -> Active Dropdown Month
            
            // ⚡ QUERY EXECUTION: Select query chalane ke liye executeQuery() ka use karte hain, jo data ResultSet me deta hai
            rs = stmt.executeQuery();
            
            // 📋 RECORD ITERATION: Agar database table me record mil gaya...
            if (rs.next()) {
                // ...toh 'amount' column ki numeric value nikal kar hamare variable me store kar lo
                incomeAmount = rs.getDouble("amount");
            }
            
            // 📤 CLEAN JSON OUTPUT: Frontend JavaScript ko pure standard numeric key-value return block bhej do
            out.print("{\"amount\": " + incomeAmount + "}");
            
        } catch (SQLException e) {
            e.printStackTrace();
            // Failure fallback filter: Agar crash hua toh safety ke liye 0.0 value response me wrap up kar do
            out.print("{\"amount\": 0.0, \"error\": \"" + e.getMessage() + "\"}");
        } finally {
            // 🧹 FINAL RESOURCE CLOSURE CLOSING PIPELINE: Memory optimize karne ke liye saare database objects reverse order me close karo
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