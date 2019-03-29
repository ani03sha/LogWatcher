package org.redquark.logwatcher.core.configs;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * @author Anirudh Sharma
 * 
 * This interface defines the configuration to schedule the job for log watching
 */
@ObjectClassDefinition(name = "Red Quark Log Watcher Configuration", description = "This confguration captures different parameters required for watching the log files")
public @interface LogWatcherConfiguration {

	/**
	 * This parameter returns and array of errors/exceptions a user wish to monitor
	 * 
	 * @return {@link String}
	 */
	@AttributeDefinition(name = "Error/Exception Types", description = "Select the name of the Java exception/error you wish to monitor", type = AttributeType.STRING)
	public String[] type();

	/**
	 * This parameter returns the path of the log file which you wish to monitor
	 * 
	 * @return {@link String}
	 */
	@AttributeDefinition(name = "File Path", description = "Select the paths of file which needs to be watched", type = AttributeType.STRING)
	public String filePath();

	/**
	 * This parameter returns the cron expression to schedule the job
	 * 
	 * @return {@link String}
	 */
	@AttributeDefinition(name = "Cron Expression", description = "Cron expression to schedule the job")
	public String cronExpression();

}
