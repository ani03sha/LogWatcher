package org.redquark.logwatcher.core.configs;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * @author Anirudh Sharma
 * 
 *         This interface defines the configuration to schedule the job for log
 *         watching
 */
@ObjectClassDefinition(name = "Red Quark Log Watcher Configuration", description = "This confguration captures different parameters required for watching the log files")
public @interface LogWatcherConfiguration {

	/**
	 * This parameter returns and array of errors/exceptions a user wish to monitor
	 * 
	 * @return {@link String}
	 */
	@AttributeDefinition(name = "Error/Exception Types", description = "Select the name of the Java exception/error you wish to monitor", type = AttributeType.STRING)
	public String[] type() default { "NullPointerException", "InvalidItemStateException" };

	/**
	 * This parameter returns the path of the log file which you wish to monitor
	 * 
	 * @return {@link String}
	 */
	@AttributeDefinition(name = "File Path", description = "Select the path of file which needs to be watched.", type = AttributeType.STRING)
	public String filePath() default "logs";

	/**
	 * This parameter returns the name of the file to watch
	 * 
	 * @return {@link String}
	 */
	@AttributeDefinition(name = "Log File", description = "Name of the file to watch. Default is error.log", type = AttributeType.STRING)
	public String logFile() default "error.log";

	@AttributeDefinition(name = "Trace Lines", description = "Number of stack trace lines to capture. Default is 10", type = AttributeType.INTEGER)
	public int traceLines() default 10;

	/**
	 * This parameter returns the cron expression to schedule the job
	 * 
	 * @return {@link String}
	 */
	@AttributeDefinition(name = "Cron Expression", description = "Cron expression to schedule the job. Defualt is 0 0 12 ? * MON-FRI")
	public String cronExpression() default "0 0 12 ? * MON-FRI";

}
