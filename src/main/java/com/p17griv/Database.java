package com.p17griv;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import javax.swing.*;
import java.awt.GridLayout;

/**
 * Establishes connection with a MySQL server, creates a database for
 * the application, creates and fills tables with data from CSV files and 
 * executes various queries.
 * 
 * @author  Pashalis Grivas
 * @version 1.0
 * @since   2020-01-03 
 */
public class Database {

    Connection connection = null; //Database connection

    /**
     * Establishes connection with database using server's credentials
     * that user provided via dialog.
     * 
     * @return <code>true</code> If connection succeed or <code>false</code> If connection failed.
     */
    public boolean establishDatabase() {
	    try {
            JPanel panel = new JPanel(new GridLayout(0, 1));
            JTextField url = new JTextField("jdbc:mysql://localhost:3306/");
            JTextField user = new JTextField();
            JPasswordField pass = new JPasswordField();

            panel.add(new JLabel("URL: "));
            panel.add(url);
            panel.add(new JLabel("user: "));
            panel.add(user);
            panel.add(new JLabel("password: "));
            panel.add(pass);

            int result = JOptionPane.showConfirmDialog(null, panel, "Connect to SQL server",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                Class.forName("com.mysql.jdbc.Driver");
                this.connection = DriverManager.getConnection(url.getText() + "?serverTimezone=UTC", user.getText(), String.valueOf(pass.getPassword())); //Open a connection

                Statement statement = this.connection.createStatement();
                statement.executeUpdate("CREATE DATABASE IF NOT EXISTS weatherinfodb;");
                this.connection = DriverManager.getConnection(url.getText() + "weatherinfodb?serverTimezone=UTC", user.getText(), String.valueOf(pass.getPassword())); // Connect to database
                return true;
            }
            else
                return false;
	    }
	    catch(Exception e) {
	        e.printStackTrace();
	    }
        return false;
	}

