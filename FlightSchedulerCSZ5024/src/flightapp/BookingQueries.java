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
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author KC
 */
public class BookingQueries {
    
    private static final Connection connection = DBConnection.getConnection();
    
    //checks if a flight is full on a specified day
    public static boolean flightFull (String flight, String date) {
        
        try {
            final String sqlRequest = "SELECT NUMSEATS FROM FLIGHT WHERE FLIGHTNAME = ?";
            final PreparedStatement stmt = connection.prepareStatement(sqlRequest);
            stmt.setString(1, flight);
            ResultSet rs = stmt.executeQuery();
            
            rs.next();
            int seats = rs.getInt(1);
     
            final String sqlRq = "SELECT * FROM BOOKING WHERE FLIGHT = ? and DAY = ?";
            final PreparedStatement bstmt = connection.prepareStatement(sqlRq);
            bstmt.setString(1,flight);
            bstmt.setString(2,date);
            ResultSet brs = bstmt.executeQuery();
            
            int checkseats = 0;
            while (brs.next()){
                checkseats += 1;
            }
            if (checkseats >= seats) {
                Flight.setSeatSub(checkseats);
                return true;
            }
            else {
                Flight.setSeatSub(checkseats);
                return false;
            }
        }
        catch (SQLException sqlException){
            sqlException.printStackTrace();
            return false;
        }
        
    }
    
