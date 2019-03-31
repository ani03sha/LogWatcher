package org.redquark.logwatcher.core.email;

/**
 * @author Anirudh Sharma
 * 
 * This interface exposes methods for sending emails
 *
 */
public interface EmailService {

	/**
	 * Sends email to the designated user
	 * @param content
	 */
	public void sendEmail(String content);
}
