package com.spendwise.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.spendwise.util.DatabaseConnection;

@WebServlet("/getExpenses")
public class GetExpensesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            out.print("[]");
            return;
        }
        int userId = (int) session.getAttribute("userId");
        String selectedMonth = request.getParameter("month");
        
        if (selectedMonth == null || selectedMonth.trim().isEmpty()) {
            selectedMonth = String.valueOf(Calendar.getInstance().get(Calendar.MONTH) + 1);
        }

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        StringBuilder jsonBuilder = new StringBuilder("[");

        try {
            conn = DatabaseConnection.getConnection();
            // SELECT id ko badhaya gaya hai delete reference ke liye
            String sql = "SELECT id, item_name, amount, category FROM expenses WHERE user_id = ? AND MONTH(transaction_date) = ? ORDER BY id DESC";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, Integer.parseInt(selectedMonth));

            rs = stmt.executeQuery();
            boolean first = true;
            while (rs.next()) {
                if (!first) jsonBuilder.append(",");
                jsonBuilder.append("{")
                           .append("\"id\":").append(rs.getInt("id")).append(",")
                           .append("\"itemName\":\"").append(rs.getString("item_name")).append("\",")
                           .append("\"amount\":").append(rs.getDouble("amount")).append(",")
                           .append("\"category\":\"").append(rs.getString("category")).append("\"")
                           .append("}");
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
        jsonBuilder.append("]");
        out.print(jsonBuilder.toString());
    }
}