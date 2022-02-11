WAES/Philips Test - Assignment Scalable Web
==============

#### Small context

It is a REST API responsible for storing, listing, updating and deleting Products.

#### API Instructions

For more detailed info access the [Swagger](http://localhost:8080/swagger-ui.html) after running the project.


#### Languages / Frameworks / Tools

- Java 11 (Language)
- Swagger Code Gen (Open API - API First)
- Spring Boot (Framework)
- H2 (Database)
- Flyway (Database Migration)
- Swagger (Documentation)
- JUnit (Tests)
- Jacoco (Code Coverage)
- Rest Assured (Integration Tests)
- Resilience4j (Circuit-breaker & Retry)

#### Requirements

- JDK Java 11
- Maven 3.6

# Running the project

#### Compiling and Running the project

 On the project's folder, run the command below to compile and start the application.
 > mvn clean spring-boot:run
 
 The command will also start the H2 in-memory database and create the product_entity schema.
 
#### Swagger UI

 You can check the swagger on the endpoint below.
- [Swagger](http://localhost:8080/swagger-ui.html)

 The contract of the api, is on the file [swagger.yml](/src/main/resources/swagger.yaml)

#### H2 Console

 After starting the application the H2 Console will be available on the endpoint bellow, using the data source url "jdbc:h2:mem:waestestdb"
- [Database console](http://localhost:8080/h2-console/) 

#### Running Tests

To run the unit and integration tests, just run the command below. 
The Jacoco report will be generated and available on the [target folder](target/site/jacoco/index.html)
> mvn test

# Project Info

I have used Spring Boot to build this REST API. 
Following the KISS principle, I have tried to keep the project as simple as possible.
I also tried to follow the clean code principles, which I in my opinion makes it very easy for other developers to understand my code, and also to increase the maintainability of the code.  
We have four main layers in this project:
  * The REST Controller layer - Responsible for receiving the requests and handing the data to the business layer to process it.
  * The Business layer (Services) - The application logic is stored in this layer, which will process the data and respond to the clients. It is also responsible for calling the Persistency layer.
  * The Persistence layer (Repositories)- All the data stored is handled by this layer, making usage of the spring data JPA
  * The Integration layer - Responsible to propagate local changes and additions to 3rd parties API

I also used the Observer Pattern in order to notify whenever there is need to reprocess an event and also whenever we need to propagate data to 3rd party APIs.
With that in place I added schedulers to consume queues and process the events:
  * One to process error events while propagating Products to 3rd party APIs;
  * One for propagate the Events to 3rd party APIs

Last but not least, I have added a Circuit Breaker that will open once 70% of the calls start to fail. After 10 seconds the Circuit Breaker will change to HALF OPEN. Of course the Circuit Breaker, alongside with Retry, cares of a tune but that is not the goal right now, so I left them as they are. 

#### Decisions Made

1. H2 Database - Trying to keep it very simple, I added this in-memory database. If necessary it would be simple to migrate to another database of choice.
2. Flyway - Simple setup and very friendly to use while versioning and migrating databases.
3. Rest Assured - Easy way to implement integration tests.
4. Jacoco - Easy way to verify how much of the code is covered by unit/integration tests.
5. Swagger Code Gen - The API first strategy is being used a lot among the developers, and it avoids having to create tons of code.
6. Comments - I think that a well-written code must not have comments unless it is not that simple to explain. It must be understandable by itself. With that in mind, I did not add too many comments over the code while coding this assignment.
7. Swagger - There is no need for further explanation.
8. Exclusions on coverage - I have excluded the PhilipsCodeChallengeApplication, Auto-Generated Models (Simple POJOs), and all the Constants files from Jacoco coverage, since those classes are not meant to be tested. Anyway, PhilipsCodeChallengeApplication is tested when we set up the Spring Boot context while running the integration tests.


#### Improvement Suggestions

1. Remove the Local Queues and add Cloud based Queue Services (e.g. AWS SQS) so the queues are shared across multiple nodes in a cloud environment.
   * 1.1 The Propagation schedulers would be replaced by SQS consumers wich would consume every message as soon as they arrive in the Queue.
   * 1.2 The Error Reprocess schedulers would be replaced by SQS consumers wich would consume every message with a delay from the time the message arrives in the Queue.
2. Add a file to handle message properties in case of need for internationalization. As it is not needed right now, I left the messages in some constant files.
3. Replace the unit tests by Spock to make the unit tests to make usage of BDD to bring the business closer to the code.