package com.spendwise.controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
@WebServlet("/logoutUser")
public class LogoutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Chalu user session ko pakadna
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate(); // Saara login identity data memory se khatam!
        }
        // Logout hone ke baad user ko wapas login.html page par bhej dena
        response.sendRedirect("login.html");
    }
}