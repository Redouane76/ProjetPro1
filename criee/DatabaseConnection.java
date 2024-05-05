package criee;

import java.sql.*;

public class DatabaseConnection {
	
	 private Connection connection;

	    public DatabaseConnection(String jdbcURL, String username, String password, String dbName) throws SQLException {
	        this.connection = DriverManager.getConnection(jdbcURL, username, password);
	        String useDatabaseQuery = "USE " + dbName;
	        PreparedStatement useDatabaseStatement = connection.prepareStatement(useDatabaseQuery);
	        useDatabaseStatement.executeUpdate();
	        useDatabaseStatement.close();
	    }

	    public Connection getConnection() {
	        return connection;
	    }

}
