package de.uni_potsdam.hpi.bpt.resource_management;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.UrlValidator;

/**
 * Validates URLs and email addresses.
 * 
 * @author tw
 *
 */
public class BPTValidator {
	
	private static final String[] schemes = new String[] {"http","https","ftp"};
	private static UrlValidator urlValidator = new UrlValidator(schemes);
	private static EmailValidator emailValidator = EmailValidator.getInstance();
	
	public static boolean isOutOfDate(Date lastUpdate) {
		int differenceInDays = (int) (((lastUpdate.getTime() - (new Date()).getTime())) / (1000 * 60 * 60 * 24));
		return differenceInDays > 730;
	}
	
	/**
	 * Checks if an URL is valid, i.e. if it is well-formed and if it points to available resources.
	 * 
	 * @param url URL to be checked
	 * @return true if the given URL is valid
	 */
	public static boolean isValidUrl(String url) {
		return urlValidator.isValid(url) && pageIsAvailable(url);
	}
	
	/**
	 * Checks if an email address is well-formed.
	 * 
	 * @param email mail address to be checked
	 * @return true if the mail address is well-formed
	 */
	public static boolean isValidEmail(String email) {
		return emailValidator.isValid(email);
	}
	
	/**
	 * Checks if an URL points to available resources.
	 * Sends HTTP GET requests and checks the codes of response.
	 * 
	 * @param url URL to be checked as String
	 * @return true if the given URL points to available resources, i.e. the HTTP response code is 2xx oder 3xx
	 */
	private static boolean pageIsAvailable(String url) {
		boolean isAvailable = false;
		HttpURLConnection httpConnection = null;
		try {
			httpConnection = (HttpURLConnection) new URL(url).openConnection();
			httpConnection.setRequestProperty("User-Agent", ""); 
			httpConnection.connect();
			String responseCode = (new Integer(httpConnection.getResponseCode())).toString();
			System.out.println(new Date() + " - " + url + " - Code: " + responseCode);
			if (!responseCode.startsWith("2") && !responseCode.startsWith("3")) {
				isAvailable = false;
			} else {
				isAvailable = true;
			}
		} catch (Exception e) {
			System.out.println(new Date() + " - " + url + " - Code: UNKNOWN");
			return false;
		} finally {
			if (httpConnection != null) {
				httpConnection.disconnect();
			}
		}
		return isAvailable;
	}
}
