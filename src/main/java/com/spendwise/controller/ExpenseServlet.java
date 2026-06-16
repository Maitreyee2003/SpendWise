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

@WebServlet("/addExpense")
public class ExpenseServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String name = request.getParameter("itemName");
        String priceString = request.getParameter("itemPrice");
        String category = request.getParameter("itemCategory");
        String selectedMonth = request.getParameter("monthMonth"); // Frontend selector mapping number

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            out.print("{\"status\": \"error\", \"message\": \"Session expired. Please login again.\"}");
            return;
        }
        int userId = (int) session.getAttribute("userId");

        if (name == null || priceString == null || category == null || name.trim().isEmpty() || priceString.trim().isEmpty()) {
            out.print("{\"status\": \"error\", \"message\": \"Fields cannot be empty!\"}");
            return;
        }

        double price = Double.parseDouble(priceString);
        
        // 🗓️ Sahi month integer nikalna dynamic mapping ke liye
        int monthInt;
        try {
            monthInt = Integer.parseInt(selectedMonth);
        } catch (Exception e) {
            monthInt = Calendar.getInstance().get(Calendar.MONTH) + 1;
        }
        
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        // Pure ISO Standard database date string design structure setup (e.g. 2026-01-01 12:00:00)
        String customDate = currentYear + "-" + String.format("%02d", monthInt) + "-01 12:00:00";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "INSERT INTO expenses (user_id, item_name, amount, category, transaction_date) VALUES (?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, userId);
            stmt.setString(2, name);
            stmt.setDouble(3, price);
            stmt.setString(4, category);
            stmt.setString(5, customDate);

            int rowsInserted = stmt.executeUpdate();

            if (rowsInserted > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                int newId = 0;
                if (generatedKeys.next()) {
                    newId = generatedKeys.getInt(1);
                }
                out.print("{\"status\": \"success\", \"id\": " + newId + ", \"message\": \"Expense added successfully!\"}");
            } else {
                out.print("{\"status\": \"error\", \"message\": \"Failed to insert data.\"}");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            out.print("{\"status\": \"error\", \"message\": \"Database error: " + e.getMessage() + "\"}");
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