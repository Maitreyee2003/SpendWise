package com.spendwise.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = 
        "jdbc:mysql://localhost:3306/spendwise_db";
    
    private static final String USERNAME = "root";
    
    private static final String PASSWORD = "Maitreyee@123";

    public static Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                URL, USERNAME, PASSWORD);
            System.out.println("SpendWise Backend: Database se taar successfully jud gaya hai!");
        } catch (ClassNotFoundException e) {
            System.out.println("Error: Driver class nahi mili!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Error: Connection fail! Password ya MySQL server check karo.");
            e.printStackTrace();
        }
        return connection;
    }
}