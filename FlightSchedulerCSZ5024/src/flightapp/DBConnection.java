/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flightapp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author KC
 */
public class DBConnection {
    
    private static String URL = "jdbc:derby://localhost:1527/FlightAppcsz5024";
    private static Connection connection;
        
    public static Connection getConnection() {
        try {
            if (connection == null){
                connection = DriverManager.getConnection(URL,"java","java");
                System.out.print("Connection Successful");
            }
              
        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
            System.out.print("Connection Failed");
        }
        return connection;
            
    }
}
