package de.uni_potsdam.hpi.bpt.resource_management.ektorp;

import java.net.MalformedURLException;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.http.StdHttpClient.Builder;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;

/**
 * Provides a static method to connect to CouchDB.
 * 
 * public static CouchDbConnector connect(String table)
 *
 * @author tw
 *
 */
public class BPTDatabase {
	
	private static String host;
	private static int port;
	private static String username;
	private static String password;

	/**
     * Connects to CouchDB.
     * 
     * @param table the name of the database to connect to
     * @return the connection to the database
     * 
     */
	public static CouchDbConnector connect(String table) {
		
		setProperties();
		
		HttpClient httpClient = new StdHttpClient.Builder()
									.host(host)
									.port(port)
//									.username(username)
//									.password(password)
									.build();
		
		CouchDbInstance databaseInstance = new StdCouchDbInstance(httpClient);
		CouchDbConnector database = new StdCouchDbConnector(table, databaseInstance);
		
		database.createDatabaseIfNotExists();
		
		return database;
		
	}

	private static void setProperties() {

		ResourceBundle resourceBundle = ResourceBundle.getBundle("de.uni_potsdam.hpi.bpt.resource_management.bptrm");
		try {
			host = resourceBundle.getString("DB_HOST");
		} catch (MissingResourceException e) {
			host = "localhost";
		}
		try {
			port = Integer.parseInt(resourceBundle.getString("DB_PORT"));
		} catch (MissingResourceException e) {
			port = 5984;
		}
		try {
			username = resourceBundle.getString("DB_USERNAME");
		} catch (MissingResourceException e) {
			username = "";
		}
		try {
			password = resourceBundle.getString("DB_PASSWORD");
		} catch (MissingResourceException e) {
			password = "";
		}
		
	}

	public static String getHost() {
		return host;
	}

	public static void setHost(String host) {
		BPTDatabase.host = host;
	}

	public static int getPort() {
		return port;
	}

	public static void setPort(int port) {
		BPTDatabase.port = port;
	}
	
}