    //adds booking to DB
    public static boolean addBooking(BookingEntry book, boolean fill){
        
        String customer = book.getCustomer();
        String flight = book.getFlight();
        String date = book.getDate();
        fill = flightFull(flight, date);
        final String sql;
        try {
            if (fill){
                sql = "INSERT INTO WAITLIST(CUSTOMER, FLIGHT, DAY, TIMESTAMP) VALUES(?,?,?,?)";
            }
            else {
                sql = "INSERT INTO BOOKING(CUSTOMER, FLIGHT, DAY, TIMESTAMP) VALUES(?,?,?,?)";
            }
    
            final PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1,customer);
            stmt.setString(2,flight);
            stmt.setString(3,date);
            stmt.setTimestamp(4,book.getTime());
            stmt.executeUpdate();
    
            System.out.println("\nFlight "+flight+" booked/waitlisted successfully");
            return true;
        }
        catch (SQLException sqlException){
                  
            return false;
        }
    }
    
    //obsolete method, left over from part 1. rewritten as findBooking()
    public static String checkBookings(String flight, String date){
        
        String booking  = "Booked ";
        booking += "Flight: "+flight;
        booking += " on Date: "+date+"\n";
        
        try {
            final String QUERY = "SELECT CUSTOMER FROM BOOKING WHERE FLIGHT = ? and DAY = ?";
            final PreparedStatement customerStatement = connection.prepareStatement(QUERY);
            customerStatement.setString(1,flight);
            customerStatement.setString(2,date);
            ResultSet resultCustomer =customerStatement.executeQuery();
            
            while ( resultCustomer.next()){
                booking += "    " + resultCustomer.getString(1) + "\n";
            }
        
        }
        catch (SQLException se){
            se.printStackTrace();
        }
        return booking;
    }
    
    //unused, returns the number of people waitlisted on a specified date for a specified flight
    public static int checkWaitlistNum(String date, String flight) {
        
        try {
        
            final String sql = "SELECT CUSTOMER, FLIGHT FROM WAITLIST WHERE DAY = ? ORDER BY TIMESTAMP";
            final PreparedStatement stmt = connection.prepareStatement(sql);
        
            stmt.setString(1,date);
            ResultSet rs = stmt.executeQuery();
        
            int count = 0;
            while (rs.next()) {
                if(rs.getString(2).equals(flight)) {
                    count++;
                }
            }
            return count;
        
        }
        catch (SQLException se){
            se.printStackTrace();
        }
        return 0;
    }
    
    //obsolete method, left over from part 1. rewritten as findWaitlist()
    public static String checkWaitlist(String date){
        
        String booking  = "Waitlist ";
        booking += "on Date "+date + ": \n";
        
        try {
        
            final String sql = "SELECT CUSTOMER, FLIGHT FROM WAITLIST WHERE DAY = ? ORDER BY TIMESTAMP";
            final PreparedStatement stmt = connection.prepareStatement(sql);
        
            stmt.setString(1,date);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
            
                booking += "    " + rs.getString(1) + " on flight " + rs.getString(2)+ "\n";
            }
        
        }
        catch (SQLException se){
            se.printStackTrace();
        }
        return booking;
    }
    
    //development for final project
    public static boolean deleteBooking(String name, String date, String flight) {
        
        try {
            
            //final String sql = "SELECT * FROM BOOKING WHERE CUSTOMER = ? and FLIGHT = ? and DAY = ?";
            final String sql = "SELECT * FROM BOOKING";
            //final PreparedStatement stmt = connection.prepareStatement(sql, ResultSet.CONCUR_UPDATABLE, ResultSet.TYPE_SCROLL_INSENSITIVE);
            Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = stmt.executeQuery(sql);
            
            while(rs.next()) {
                if (rs.getString(1).equals(name)) {
                    if (rs.getString(2).equals(flight)) {
                        if (rs.getString(3).equals(date)) {
                            rs.deleteRow();
                            System.out.println("\nBooking Successfully deleted");
                            return true;
                        }
                    }
                }
                
            }
            
            
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
        System.out.println("\nBooking not found, checking waitlist");
        return false;
    }
    
    public static boolean deleteWaitlist(String name, String date, String flight) {
        
        try {
            
            final String sql = "SELECT * FROM WAITLIST";
            Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = stmt.executeQuery(sql);
            
            while(rs.next()) {
                if (rs.getString(1).equals(name)) {
                    if (rs.getString(2).equals(flight)) {
                        if (rs.getString(3).equals(date)) {
                            rs.deleteRow();
                            System.out.println("\nWaitlist entry successfully deleted");
                            return true;
                        }
                    }
                }
            }
            
            
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
        System.out.println("\nError in deleting waitlist entry: not found");
        return false;
    }
    
    //supporting method for dropFlight
    public static ArrayList findBooking(String flight) {
        
        String customer;
        String day;
        String timestamp;
        ArrayList<String> booking = new ArrayList();
        
        try {
            
            final String sql = "SELECT CUSTOMER, DAY, TIMESTAMP FROM BOOKING WHERE FLIGHT = ?";
            final PreparedStatement stmt = connection.prepareStatement(sql);
            
            stmt.setString(1, flight);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                customer = rs.getString(1);
                day = rs.getDate(2).toString();
                timestamp = rs.getTimestamp(3).toString();
                booking.add(customer);
                booking.add(day);
                booking.add(timestamp);
                return booking;
            }
            
        }
        catch(SQLException se) {
            se.printStackTrace();
        }
        return booking;
    }
    
    //supporting method for dropFlight
    public static ArrayList findWaitlist(String flight) {
        
        String customer;
        String day;
        String timestamp;
        ArrayList<String> waitlist = new ArrayList();
        
        try {
            
            final String sql = "SELECT CUSTOMER, DAY, TIMESTAMP FROM WAITLIST WHERE FLIGHT = ?";
            final PreparedStatement stmt = connection.prepareStatement(sql);
            
            stmt.setString(1, flight);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                customer = rs.getString(1);
                day = rs.getDate(2).toString();
                timestamp = rs.getTimestamp(3).toString();
                waitlist.add(customer);
                waitlist.add(day);
                waitlist.add(timestamp);
                return waitlist;
            }
            
        }
        catch(SQLException se) {
            se.printStackTrace();
        }
        return waitlist;
    }
    
    //supporting method for dropFlight
    public static String relocate(String feedback, BookingEntry booking, String flight) {
        
        /*
        Commented out code adds the Customer into the waitlist pool for the flight with the 
        shortest waitlist line on the specified date 
        not required for grading
        */
        
        boolean delete = deleteBooking(booking.getCustomer(), booking.getDate(), flight);
            
            if(delete == true) {
                
                String date = booking.getDate();
                int waitlistnum;
                int least = 0;
                String leastWait = null;
                
                for (String i : Flight.getFLIGHTnames()) {
                    if(i.equals(flight)) {
                        System.out.print("\nSame flight, avoiding infinite loop");
                    }
                    else {
                        boolean isfull = flightFull(i, date);
                        //add booking for flight and date
                        if (isfull == false) {
                            Timestamp timestamp = booking.getTime();
                            BookingEntry book = new BookingEntry(booking.getCustomer(), i, date, timestamp);
                            addBooking(book, false);
                            return feedback += "\nCustomer "+booking.getCustomer()+" successfully rebooked for "+i+" on "+date;
                        }
                        else {
                            /*
                            waitlistnum = checkWaitlistNum(date, i);
                            if (waitlistnum <= least) {
                                least = waitlistnum;
                                leastWait = i;
                            }*/
                        }
                    }
                    
                }
                return feedback += "\nCould not find avaliable seating for "+date+", Customer "+booking.getCustomer()+" booking deleted";
                /*
                Timestamp timestamp = booking.getTime();
                BookingEntry book = new BookingEntry(booking.getCustomer(), leastWait, date, timestamp);
                addBooking(book, true);
                return feedback += "\nCustomer "+booking.getCustomer()+" placed on waitlist for "+leastWait+" on "+date; 
                */
            }
            else {
                return feedback += "\nBookings found for "+flight+" but encountered an unexpected error while relocating these bookings";
            }
    }
    
    public static String dropFlight(String flight) {
        
        /*
        customer bookings are inherently rearranged based upon timestamp, due to the linear nature of my methods
        */
        
        String feedback = "\nAttempting to drop "+flight;
        ArrayList booking = findBooking(flight);
        ArrayList waitlist = findWaitlist(flight);
        Timestamp time = new Timestamp(Calendar.getInstance().getTime().getTime());
        BookingEntry flightbooks;
        BookingEntry flightwaits;
        
        //rearranges bookings
        System.out.println("\nrearranging bookings");
        while (booking.size() > 0) {
            flightbooks = new BookingEntry(booking.get(0).toString(), flight, booking.get(1).toString(), time);
            feedback = relocate(feedback, flightbooks, flight);
            booking = findBooking(flight);
        }
        
        //deletes waitlist entries
        System.out.println("\ndeleting waitlist entries ");
        while (waitlist.size() > 0) {
            flightwaits = new BookingEntry(waitlist.get(0).toString(), flight, waitlist.get(1).toString(), time);
            if (!deleteWaitlist(flightwaits.getCustomer(), flightwaits.getDate(), flightwaits.getFlight())) {
                feedback += "\nan unexpected error occurred while attempting to delete waitlist entry";
            } 
            else {
                feedback += "\nCustomer "+flightwaits.getCustomer()+" successfully deleted from waitlist for flight "+flightwaits.getFlight();
            }
            waitlist = findWaitlist(flight);
        }
        
        //drops the flight from DB
        try {
            
            final String sql = "SELECT FLIGHTNAME FROM FLIGHT";
            Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            
            ResultSet rs = stmt.executeQuery(sql);
            
            int count = 0;
            while(rs.next()) {
                //System.out.println(rs.getString(1)+" "+flight);
                if (rs.getString(1).equals(flight)) {
                    rs.deleteRow();
                    count++;
                }
            }
            
            if (count == 0)
                return feedback += "\n   flight not found";
            else
                System.out.println("\n"+flight+" dropped successfully");
                return feedback += "\nFlight "+flight+" dropped successfully";
        
        }
        catch(SQLException se) {
            se.printStackTrace();
        }
        return feedback += "\n   an error occured while attempting to drop the flight";
    }
    
    public static String Status(String customer) {
        
        String status = "Flights customer is booked for:";
        String waitlist = "Flights customer is waitlisted for:";
        
        try {
            
            final String sql = "SELECT FLIGHT, DAY FROM BOOKING WHERE CUSTOMER = ?";
            final PreparedStatement stmt = connection.prepareStatement(sql);
            
            stmt.setString(1, customer);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                status += "\n   " + rs.getString(1) + " on date " + rs.getString(2);
            }
            status += "\n\n";
            
            final String sqlw = "SELECT FLIGHT, DAY FROM WAITLIST WHERE CUSTOMER = ?";
            final PreparedStatement wstmt = connection.prepareStatement(sqlw);
            
            wstmt.setString(1, customer);
            ResultSet wrs = wstmt.executeQuery();
            
            while (wrs.next()) {
                waitlist += "\n   " + wrs.getString(1) + " on date " + wrs.getString(2);
            }
            waitlist += "\n";
            
        }
        catch(SQLException se) {
            se.printStackTrace();
        }
        return status + waitlist;
    }
    
    //method specific to the cancel booking tab
    public static String cancelBooking(String customer, String date) {
        
        String feedback = "";
        
        try {
            
            final String sql = "SELECT * FROM BOOKING";
            Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = stmt.executeQuery(sql);
            
            while(rs.next()) {
                if (rs.getString(1).equals(customer)) {
                    if (rs.getString(3).equals(date)) {
                        feedback += "\nFlight "+rs.getString(2)+" canceled for customer "+customer+" on date "+date;
                        rs.deleteRow();
                        System.out.println("\nBooking Successfully deleted");
                    }
                }
                
            }
            if (feedback.length() > 0) {
                return feedback;
            }
            
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
        feedback += "Booking not found, checking waitlist";
        System.out.println("\nBooking not found, checking waitlist");
        return feedback;
    }
    
    //method specific to the cancel booking tab
    public static String cancelWaitlist(String customer, String date) {
        
        String feedback = "";
        
        try {
            
            final String sql = "SELECT * FROM WAITLIST";
            Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = stmt.executeQuery(sql);
            
            while(rs.next()) {
                if (rs.getString(1).equals(customer)) {
                    if (rs.getString(3).equals(date)) {
                        feedback += "\nWaitlist for flight "+rs.getString(2)+" canceled for customer "+customer+" on date "+date;
                        rs.deleteRow();
                        System.out.println("\nWaitlist entry Successfully deleted");
                    }
                }
                
            }
            if (feedback.length() > 0) {
                return feedback;
            }
            
            
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
        feedback += "\nNo waitlist entry found for customer "+customer+" on date "+date;
        System.out.println("\nno waitlist entry found for customer "+customer+" on date "+date);
        return feedback;
    }
    
    //goes through all entries in the waitlist, and books entries that are available
    public static String waitToBooking() {
        
        String feedback = "";
        
        try {
            final String sql = "SELECT * FROM WAITLIST ORDER BY TIMESTAMP";
            final Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = stmt.executeQuery(sql);
            
            String customer;
            String date;
            String flight;
            Timestamp time;
            BookingEntry book;
            
            while(rs.next()) {
                customer = rs.getString(1);
                date = rs.getString(3);
                flight = rs.getString(2);
                time = rs.getTimestamp(4);
                
                //for (String i(flight) : Flight.getFLIGHTnames()) {
                    if (!flightFull(flight, date)) {
                        book = new BookingEntry(customer, flight, date, time);
                        addBooking(book, false);
                        feedback += "\nCustomer "+customer+" moved from waitlist to flight "+flight+" on date "+date;
                        deleteWaitlist(customer, date, flight);
                        System.out.println("\nMoved waitlist customer to booking");
                        //break;
                    }
                //}
            }
            if (feedback.length() > 0) {
                return feedback;
            }
            
            
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
        return feedback;
    }
}
