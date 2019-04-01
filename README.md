# Log Watcher

This project can be used to watch different types of logs in an AEM server while it is running. Sometimes we want to get notified if a certain type of message appears in the logs, such as NullPointerException, SegmentNotFoundException etc. 

It is better to get notified automatically when such message appears rather than waiting until the last moment. This project does just that. 

## How does it work?

Log Watcher contains a [Sling Scheduler](https://sling.apache.org/documentation/bundles/scheduler-service-commons-scheduler.html) which can be configured as per your needs and it will poll the specified log file for the specified message (to watch out for) at the specified time.

You can configure to capture when the specified message comes in the log and notify the desired group of people by sending them an email. Internally, it uses [Day CQ Mail Service](https://helpx.adobe.com/in/experience-manager/6-4/sites/administering/using/notification.html) implementation of AEM.

## How to configure

Configuration of this service can be done in following steps - 

* Clone and build the project by running the command: mvn clean install
* Install the bundle in you AEM server at: http://localhost:4502/system/console/bundles
* Navigate to configMgr at: http://localhost:4502/system/console/configMgr
* Search for Red Quark Log Watcher Configuration and configure it
* Search for Red Quark Log Watcher Email Configuration and configure it
* Configure Day CQ Mail Service and save
* You should be able to get email notification at the configured time 

## Issues

If you face any issues or problems, you are welcome to open issues. You can do this by following steps - 

* Go to the Issues tab in the repository
* Click on New issue button
* Give appropriate title to the issue
* Add detailed description of the issue and if possible, steps to reproduce
* Click on Open issue button

## How to contribute

Contributions are more than welcome in this project. Below are the steps, you can follow to contribute - 

* Switch to the 'develop' branch of the repository
* Clone the develop branch in your local system
* Make your changes
* Open a pull request against the 'develop' branch only.