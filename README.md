## Spring Boot Application with AWS S3 Integration and Monitoring

This project implements a Spring Boot application that interacts with Amazon S3 for file storage and retrieval. It also includes basic functionalities for logging and application monitoring.

**Features:**

* Connects to a designated AWS S3 bucket.
* Provides API endpoints for:
    * Downloading files from S3 based on filename (GET request).
    * Uploading files to S3 (POST request).
* Placeholder for integrating with Splunk for structured application logging.
* Leverages Spring Boot Actuator for basic application monitoring endpoints.

**Requirements:**

* Java 11+
* Maven

**Setup:**

1. Configure AWS credentials (accessKey, secretKey) and S3 bucket name in `application.yml`.
2. Include necessary libraries (AWS SDK for Java, Spring Boot dependencies) in your pom.xml file.

**Running the application:**

1. Build the project using Maven: `mvn clean package`
2. Run the application: `java -jar <your-application.jar>`

**API Endpoints:**

* GET /api/v1/files/{fileName}: Retrieves a file from S3 based on the provided filename.
* GET /api/v1/files/details: Retrieves all the file details(file name, file size, last modified date) present in provided S3 bucket.
* POST /api/v1/files: Uploads a file to S3.  The file data should be sent as part of the request body.
