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

@WebServlet("/deleteExpense")
public class DeleteExpenseServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            out.print("{\"status\": \"error\", \"message\": \"Unauthorized request.\"}");
            return;
        }
        int userId = (int) session.getAttribute("userId");
        String expenseId = request.getParameter("id");

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "DELETE FROM expenses WHERE id = ? AND user_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, Integer.parseInt(expenseId));
            stmt.setInt(2, userId);

            int rowsDeleted = stmt.executeUpdate();

            if (rowsDeleted > 0) {
                out.print("{\"status\": \"success\", \"message\": \"Deleted successfully!\"}");
            } else {
                out.print("{\"status\": \"error\", \"message\": \"Record not found.\"}");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            out.print("{\"status\": \"error\", \"message\": \"" + e.getMessage() + "\"}");
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