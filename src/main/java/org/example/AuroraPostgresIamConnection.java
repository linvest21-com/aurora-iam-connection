package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rds.RdsClient;
import software.amazon.awssdk.services.rds.RdsUtilities;

public class AuroraPostgresIamConnection {
	
	private static final String AURORA_DB_HOST = "tempdb.cluster-cu4jou8grpwg.us-east-1.rds.amazonaws.com"; // e.g., mydb.cluster-xxxxxxxx.us-east-1.rds.amazonaws.com
	private static final int AURORA_DB_PORT = 5432;
	private static final String AURORA_DB_NAME = "linvest21";
	private static final String DB_USER = "eagle_dev_admin";
	private static final Region AWS_REGION = Region.US_EAST_1; // Change to your region
	
	public static void main(String[] args) {
		Connection connection = null;
		try {
//			String jdbcUrl = String.format("jdbc:aws-wrapper:postgresql://%s:%d/%s", AURORA_DB_HOST, AURORA_DB_PORT, AURORA_DB_NAME);
			String jdbcUrl = String.format("jdbc:aws-wrapper:postgresql://localhost:5433/%s",  AURORA_DB_NAME);
			System.out.println("jdbcurl: " + jdbcUrl);
			// Generate an IAM authentication token using AWS SDK
			RdsClient rdsClient = RdsClient.builder()
					                      .region(AWS_REGION)
					                      .credentialsProvider(DefaultCredentialsProvider.create())
					                      .build();
			
			RdsUtilities rdsUtilities = rdsClient.utilities();
			String authToken = rdsUtilities.generateAuthenticationToken(builder -> builder
					                                                                       .hostname(AURORA_DB_HOST)
					                                                                       .port(AURORA_DB_PORT)
					                                                                       .username(DB_USER));
			System.out.println("authtoken: " + authToken);
			// Set up the connection properties
			Properties props = new Properties();
			props.setProperty("user", DB_USER);
			props.setProperty("password", authToken);
			props.setProperty("ssl", "true");
			props.setProperty("sslmode", "require");
			props.setProperty("sslrootcert", "./us-east-1-bundle.pem");
			
			// Connect to the database
			connection = DriverManager.getConnection(jdbcUrl, props);

			System.out.println("Successfully connected to the database!");


            // Create a Statement object
            Statement stmt = connection.createStatement();
            
            // Query to get columns from the 'my_schema' schema and 'students' table
            String query = "SELECT column_name, data_type, is_nullable, column_default " +
                           "FROM information_schema.columns " +
                           "WHERE table_schema = 'my_schema' AND table_name = 'students'";
            
            // Execute the query
            ResultSet rs = stmt.executeQuery(query);
            
            // Print out column details
            System.out.println("Columns in the 'students' table:");
            while (rs.next()) {
                String columnName = rs.getString("column_name");
                String dataType = rs.getString("data_type");
                String isNullable = rs.getString("is_nullable");
                String columnDefault = rs.getString("column_default");
                
                System.out.println("Column Name: " + columnName);
                System.out.println("Data Type: " + dataType);
                System.out.println("Is Nullable: " + isNullable);
                System.out.println("Default Value: " + columnDefault);
                System.out.println("-----------");
            }			

			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
