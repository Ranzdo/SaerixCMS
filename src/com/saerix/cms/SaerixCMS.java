package com.saerix.cms;

import groovy.lang.GroovyClassLoader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.saerix.cms.database.DatabaseException;
import com.saerix.cms.database.DatabaseLoader;
import com.saerix.cms.database.mainmodels.UserModel;
import com.saerix.cms.host.HostException;
import com.saerix.cms.util.Util;

public class SaerixCMS {
	
	public static void main(String[] args) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
			return;
		}
		
		startServer();
	}
	
	private static void startServer() {
		try {
			FileReader fr = new FileReader("config.properties");
			Properties properties = getDefaults();
			properties.load(fr);
			FileWriter fw = new FileWriter("config.properties");
			properties.store(fw, "SaerixCMS configuration");
			fr.close();
			fw.close();
			SaerixCMS cms = new SaerixCMS(properties);
			
			try {
				cms.enable();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		catch (FileNotFoundException e) {
			System.out.println("No configuration file found, starting the setup...\n");
			startSetup();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void startSetup() {
		Properties properties = getDefaults();
		System.out.print("Welcome to SaerixCMS installing guide. Follow the instructions to setup the server.\nTo choose the default option enter a blank value.");
		
		askUser(properties, "mysql_hostname", "MySQL Hostname", "The hostname of the mysql server");

		askUser(properties, "mysql_port", "MySQL Port", "The port to the mysql server");
		
		askUser(properties, "mysql_username", "MySQL Username", "The username to the mysql server");
		
		askUser(properties, "mysql_password", "MySQL Password", "The password to the mysql server");
		
		askUser(properties, "mysql_database", "MySQL Database Name", "The database name to the mysql server");
		
		askUser(properties, "mysql_prefix", "MySQL Table Prefix", "An prefix that will be set to all tables names");
		
		System.out.println("Will now try to connect to the database (5 seconds timeout)");
		
		String connectionURL = "jdbc:mysql://"+properties.getProperty("mysql_hostname")+":"+properties.getProperty("mysql_port")+"/"+properties.getProperty("mysql_database");
		Properties connProperties = new java.util.Properties();
        connProperties.put("user", properties.getProperty("mysql_username"));
        connProperties.put("password", properties.getProperty("mysql_password"));
        DriverManager.setLoginTimeout(5);
        Connection connection;
        try {
			connection = DriverManager.getConnection(connectionURL, connProperties);
		} catch (SQLException e) {
			System.out.println("Could not connect to the database with the supplied information, please try again...");
			e.printStackTrace();
			return;
		}
        
        System.out.println("Successfully connected to the database. Will now write database structure (it will not overwrite if a table already exists)");
        
        try {
        	for(String sql : Util.readResource("/install.sql").split(";")) {
        		Statement s = connection.createStatement();
        		s.execute(sql);
        		s.close();
        	}
        }
        catch(IOException e) {
        	//If this happens we got problems...
        	e.printStackTrace();
        	return;
        }
        catch(SQLException e) {
        	System.out.println("An sql error was thrown during the install procedure.");
        	e.printStackTrace();
        	return;
        }
        
        System.out.println("Successfully wrote the database structure. Now we just need a little more information.");
        
		askUser(properties, "default_hostname", "Deafult Hostname", "The hostname that this server will be available on");
		
		askUser(properties, "port", "Port", "The port that this server will be available on");
		
		try {
			FileWriter fw = new FileWriter("config.properties");
			try {
				properties.store(fw, "SaerixCMS configuration");
			}
			finally {
				fw.close();
			}
		}
		catch(IOException e) {
			System.out.println("Could not write an configuration file.");
			e.printStackTrace();
			return;
		}
		
		System.out.println("Install completed, starting up the server...");
		
		startServer();
	}
	
	private static void askUser(Properties properties, String key, String name, String description) {
		String deafults = properties.getProperty(key);
		System.out.print("\n\n"+name+" :"+description+": (default "+deafults+"): ");
		properties.put(key, readValue(deafults));
	}
	
	private static String readValue(String defaults) {
		String value = new Scanner(System.in).nextLine();
		return value.equals("") ? defaults : value;
	}

	public static Properties getDefaults() {
		Properties properties = new Properties();
		properties.put("developer_mode", "false");
		properties.put("mysql_hostname", "127.0.0.1");
		properties.put("mysql_port", "3306");
		properties.put("mysql_username", "root");
		properties.put("mysql_password", "");
		properties.put("mysql_database", "saerixcms");
		properties.put("mysql_prefix", "cms_");
		properties.put("default_hostname", "127.0.0.1");
		properties.put("port", "80");
		return properties;
	}
	
	@SuppressWarnings("unused")
	private SaerixHttpServer server;
	private GroovyClassLoader gClassLoader = new GroovyClassLoader(SaerixCMS.class.getClassLoader());
	private ExecutorService executor = Executors.newCachedThreadPool();
	private Properties properties;
	private DatabaseLoader databaseLoader;
	
	public SaerixCMS(Properties properties) {
		this.properties = properties;
	}
	
	public void enable() throws IOException, DatabaseException, HostException {
		databaseLoader = new DatabaseLoader(this, properties);
		
		UserModel model = ((UserModel)databaseLoader.getMainDatabase().getModel("users"));
		if(model.getUsers().length == 0) {
			System.out.println("No users are registered, please setup an SuperAdmin account.");
			
			System.out.print("Enter Username: ");
			String username = new Scanner(System.in).nextLine();
			if(username.equals("")) {
				System.out.println("Username cannot be blank... Please try again.");
				return;
			}
			
			System.out.print("\nEnter Password: ");
			String password = new Scanner(System.in).nextLine();
			if(username.equals("")) {
				System.out.println("\nPassword cannot be blank... Please try again.");
				return;
			}
			model.register(username, password);
			
			System.out.println("\nSuccessfully added the user to the database.");
		}
		
		System.out.println("Starting up the server...");
		server = new SaerixHttpServer(this, Integer.parseInt(getProperties().get("port").toString()), getProperties().get("default_hostname").toString());
		System.out.println("The Server is started, listining on port "+getProperties().get("port")+" and on the default hostname "+getProperties().get("default_hostname"));
	}
	
	public boolean isInDevMode() {
		return getProperties().get("developer_mode").equals("true");
	}
	
	public GroovyClassLoader getGroovyClassLoader() {
		return gClassLoader;
	}
	
	public ExecutorService executor() {
		return executor;
	}
	
	public Properties getProperties() {
		return properties;
	}
	
	public DatabaseLoader getDatabaseLoader() {
		return databaseLoader;
	}
	
}
