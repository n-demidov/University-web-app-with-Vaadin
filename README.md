#University (web-app with Vaadin)

About
-----

A simple web-app with Vaadin framework. Application allows edit and filter students and groups.

Features: UI at Vaadin framework; HSQLDB (in-process mode). Also Java 1.8, JDBC, Hibernate Validator.

`create_tables.sql` file contains scripts for creating tables in HSQLDB.

Build and Run
-------------

1. Run in the command line:
	```
	mvn package
	mvn jetty:run
	```

2. Open `http://localhost:8080` in a web browser.
