/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flightapp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 *
 * @author KC
 */
public class Customer {
    
    private static Connection connection = DBConnection.getConnection();
  

    public static boolean addCustomer(String name) {
        
        //sql statements inspired by https://www.tutorialspoint.com/jdbc/jdbc-insert-records.htm
        
        try {
            final String sqlInsert = "INSERT INTO customer VALUES(?)";
            final PreparedStatement stmt = connection.prepareStatement(sqlInsert);
            stmt.setString(1, name);
            stmt.executeUpdate();
            return true;
        }
        catch(SQLIntegrityConstraintViolationException e) {
            System.out.println("\nDuplicate name entered, please enter another\n");
            return false;
        }
        catch (SQLException se) {
            se.printStackTrace();
            return false;
        }
    }
  
    public static ArrayList<String> getCustomer() {
    
        try {
            String sqlRequest = "SELECT name FROM customer";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sqlRequest);
         
            ArrayList<String> customers = new ArrayList<>();
            while(rs.next()){
                customers.add(rs.getString(1));
            }
            return customers;
        }
          
        catch (SQLException sqlException){
            sqlException.printStackTrace();
            return new ArrayList<>();
        }
     
    }
}
