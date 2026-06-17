// LINE 1: Bata raha hai ki yeh file 'com.spendwise.util' package ke andar surakshit rakhi hai.
package com.spendwise.util;

// LINE 2: Java ki built-in SQL libraries ko import karna taaki hum database ke functions use kar sakein.
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

	// Clever Cloud ka database URL
	private static final String URL = "jdbc:mysql://bwuvbt2g59kf8qo6b5oe-mysql.services.clever-cloud.com:3306/bwuvbt2g59kf8qo6b5oe?useSSL=false&allowPublicKeyRetrieval=true";

	// Clever Cloud ka username
	private static final String USERNAME = "uhk5oytswnoxtzb";

	// Clever Cloud ka password
	private static final String PASSWORD = "calVSuygKFIdV8VlcXMF";

    // LINE 6: Ek common method (function) jo pure project mein kahin bhi database connection provide karega.
    public static Connection getConnection() {
        Connection connection = null;
        
        try {
            // LINE 7: MySQL ke Driver software ko load karna jo Java aur MySQL ke beech ka translator hai.
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // LINE 8: DriverManager se bolna ki is URL, Username, aur Password ke saath database ka taar jod de.
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            
            // LINE 9: Agar connection successfully jud gaya, toh Eclipse ke Console par yeh success message dikhega.
            System.out.println("SpendWise Backend: Database se taar successfully jud gaya hai!");
            
        } catch (ClassNotFoundException e) {
            // LINE 10: Agar project mein MySQL Connector ki JAR file missing hogi, toh yeh error chalega.
            System.out.println("Error: Driver class nahi mili! Jar file check karo.");
            e.printStackTrace();
        } catch (SQLException e) {
            // LINE 11: Agar database band hai ya password galat hai, toh yeh error chalega.
            System.out.println("Error: Connection fail! Password ya MySQL server check karo.");
            e.printStackTrace();
        }
        
        // LINE 12: Juda hua connection object wapas bhej dena taaki aage hum data save kar sakein.
        return connection;
    }
}