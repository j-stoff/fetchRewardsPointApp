## Fetch Rewards Web Service Points Application

### The application itself
This application is written in Java and uses a Jersey dependency as a Rest Framework which is a thin layer over the JAX-RS Java implementation. The idea was to write as much as I could without relying too much on the framework and other dependencies to do the heavy lifting. In most other settings I would leverage the use of Frameworks as they suited my needs as most do offer substantial benefits in development time by abstracting away the need to define simple or common tasks (getters and setters for example).

### Application Structure
There are 3 layers to this project:
- A Service layer to accept incoming REST requests, check arguments, and format outgoing responses
- A Logic layer to handle all business logic being applied within the application
- A Data layer to handle the search and retrieval of data sources for use within the application

Each of these layers should have a good abstraction from each other in case future refactoring was to take place and the code were to change internally. If this were a larger project I would have utilized the use of Java Interfaces between these layers to ensure that changes to a layer did not result in a re-work on both layers.

An example of this application flow for a User Account request would be:
1. Accept the request and determine that the necessary information is present (user account name).
2. Utilize the right Logic layer to pull back the user information. The reason the Data layer isn't used directly is there may be some additional data or formatting to be done before returning to the caller.
3. Ensure the look up was successful and an account was found. If not, inform the user that their search did not work.
4. Return the user information to Service layer to be returned to the caller.

The other pieces of the application would be considered the 'Model' in an MVC application or 'beans' in Java terms. Their main purpose is to represent the data and interaction with that data. Most of these are very simple with getters and setters; however, the exception is the UserAccount as it has to manage who applied a payment to the account. I would have preferred to abstract this to the Data layer in the case of a database; however, it is much simpler to have it within the class storing the data.

I did not make new packages for the various layers as most of these classes would be in their own package. If this application needs to scale that would be one of the first places I would start for refactoring.

### Building the Application
The application uses Maven for dependency management to pull a few dependencies (Jersey, Apache commons, Gson). The maven goals **clean** and  **package** will create a fresh WAR file that can be uploaded to a server.

The Jersey version is 2.32, so it does not use the latest jakarta packages but rather the old javax ones. The reason I chose this was it ran with version 9 of Tomcat. Since Tomcat is very easy (and free) to set up, I molded the versioning of the dependencies to fit the platform. It is not necessary to use Tomcat; however, I do not know about the availability of other Java Servers. The base POM builds a WAR which will deploy to most server types (JBoss, TomEE, etc.), so keep that in mind when running locally.

#### Limitations
- **Memory storage for data instead of database/external source.** The reason for this was simplicity in the code; however, it does make for some odd data structure manipulation.
- **Lack of Unit Testing.** I did not add unit tests to this application since it would have only prolonged the development on the application; however, I do think it is a necessary practice for almost all Production applications. Instead, I relied on Postman tests to run Integration tests on the system to check the functionality.
- **Exceptions thrown to caller.** I am throwing the exceptions that happen in the application to the caller since this is a dummy app without sensitive data. If this were something with private data then abstracting these error messages into something meaningful yet not obvious to an outside recipient would be needed.
- **Form parameters used instead of JSON payloads.** This was done mostly for simplicity. I assume it is easier to test with Form parameters rather JSON payloads for most Web Service Testing tools; however, I know JSON can be mapped as the input data to a Web Service as well.
- **Older framework.** I used the Jersey spec from 2017 as that is what I am most familiar with though it is an older technology. I did not use Spring Boot or any other frameworks to align with just a pure Java application and not rely on the magic that certain frameworks provide.

### Examples

Echo Example with Tomcat running on localhost.
![Postman Echo Example with Tomcat Server](/images/Echo_test_with_postman.png)

Check User Account status.
![Postman Check Payments for User](/images/CheckUserAccount_with_postman.PNG)


### Other
- Be sure to add a path for the log4j2.xml in order to get logging support. I typically just put this in my /target folder of a Maven project so I know it is local to the workspace.
- I would test the /echo service to ensure the Server and Application start up was correct before attempting to use the services.