    /**
     * Checks the existence of 'MET_STATION' 'TEMPERATURE' 'WIND_INFO' tables
     * in database.
     * 
     * @return <code>true</code>    If tables exist / 
     *         <code>false</code>   If tables are missing.
     */
    public boolean tablesExist() {
        try {
            DatabaseMetaData dbm = this.connection.getMetaData();
            boolean table1, table2, table3;
            table1 = table2 = table3 = false;

            ResultSet tables = dbm.getTables(null, null, "MET_STATION", null);
            if(tables.next())
                table1 = true;

            tables = dbm.getTables(null, null, "TEMPERATURE", null);
            if(tables.next())
                table2 = true;

            tables = dbm.getTables(null, null, "WIND_INFO", null);
            if(tables.next())
                table3 = true;

            if(table1 && table2 && table3)
                return true;
            else
                return false;
        }
        catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Checks if tables 'MET_STATION' 'TEMPERATURE' and 'WIND_INFO' are empty.
     * 
     * @return <code>true</code>    If tables are empty / 
     *         <code>false</code>   If tables contain data.
     */
    public boolean tablesEmpty() {
        try {
            ResultSet table1 = this.connection.createStatement().executeQuery("SELECT CASE WHEN EXISTS(SELECT 1 FROM MET_STATION) THEN 0 ELSE 1 END AS IsEmpty;");
            ResultSet table2 = this.connection.createStatement().executeQuery("SELECT CASE WHEN EXISTS(SELECT 1 FROM TEMPERATURE) THEN 0 ELSE 1 END AS IsEmpty;");
            ResultSet table3 = this.connection.createStatement().executeQuery("SELECT CASE WHEN EXISTS(SELECT 1 FROM WIND_INFO) THEN 0 ELSE 1 END AS IsEmpty;");
            if (table1.next() && table2.next() && table3.next())
                if(table1.getString("IsEmpty").equals("0") && table2.getString("IsEmpty").equals("0") && table3.getString("IsEmpty").equals("0"))
                    return false;
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Creates 'MET_STATION' 'TEMPERATURE' 'WIND_INFO' tables.
     * 
     * @return <code>true</code>    If tables created successfully /
     *         <code>false</code>   If tables creation failed.
     */
    public boolean createTables() {
        try {
			Statement statement = this.connection.createStatement();
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS MET_STATION "
								+ "(ID INT NOT NULL,"
								+ "NAME VARCHAR(50),"
								+ "STATE VARCHAR(50),"
								+ "LATITUDE DOUBLE,"
								+ "LONGITUDE DOUBLE,"
								+ "ALTITUDE DOUBLE,"
								+ "PRIMARY KEY (ID));"); // Create "MET_STATION" table

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS TEMPERATURE "
								+ "(MET_STATION INT NOT NULL,"
								+ "MONTH VARCHAR(30) NOT NULL,"
								+ "YEAR_ YEAR NOT NULL,"
								+ "MAX_TEMP DOUBLE,"
								+ "MIN_TEMP DOUBLE,"
								+ "PRIMARY KEY (MET_STATION,MONTH,YEAR_),"
								+ "FOREIGN KEY (MET_STATION)"
								+ "REFERENCES MET_STATION(ID));"); // Create "TEMPERATURE" table
			
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS WIND_INFO "
								+ "(MET_STATION INT NOT NULL,"
								+ "WIND_DIR CHAR NOT NULL,"
								+ "YEAR_ YEAR NOT NULL,"
								+ "NO_OF_DAYS INT,"
								+ "PRIMARY KEY (MET_STATION,WIND_DIR,YEAR_),"
								+ "FOREIGN KEY (MET_STATION)"
								+ "REFERENCES MET_STATION(ID));"); // Create "WIND_INFO" table
            return true;
		}
        catch(com.mysql.cj.jdbc.exceptions.CommunicationsException e) {
            JOptionPane.showMessageDialog(null, "Unable to establish connection with database server.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
	    catch(Exception e) {
	        e.printStackTrace();
            return false;
	    }
    }

    /**
     * Deletes 'MET_STATION' 'TEMPERATURE' 'WIND_INFO' tables.
     * 
     * @return <code>true</code>    If tables deleted successfully /
     *         <code>false</code>   If tables deletion failed.
     */
    public boolean deleteTables() {
        try {
			Statement statement = this.connection.createStatement();
			statement.executeUpdate("DROP TABLE TEMPERATURE");
			statement.executeUpdate("DROP TABLE WIND_INFO");
			statement.executeUpdate("DROP TABLE MET_STATION");
            return true;
		}
        catch(com.mysql.cj.jdbc.exceptions.CommunicationsException e) {
            JOptionPane.showMessageDialog(null, "Unable to establish connection with database server.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
		catch(Exception e) {
		    e.printStackTrace();
            return false;
		}
    }

    /**
     * Inserts data to 'MET_STATION' 'TEMPERATURE' 'WIND_INFO' tables
     * from CSV files.
     * 
     * @return <code>true</code>    If data inserted successfully /
     *         <code>false</code>   If data insertion failed.
     */
    public boolean insertData() {
        try {
            Statement statement = this.connection.createStatement();
            Scanner scanner = new Scanner(new File(this.getClass().getClassLoader().getResource("MET_STATION.csv").getPath())); // Open CSV file
            int lineCount = 1;
            while (scanner.hasNext()) {
                List<String> line = Arrays.asList(scanner.nextLine().split(",")); // Parse each line of the file
                if(lineCount!=1) // Skip the first line of the CSV file
                    statement.executeUpdate("INSERT INTO MET_STATION VALUES ('"+line.get(0)+"', '"+line.get(1)+"', '"+line.get(2)+"', '"+line.get(3)+"', '"+line.get(4)+"', '"+line.get(5)+"');");
                lineCount++;
            }
            scanner.close();

            scanner = new Scanner(new File(this.getClass().getClassLoader().getResource("TEMPERATURE.csv").toURI())); // Open CSV file
            lineCount = 1;
            while (scanner.hasNext()) {
                List<String> line = Arrays.asList(scanner.nextLine().split(",")); // Parse each line of the file
                if(lineCount!=1) // Skip the first line of the CSV file
                    statement.executeUpdate("INSERT INTO TEMPERATURE VALUES ('"+line.get(0)+"', '"+line.get(1)+"', '"+line.get(2)+"', '"+line.get(3)+"', '"+line.get(4)+"');");
                lineCount++;
            }
            scanner.close();

            scanner = new Scanner(new File(this.getClass().getClassLoader().getResource("WIND_INFO.csv").toURI())); // Open CSV file
            lineCount = 1;
            while (scanner.hasNext()) {
                List<String> line = Arrays.asList(scanner.nextLine().split(",")); // Parse each line of the file
                if(lineCount!=1) // Skip the first line of the CSV file
                    statement.executeUpdate("INSERT INTO WIND_INFO VALUES ('"+line.get(0)+"', '"+line.get(1)+"', '"+line.get(2)+"', '"+line.get(3)+"');");
                lineCount++;
            }
            scanner.close();
            return true;
        }
        catch (java.io.FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Unable to find CSV files.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        catch (java.sql.SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(null, "Tables already contain data.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        catch(com.mysql.cj.jdbc.exceptions.MysqlDataTruncation e) {
            JOptionPane.showMessageDialog(null, "Some files may have more columns than tables columns.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        catch(java.lang.IndexOutOfBoundsException e) {
            JOptionPane.showMessageDialog(null, "Some files may have less columns than tables columns.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Executes a SQL query in order to retrieve all meteorological station names from
     * "MET_STATION" table of the database.
     * 
     * @return A list with all meteorological station names and states found in the database.
     */
    public List<String> getAllMetStations() {
		List<String> result = new ArrayList<>();
		try {
			Statement statement = this.connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT NAME, STATE FROM MET_STATION;");
			while(resultSet.next())
				result.add(resultSet.getString("NAME") + ", " + resultSet.getString("STATE"));
		}
		catch(java.lang.NullPointerException e) {
			JOptionPane.showMessageDialog(null,
            	    "Make sure that tables aren't empty.",
            	    "Error",
            	    JOptionPane.ERROR_MESSAGE);
		}
		catch(Exception e) {
		    e.printStackTrace();
		}
		return result;
	}

    /**
     * Executes SQL queries in order to retrieve all information about a
     * meteorological station from all the tables of the database.
     * 
     * @param metStation    The name of the wanted meteorological station.
     * @return A string with all information found. 
     */
    public String getMetStation(String metStation) {
		StringBuilder result = new StringBuilder();
		try {
			int metStationId = 0;
			Statement statement = this.connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT ID, STATE, LATITUDE, LONGITUDE, ALTITUDE FROM MET_STATION WHERE NAME = '" + metStation + "';");
			while(resultSet.next()) {
				metStationId = resultSet.getInt("ID"); // Get met station's ID
				result.append("Name: " + metStation);
				result.append("\nState: " + resultSet.getString("STATE"));
				result.append("\nLatitude: " + resultSet.getString("LATITUDE"));
				result.append("\nLongitude: " + resultSet.getString("LONGITUDE"));
				result.append("\nAltitude: "+ resultSet.getString("ALTITUDE"));
			}

			resultSet = statement.executeQuery("SELECT MAX_TEMP, MIN_TEMP, MONTH, YEAR_ FROM TEMPERATURE WHERE MET_STATION = '" + metStationId + "';");
			result.append("\n\nTemperatures: ");
			while(resultSet.next())
				result.append("\n\nMaximum: " + resultSet.getInt("MAX_TEMP") + " Minimum: " + resultSet.getInt("MIN_TEMP")+" Month: " + resultSet.getString("MONTH") + " Year: " + resultSet.getInt("YEAR_"));
			
            resultSet = statement.executeQuery("SELECT WIND_DIR, YEAR_, NO_OF_DAYS FROM WIND_INFO WHERE MET_STATION = '" + metStationId + "';");
			result.append("\n\nWinds: ");
			while(resultSet.next())
				result.append("\n\nWind Direction: " + resultSet.getString("WIND_DIR")+" Year: " + resultSet.getInt("YEAR_") + " Duration (days): " + resultSet.getInt("NO_OF_DAYS"));
		}
		catch(Exception e) {
		    e.printStackTrace();
		}
		return result.toString();
	}

    /**
     * Executes SQL queries in order to retrieve all the meteorological 
     * stations that recorded the maximum and minimum temperature in a 
     * specific year.
     * 
     * @param year  The year that user selects.
     * @return A string with the names and states of the stations that 
     * had the minimum and maximum temperatures of the year and the month 
     * those were recorded.
     */
    public String getYearsMaxAndMinTemp(int year) {
		StringBuilder result = new StringBuilder();
		try {
			Statement statement = this.connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT NAME, STATE, MONTH FROM MET_STATION INNER JOIN TEMPERATURE ON MET_STATION.ID = TEMPERATURE.MET_STATION WHERE MONTH IN (SELECT MONTH FROM TEMPERATURE WHERE YEAR_ = '" + year + "' AND MAX_TEMP IN (SELECT MAX(MAX_TEMP) FROM TEMPERATURE WHERE YEAR_ = '" + year + "')) AND ID IN (SELECT MET_STATION FROM TEMPERATURE WHERE YEAR_ = '" + year + "' AND MAX_TEMP IN (SELECT MAX(MAX_TEMP) FROM TEMPERATURE WHERE YEAR_ = '" + year + "'));");
			result.append("The stations that recorded the maximum temperature in " + year + " are: ");
			while(resultSet.next()) {
				result.append("\n\nName: " + resultSet.getString("NAME"));
				result.append("\nState: " + resultSet.getString("STATE"));
				result.append("\nMonth: " + resultSet.getString("MONTH"));
			}
			resultSet = statement.executeQuery("SELECT NAME, STATE, MONTH FROM MET_STATION INNER JOIN TEMPERATURE ON MET_STATION.ID = TEMPERATURE.MET_STATION WHERE MONTH IN (SELECT MONTH FROM TEMPERATURE WHERE YEAR_ = '" + year + "' AND MIN_TEMP IN (SELECT MIN(MIN_TEMP) FROM TEMPERATURE WHERE YEAR_ = '" + year + "')) AND ID IN (SELECT MET_STATION FROM TEMPERATURE WHERE YEAR_ = '" + year + "' AND MIN_TEMP IN (SELECT MIN(MIN_TEMP) FROM TEMPERATURE WHERE YEAR_ = '" + year + "'));");
			result.append("\n\nThe stations that recorded the minimum temperature in " + year + " are: ");
			while(resultSet.next()) {
				result.append("\n\nName: " + resultSet.getString("NAME"));
				result.append("\nState: " + resultSet.getString("STATE"));
				result.append("\nMonth: " + resultSet.getString("MONTH"));
			}
		}
		catch(Exception e) {
		    e.printStackTrace();
		}
		return result.toString();
	}

    /**
     * Executes SQL queries in order to get the averages of maximum and
     * minimum temperatures of a specific meteorological station.
     * 
     * @param metStation    The wanted station selected by the user.
     * @return A string with the averages of maximum and minimum temperatures
     * of the station.
     */
    public String getStationsMaxMinAvgTemp(String metStation) {
		StringBuilder result = new StringBuilder();
		try {
			Statement statement = this.connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT AVG(MAX_TEMP) FROM TEMPERATURE WHERE MET_STATION IN (SELECT ID FROM MET_STATION WHERE NAME = '" + metStation + "');");
			result.append("The average of maximum temperatures of '"+ metStation + "' station is: ");
			while(resultSet.next())
				result.append(String.format("%.2f", Double.parseDouble(resultSet.getString("AVG(MAX_TEMP)"))));
                
			resultSet = statement.executeQuery("SELECT AVG(MIN_TEMP) FROM TEMPERATURE WHERE MET_STATION IN (SELECT ID FROM MET_STATION WHERE NAME = '" + metStation + "');");
			result.append("\n\nThe average of minimum temperatures of '" + metStation + "' station is: ");
			while(resultSet.next())
				result.append(String.format("%.2f", Double.parseDouble(resultSet.getString("AVG(MIN_TEMP)"))));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return result.toString();
	}

    /**
     * Executes SQL queries in order to retrieve all information about
     * the winds of a specific year.
     * 
     * @param year  The wanted year selected by the user.
     * @return A string with all information found about the winds of the year.
     */
    public String getYearsWindInfo(int year) {
		StringBuilder result = new StringBuilder();
		try {
			Statement statement = this.connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT WIND_DIR, NO_OF_DAYS, NAME, STATE FROM WIND_INFO INNER JOIN MET_STATION ON MET_STATION.ID = WIND_INFO.MET_STATION WHERE YEAR_ = '" + year + "';");
			result.append("The winds that recorded in " + year + " are:");
			while(resultSet.next()) {
				result.append("\n\n-  Wind: " + resultSet.getString("WIND_DIR"));
				result.append(" for " + resultSet.getString("NO_OF_DAYS") + " days");
                result.append("\n   Recorded at: " + resultSet.getString("NAME") + ", " + resultSet.getString("STATE"));
			}
		}
		catch(Exception e) {
		    e.printStackTrace();
		}
		return result.toString();
	}
	
    /**
     * Executes SQL queries in order to retrieve the recorded winds that had the 
     * longest-lasting (in days) of each meteorological station. 
     * 
     * @return A sting that contains the direction, duration and year of the 
     * longest-lasting winds of each station.
     */
    public String getLongestWindPerMetStation() {
		StringBuilder result = new StringBuilder();
		result.append("The longest-lasting winds of each meteorological station:");
		try {
			Statement statement = this.connection.createStatement();
			Statement statement2 = this.connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT DISTINCT MET_STATION FROM WIND_INFO");
			while(resultSet.next()) {
				ResultSet resultSet2 = statement2.executeQuery("SELECT MET_STATION, WIND_DIR, NO_OF_DAYS, YEAR_, NAME, STATE FROM WIND_INFO INNER JOIN MET_STATION ON MET_STATION.ID = WIND_INFO.MET_STATION WHERE NO_OF_DAYS IN (SELECT MAX(NO_OF_DAYS) FROM WIND_INFO WHERE MET_STATION = '" + resultSet.getString("MET_STATION") + "');");
				while(resultSet2.next()) {
					result.append("\n\n- Station " + resultSet2.getString("NAME") + ", " + resultSet2.getString("STATE")+" in ");
					result.append(resultSet2.getInt("YEAR_") + " recorded a ");
					result.append(resultSet2.getString("WIND_DIR") + " wind with a duration of ");
					result.append(resultSet2.getString("NO_OF_DAYS") + " days");
				}
			}
		}
		catch(Exception e) {
		    e.printStackTrace();
		}
		return result.toString();
	}
}