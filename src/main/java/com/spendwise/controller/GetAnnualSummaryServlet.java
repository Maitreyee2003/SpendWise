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

@WebServlet("/getAnnualSummary")
public class GetAnnualSummaryServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            out.print("{}");
            return;
        }
        int userId = (int) session.getAttribute("userId");

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        // Key-Value JSON structure banana (e.g., {"1": 4500.00, "6": 12000.00})
        StringBuilder jsonBuilder = new StringBuilder("{");

        try {
            conn = DatabaseConnection.getConnection();
            
            // 🔥 ADVANCED SQL: Mahina wise group karke total sum nikalna
            String sql = "SELECT MONTH(transaction_date) as month_num, SUM(amount) as total_amount " +
                         "FROM expenses WHERE user_id = ? GROUP BY MONTH(transaction_date)";
                         
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();

            boolean first = true;
            while (rs.next()) {
                if (!first) jsonBuilder.append(",");
                jsonBuilder.append("\"").append(rs.getInt("month_num")).append("\":").append(rs.getDouble("total_amount"));
                first = false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        jsonBuilder.append("}");
        out.print(jsonBuilder.toString()); // Frontend ko map bhej diya
    }
}