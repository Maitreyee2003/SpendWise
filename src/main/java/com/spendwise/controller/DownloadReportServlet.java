package com.spendwise.controller;

import java.io.IOException;
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
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.spendwise.util.DatabaseConnection;

@WebServlet("/downloadReport")
public class DownloadReportServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
                         throws ServletException, IOException {

        // Step 1: Session check - login hai ya nahi
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.html");
            return;
        }

        int userId = (int) session.getAttribute("userId");
        String userName = (String) session.getAttribute("userName");
        String month = request.getParameter("month");
        if (month == null) month = "January";

        // Step 2: Browser ko batao ki PDF download hogi
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
            "attachment; filename=SpendWise_Report_" + month + ".pdf");

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            // Step 3: PDF Document banao
            Document document = new Document(PageSize.A4);
            PdfWriter writer = PdfWriter.getInstance(
                document, response.getOutputStream());
            document.open();

            // ============ FONTS ============
            // Different fonts for different sections
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 22,
                Font.BOLD, BaseColor.WHITE);
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 13,
                Font.BOLD, new BaseColor(0, 198, 255)); // cyan color
            Font normalFont = new Font(Font.FontFamily.HELVETICA, 10,
                Font.NORMAL, BaseColor.DARK_GRAY);
            Font boldFont = new Font(Font.FontFamily.HELVETICA, 10,
                Font.BOLD, BaseColor.BLACK);
            Font greenFont = new Font(Font.FontFamily.HELVETICA, 11,
                Font.BOLD, new BaseColor(0, 150, 0));
            Font redFont = new Font(Font.FontFamily.HELVETICA, 11,
                Font.BOLD, BaseColor.RED);
            Font whiteFont = new Font(Font.FontFamily.HELVETICA, 10,
                Font.BOLD, BaseColor.WHITE);

            // ============ DARK HEADER BANNER ============
            // Ye ek dark blue box hai top mein
            PdfPTable headerTable = new PdfPTable(1);
            headerTable.setWidthPercentage(100);
            PdfPCell headerCell = new PdfPCell();
            headerCell.setBackgroundColor(new BaseColor(10, 20, 40)); // dark navy
            headerCell.setPadding(20);
            headerCell.setBorder(Rectangle.NO_BORDER);

            // SpendWise title
            Paragraph headerTitle = new Paragraph("💰 SPENDWISE", headerFont);
            headerTitle.setAlignment(Element.ALIGN_CENTER);
            headerCell.addElement(headerTitle);

            // Subtitle
            Font subHeaderFont = new Font(Font.FontFamily.HELVETICA, 11,
                Font.NORMAL, new BaseColor(150, 200, 255));
            Paragraph subTitle = new Paragraph(
                "Personal Financial Statement — " + month + " Report", subHeaderFont);
            subTitle.setAlignment(Element.ALIGN_CENTER);
            headerCell.addElement(subTitle);
            headerTable.addCell(headerCell);
            document.add(headerTable);

            document.add(new Paragraph(" ")); // space

            // ============ ACCOUNT INFO BOX ============
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setSpacingBefore(5f);

            // Left cell - account holder
            PdfPCell leftCell = new PdfPCell();
            leftCell.setBackgroundColor(new BaseColor(240, 248, 255));
            leftCell.setPadding(10);
            leftCell.setBorderColor(new BaseColor(0, 198, 255));
            leftCell.addElement(new Paragraph("Account Holder", normalFont));
            leftCell.addElement(new Paragraph(userName, boldFont));
            infoTable.addCell(leftCell);

            // Right cell - date
            PdfPCell rightCell = new PdfPCell();
            rightCell.setBackgroundColor(new BaseColor(240, 248, 255));
            rightCell.setPadding(10);
            rightCell.setBorderColor(new BaseColor(0, 198, 255));
            rightCell.addElement(new Paragraph("Report Generated", normalFont));
            rightCell.addElement(new Paragraph(
                new java.util.Date().toString(), boldFont));
            infoTable.addCell(rightCell);
            document.add(infoTable);

            document.add(new Paragraph(" "));

            // ============ GET DATA FROM DATABASE ============
            conn = DatabaseConnection.getConnection();

            // Get total income
            double totalIncome = 0;
            String incomeSql = "SELECT SUM(amount) FROM income WHERE user_id = ?";
            stmt = conn.prepareStatement(incomeSql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();
            if (rs.next()) totalIncome = rs.getDouble(1);
            rs.close();

            // Get total expenses for selected month
            double totalExpenses = 0;
            String expSql = "SELECT SUM(amount) FROM expenses WHERE user_id = ? " +
                "AND MONTHNAME(transaction_date) = ?";
            stmt = conn.prepareStatement(expSql);
            stmt.setInt(1, userId);
            stmt.setString(2, month);
            rs = stmt.executeQuery();
            if (rs.next()) totalExpenses = rs.getDouble(1);
            rs.close();

            double savings = totalIncome - totalExpenses;

            // ============ FINANCIAL SUMMARY BOX ============
            Paragraph summaryTitle = new Paragraph(
                "📊 Financial Overview Summary", titleFont);
            summaryTitle.setSpacingBefore(10f);
            document.add(summaryTitle);

            // Summary 3-column table
            PdfPTable summaryTable = new PdfPTable(3);
            summaryTable.setWidthPercentage(100);
            summaryTable.setSpacingBefore(8f);

            // Income cell - blue
            PdfPCell incomeCell = new PdfPCell();
            incomeCell.setBackgroundColor(new BaseColor(0, 100, 200));
            incomeCell.setPadding(12);
            incomeCell.setBorder(Rectangle.NO_BORDER);
            incomeCell.addElement(new Paragraph("MONTHLY INCOME", whiteFont));
            Font bigWhite = new Font(Font.FontFamily.HELVETICA,
                16, Font.BOLD, BaseColor.WHITE);
            incomeCell.addElement(new Paragraph(
                "Rs. " + String.format("%.2f", totalIncome), bigWhite));
            summaryTable.addCell(incomeCell);

            // Expense cell - red
            PdfPCell expCell = new PdfPCell();
            expCell.setBackgroundColor(new BaseColor(200, 50, 50));
            expCell.setPadding(12);
            expCell.setBorder(Rectangle.NO_BORDER);
            expCell.addElement(new Paragraph("TOTAL EXPENSES", whiteFont));
            expCell.addElement(new Paragraph(
                "Rs. " + String.format("%.2f", totalExpenses), bigWhite));
            summaryTable.addCell(expCell);

            // Savings cell - green or red
            PdfPCell savCell = new PdfPCell();
            savCell.setBackgroundColor(savings >= 0 ?
                new BaseColor(0, 150, 80) : new BaseColor(180, 0, 0));
            savCell.setPadding(12);
            savCell.setBorder(Rectangle.NO_BORDER);
            savCell.addElement(new Paragraph("NET SAVINGS", whiteFont));
            savCell.addElement(new Paragraph(
                "Rs. " + String.format("%.2f", savings), bigWhite));
            summaryTable.addCell(savCell);
            document.add(summaryTable);

            document.add(new Paragraph(" "));

            // ============ CATEGORY BREAKDOWN ============
            Paragraph catTitle = new Paragraph(
                "📂 Category Wise Breakdown", titleFont);
            catTitle.setSpacingBefore(10f);
            document.add(catTitle);

            PdfPTable catTable = new PdfPTable(3);
            catTable.setWidthPercentage(100);
            catTable.setSpacingBefore(8f);

            // Category table header
            String[] headers = {"Category", "Amount Spent", "Status"};
            for (String h : headers) {
                PdfPCell hCell = new PdfPCell(new Phrase(h, whiteFont));
                hCell.setBackgroundColor(new BaseColor(10, 20, 40));
                hCell.setPadding(8);
                hCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                catTable.addCell(hCell);
            }

            // Get category wise data
            String catSql = "SELECT category, SUM(amount) as total " +
                "FROM expenses WHERE user_id = ? " +
                "AND MONTHNAME(transaction_date) = ? " +
                "GROUP BY category";
            stmt = conn.prepareStatement(catSql);
            stmt.setInt(1, userId);
            stmt.setString(2, month);
            rs = stmt.executeQuery();

            // Category colors
            BaseColor[] rowColors = {
                new BaseColor(240, 248, 255),
                new BaseColor(255, 255, 255)
            };
            int rowCount = 0;

            while (rs.next()) {
                String category = rs.getString("category");
                double amount = rs.getDouble("total");

                PdfPCell c1 = new PdfPCell(new Phrase(category, boldFont));
                c1.setBackgroundColor(rowColors[rowCount % 2]);
                c1.setPadding(8);
                catTable.addCell(c1);

                PdfPCell c2 = new PdfPCell(new Phrase(
                    "Rs. " + String.format("%.2f", amount), normalFont));
                c2.setBackgroundColor(rowColors[rowCount % 2]);
                c2.setPadding(8);
                c2.setHorizontalAlignment(Element.ALIGN_CENTER);
                catTable.addCell(c2);

                // Status indicator
                String status = amount > 5000 ? "⚠ High" : "✓ Normal";
                Font statusFont = amount > 5000 ? redFont : greenFont;
                PdfPCell c3 = new PdfPCell(new Phrase(status, statusFont));
                c3.setBackgroundColor(rowColors[rowCount % 2]);
                c3.setPadding(8);
                c3.setHorizontalAlignment(Element.ALIGN_CENTER);
                catTable.addCell(c3);
                rowCount++;
            }
            document.add(catTable);

            document.add(new Paragraph(" "));

            // ============ TRANSACTION TABLE ============
            Paragraph transTitle = new Paragraph(
                "📋 Transaction Details", titleFont);
            transTitle.setSpacingBefore(10f);
            document.add(transTitle);

            PdfPTable transTable = new PdfPTable(4);
            transTable.setWidthPercentage(100);
            transTable.setSpacingBefore(8f);
            transTable.setWidths(new float[]{0.5f, 3f, 2f, 2f});

            // Transaction header
            String[] tHeaders = {"#", "Description", "Category", "Amount"};
            for (String h : tHeaders) {
                PdfPCell hCell = new PdfPCell(new Phrase(h, whiteFont));
                hCell.setBackgroundColor(new BaseColor(0, 100, 180));
                hCell.setPadding(8);
                hCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                transTable.addCell(hCell);
            }

            // Get transactions
            String transSql = "SELECT item_name, category, amount " +
                "FROM expenses WHERE user_id = ? " +
                "AND MONTHNAME(transaction_date) = ? " +
                "ORDER BY transaction_date DESC";
            stmt = conn.prepareStatement(transSql);
            stmt.setInt(1, userId);
            stmt.setString(2, month);
            rs = stmt.executeQuery();

            int srNo = 1;
            while (rs.next()) {
                BaseColor rowBg = (srNo % 2 == 0) ?
                    new BaseColor(240, 248, 255) : BaseColor.WHITE;

                PdfPCell n = new PdfPCell(
                    new Phrase(String.valueOf(srNo), normalFont));
                n.setBackgroundColor(rowBg);
                n.setPadding(6);
                n.setHorizontalAlignment(Element.ALIGN_CENTER);
                transTable.addCell(n);

                PdfPCell desc = new PdfPCell(
                    new Phrase(rs.getString("item_name"), normalFont));
                desc.setBackgroundColor(rowBg);
                desc.setPadding(6);
                transTable.addCell(desc);

                PdfPCell cat = new PdfPCell(
                    new Phrase(rs.getString("category"), normalFont));
                cat.setBackgroundColor(rowBg);
                cat.setPadding(6);
                transTable.addCell(cat);

                PdfPCell amt = new PdfPCell(new Phrase(
                    "Rs. " + String.format("%.2f",
                    rs.getDouble("amount")), normalFont));
                amt.setBackgroundColor(rowBg);
                amt.setPadding(6);
                amt.setHorizontalAlignment(Element.ALIGN_RIGHT);
                transTable.addCell(amt);
                srNo++;
            }
            document.add(transTable);

            // ============ FOOTER ============
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));
            Font footerFont = new Font(Font.FontFamily.HELVETICA,
                8, Font.ITALIC, BaseColor.GRAY);
            Paragraph footer = new Paragraph(
                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                "Generated by SpendWise — Personal Finance Manager | " +
                "Confidential Document | © 2026 SpendWise",
                footerFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();

        } catch (Exception e) {
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
    }
}