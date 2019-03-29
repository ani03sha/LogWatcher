package org.redquark.logwatcher.core.schedulers;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.redquark.logwatcher.core.configs.LogWatcherConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Anirudh Sharma
 *
 */
@Component(service = Runnable.class, immediate = true)
@Designate(ocd = LogWatcherConfiguration.class, factory = true)
public class LogWatcherScheduler implements Runnable {

	// Logger
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	// LogWatcherConfiguration instance
	private LogWatcherConfiguration logWatcherConfiguration;

	// List of errors/exceptions
	private List<String> types;

	// Reference for Scheduler API injection
	@Reference
	private Scheduler scheduler;

	/**
	 * This method does the initialization tasks
	 * @param logWatcherConfiguration
	 */
	@Activate
	protected void activate(LogWatcherConfiguration logWatcherConfiguration) {

		log.info("Initializing...");

		// Initializing the configuration instance
		this.logWatcherConfiguration = logWatcherConfiguration;

		// Initializing list
		types = new LinkedList<>();

		// Add the scheduler
		addScheduler();
	}

	// Overridden method for Runnable
	@Override
	public void run() {

		// Call the method to watch logs
		watchLog();
	}

	/**
	 * This method configures the scheduler and adds it.
	 */
	private void addScheduler() {

		try {

			// Provides options to create a scheduler
			ScheduleOptions scheduleOptions = scheduler.EXPR(logWatcherConfiguration.cronExpression());

			// Set concurrent run flag
			scheduleOptions.canRunConcurrently(true);

			// Schedule here
			scheduler.schedule(this, scheduleOptions);

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * This method does the job of watching the logs
	 */
	private void watchLog() {

		try {

			// Getting the error/exception types from the configuration
			types = Arrays.asList(logWatcherConfiguration.type());

			// Getting the path of the log file to monitor
			String filePath = logWatcherConfiguration.filePath();

			// Getting an instance of the WatchService to watch the path
			WatchService watchService = FileSystems.getDefault().newWatchService();

			// Path of the directory
			Path directory = Paths.get(filePath);

			// Register the directory to watch service for different events
			directory.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);

			log.info("WatchService is registered for the directory: {}", directory.getFileName());

			// Infinite loop to poll the files in directory
			while (true) {

				// A token representing the registration of a watchable object with a
				// WatchService.
				WatchKey watchKey;

				try {
					// Retrieves and removes next watch key, waiting if none are yet present
					watchKey = watchService.take();
				} catch (InterruptedException ie) {
					log.error(ie.getMessage(), ie);
					return;
				}

				// Retrieves and removes all pending events for this watch key, returning a List
				// of the events that were retrieved. Note that this method does not wait if
				// there are no events pending.
				for (WatchEvent<?> event : watchKey.pollEvents()) {

					// Getting the kind of the event
					WatchEvent.Kind<?> kind = event.kind();

					// An event or a repeated event for an object that is registered with a
					// WatchService. An event is classified by its kind and has a count to indicate
					// the number of times that the event has been observed. This allows for
					// efficient representation of repeated events. The context method returns any
					// context associated with the event. In the case of a repeated event then the
					// context is the same for all events.
					@SuppressWarnings("unchecked")
					WatchEvent<Path> watchEvent = (WatchEvent<Path>) event;

					// Returns the context for the event. In the case of ENTRY_CREATE, ENTRY_DELETE,
					// and ENTRY_MODIFY events the context is a Path that is the relative path
					// between the directory registered with the watch service, and the entry that
					// is created, deleted, or modified.
					//
					// Here we will be watching the file that is modified/deleted/added
					Path fileName = watchEvent.context();

					// Name of the file to watch for
					String logFile = logWatcherConfiguration.logFile();

					// Check if we are looking for the modified file only and its name should be
					// equal to the name of the file we wish to watch
					if (kind == ENTRY_MODIFY && fileName.toString().equals(logFile)) {
						checkForErrors(types, filePath, logFile);
					}
				}
			}

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * @param errorTypes
	 * @param filePath
	 */
	private void checkForErrors(List<String> errorTypes, String filePath, String logFile) {

		try {

			// Getting the actual file path for Java - by escaping special characters
			String finalPath = filePath.replaceAll("\\", "\\\\") + "\\" + logFile;

			// Getting the input bytes from the file represented by finalPath
			FileInputStream fis = new FileInputStream(finalPath);

			// A data input stream lets an application read primitive Java data types from
			// an
			// underlying input stream in a machine-independent way.
			DataInputStream dis = new DataInputStream(fis);

			// Reads text from a character-input stream, buffering characters so as
			// to provide for the efficient reading of characters, arrays, and lines.
			BufferedReader br = new BufferedReader(new InputStreamReader(dis));

			// Getting the instance of StringBuilder for efficient appending
			StringBuilder sb = new StringBuilder();
			
			// Reads each line via BufferedReader from the file
			String stringLine;
			
			// Flag to determine if we have read desired number of lines
			boolean broken = false;
			
			
			// Counts lines
			int line = 0;
			
			// Loop to go through each type of error/exception passed
			for(String type : types) {
				
				// Loop breaks if we reach to the end of the file
				while((stringLine = br.readLine()) != null) {
					
					// Check if the current line contains the passed error type
					if(stringLine.contains(type)) {
						// Set the flag - we are now going to capture stack trace
						broken = true;
					}
					
					if(broken) {
						
						// Add current line to the StringBuilder object
						sb.append(stringLine + "\n");
						
						// Increment the line count
						line++;
					}
					
					// Checks if captured desired number of lines
					if(line == logWatcherConfiguration.traceLines()) {
						
						// Reset the flag
						broken = false;
						
						// Set the line number to 0 for next instance of error/exception stack trace
						line = 0;
					}
				}
				
				
				// Close BufferedReader
				br.close();
				
				// Close DataInputStream
				dis.close();
				
				// Close FileInputStream
				fis.close();
			}
			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
