package org.redquark.logwatcher.core.email.impl;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.redquark.logwatcher.core.configs.LogWatcherEmailConfiguration;
import org.redquark.logwatcher.core.email.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;

/**
 * @author Anirudh Sharma
 *
 */
@Component(service = EmailService.class, immediate = true)
@Designate(ocd = LogWatcherEmailConfiguration.class)
public class EmailServiceImpl implements EmailService {

	// Logger
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	// Get the instance of LogWatcherEmailConfiguration
	private LogWatcherEmailConfiguration logWatcherEmailConfiguration;

	// OSGI Service to access the available Gateways for a message of a given type
	@Reference
	private MessageGatewayService messageGatewayService;

	// Method for initialization of objects
	@Activate
	protected void activate(LogWatcherEmailConfiguration logWatcherEmailConfiguration) {

		// Getting the instance of LogWatcherEmailConfiguration
		this.logWatcherEmailConfiguration = logWatcherEmailConfiguration;
	}

	/**
	 * This method will send the content to users via email
	 */
	@Override
	public void sendEmail(String content) {

		// Email IDs to put in 'To'
		String to = logWatcherEmailConfiguration.to();

		// Email IDs to put in 'Cc'
		String cc = logWatcherEmailConfiguration.cc();

		// Email id of the sender
		String from = logWatcherEmailConfiguration.from();

		// Subject of the email
		String subject = logWatcherEmailConfiguration.subject();

		try {

			// Object capable of sending a message to a recipient
			MessageGateway<Email> messageGateway;

			// This class sets thesender's email & name, receiver's email & name, subject,
			// and the sent date.
			Email email = new SimpleEmail();

			// Configuring the To field - Mandatory
			if (to != null && to != "") {
				email.addTo(to);
			} else {
				log.error(
						"Please check the email list. 'To' field should not be empty and should contain valid email ids");
				return;
			}

			// Configuring 'Cc' field - Optional
			if (cc != null && cc != "") {
				email.addCc(cc);
			} else {
				log.error("Please check the email list. 'Cc' field should contain valid email ids");
			}

			// Setting subject of the email
			email.setSubject(subject);

			// Configuring 'From' field - Mandatory
			if (from != null && from != "") {
				email.setFrom(from);
			} else {
				log.error("Please check the 'From' field. It should not be empty");
				return;
			}
			
			// Setting the content in the email
			if(content != null) {
				email.setMsg(content);
			}
			
			// Getting the reference of gateway
			messageGateway = messageGatewayService.getGateway(Email.class);
			
			// Sending email
			messageGateway.send(email);

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

}
