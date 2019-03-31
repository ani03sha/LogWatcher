package org.redquark.logwatcher.core.configs;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * @author Anirudh Sharma
 * 
 * This configuration helps to configure the recipients and sender of email
 */
@ObjectClassDefinition(name = "Red Quark Log Watcher Email Configuration", description = "Configuration for senders and recipients of email")
public @interface LogWatcherEmailConfiguration {

	/**
	 * This method will take the comma separated list of email to be put in 'To'
	 * 
	 * @return {@link String}
	 */
	@AttributeDefinition(name = "To", description = "Comma separated list of emails to be put in to", type = AttributeType.STRING)
	public String to();
	
	/**
	 * This method will take the comma separated list of email to be put in 'Cc'
	 * 
	 * @return {@link String}
	 */
	@AttributeDefinition(name = "Cc", description = "Comma separated list of emails to be put in cc", type = AttributeType.STRING)
	public String cc();
	
	/**
	 * This method will take the email id of sender
	 * 
	 * @return {@link String}
	 */
	@AttributeDefinition(name = "From", description = "Email id of sender", type = AttributeType.STRING)
	public String from();
	
	/**
	 * This method will take the subject of the email
	 * @return {@link String}
	 */
	@AttributeDefinition(name = "Subject", description = "Subject of the email", type = AttributeType.STRING)
	public String subject() default "Following logs were captured";
}
