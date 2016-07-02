package com.demidov.university.model.persistence.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Return Connection to DB
 */
public class DBConnection {

	private static final String JDBC_STRING = "jdbc:hsqldb:file:";
	private static final String DB_PATH = "db/universitydb/universitydb";
	private static final String JDBC_CONNECTION_STRING = JDBC_STRING + DB_PATH;
	private static final String USERNAME = "SA";
	private static final String PASSWORD = "";
	
	private static final String SHUTDOWN = "SHUTDOWN";
	private static final String WRITE_DELAY = "SET WRITE_DELAY FALSE";
    
    private Connection connection;
    
    private static final Logger logger = Logger.getLogger(DBConnection.class.getName());
    private static DBConnection instance;

	public static synchronized DBConnection getInstance() {
		if (instance == null)
			instance = new DBConnection();
		return instance;
	}
	
    private DBConnection() {
		super();
		loadDriverClass();
	}
    
    /**
     * Return connection to HSQLDB
     * @return
     * @throws SQLException
     */
    public Connection getConnection() throws SQLException
    {
    	try {
			connection = DriverManager.getConnection(JDBC_CONNECTION_STRING, USERNAME, PASSWORD);
			setDBTimeDelay();
		} catch (SQLException e) {
			logger.log(Level.SEVERE, null, e);
		}
        
        return connection;
    }
    
	/**
     * Close connection to HSQLDB
     * @throws SQLException
     */
    public void closeConnection() throws SQLException
    {
    	final Statement statement = connection.createStatement();
		
		statement.execute(SHUTDOWN);
		statement.close();
		connection.close();
    }
    
    // Load driver class
    private void loadDriverClass() {
    	try {
			Class.forName("org.hsqldb.jdbcDriver");
		} catch (ClassNotFoundException e) {
			logger.log(Level.SEVERE, null, e);
		}
    }
    
    // Set HSQLDB 'WRITE DELAY' special property 
    private void setDBTimeDelay() throws SQLException {
    	final Statement statement = connection.createStatement();
		
		statement.execute(WRITE_DELAY);
		statement.close();
	}
	
}
