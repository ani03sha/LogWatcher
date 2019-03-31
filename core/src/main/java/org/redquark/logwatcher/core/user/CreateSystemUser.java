package org.redquark.logwatcher.core.user;

import static org.redquark.logwatcher.core.constants.LogWatcherConstants.INTERMEDIATE_PATH;
import static org.redquark.logwatcher.core.constants.LogWatcherConstants.SYSTEM_USER_ID;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFactory;

import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.redquark.logwatcher.core.configs.LogWatcherEmailConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Anirudh Sharma
 * 
 * This class deals with the creation of system user
 *
 */
@Component(immediate = true)
public class CreateSystemUser {

	// Logger
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	// API to get and create ResourceResolvers
	@Reference
	private ResourceResolverFactory resourceResolverFactory;

	// Session object
	private Session session;

	// Configuration to get the email id
	private LogWatcherEmailConfiguration logWatcherEmailConfiguration;

	// Method to initialize stuff
	@Activate
	protected void activate(LogWatcherEmailConfiguration logWatcherEmailConfiguration) {

		// Getting the instance of LogWatcherEmailConfiguration
		this.logWatcherEmailConfiguration = logWatcherEmailConfiguration;

		// Creating the system user
		createSystemUser();
	}

	/**
	 * This method creates the system user with the given email
	 */
	private final void createSystemUser() {

		try (ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(null)) {

			// Getting the instance of session
			session = resourceResolver.adaptTo(Session.class);

			// This provides access to and means to maintain authorizable objects i.e. users
			// and groups
			UserManager userManager = resourceResolver.adaptTo(UserManager.class);

			// Getting the user id which will be used to send the email
			String from = logWatcherEmailConfiguration.from();

			// Getting the user name from the email id
			String userName = from.substring(0, from.indexOf('@'));

			// Getting the instance of User
			User user = (User) userManager.getAuthorizable(userName);

			if (user == null) {

				// Creating the system user
				user = userManager.createSystemUser(SYSTEM_USER_ID, INTERMEDIATE_PATH);

				// Getting the instance of ValueFactory from session
				ValueFactory valueFactory = session.getValueFactory();

				// A generic holder for the value of a property. A Value object can be used
				// without knowing the actual property type
				Value email = valueFactory.createValue(from);

				// Setting the email property for the system user
				user.setProperty("email", email);

				// Saving the session
				session.save();
			}

		} catch (LoginException | RepositoryException e) {
			log.error(e.getMessage(), e);
		} finally {
			if (session != null) {
				session.logout();
			}
		}
	}
}
