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
- Docker (Run Redis and SQS)
- Redis (Distributed Cache)
- SQS (Distributed Queue to handle Events)

#### Requirements

- JDK Java 11
- Maven 3.6
- Docker

# Running the project

#### Compiling and Running the project

To run the project, all you need to do is run the script [run.sh](/run.sh). This will instantiate the **redis** and **
sqs** instances and also create the needed queues so the service can perform their operations normally. Lastly it will
run the application.
> ./run.sh

Running the application will also start the H2 in-memory database and create the product_entity schema.

#### Swagger UI

You can check the swagger on the endpoint below.

- [Swagger](http://localhost:8080/swagger-ui.html)

The contract of the api, is on the file [swagger.yml](/src/main/resources/swagger.yaml)

#### H2 Console

After starting the application the H2 Console will be available on the endpoint bellow, using the data source url **"
jdbc:
h2:mem:waestestdb"**, user: **"sa"** with no password configured.

- [Database console](http://localhost:8080/h2-console/)

#### Running Tests

To run the unit and integration tests, just run the command below. The Jacoco report will be generated and available on
the [target folder](target/site/jacoco/index.html)
> mvn test

# Project Info

I have used Spring Boot to build this REST API. I also tried to follow the clean code principles, which I in my opinion
makes it very easy for other developers to understand my code, and also to increase the maintainability of the code.  
We have four main layers in this project:

* The REST Controller layer - Responsible for receiving the requests and handing the data to the business layer to
  process it.
* The Business layer (Services) - The application logic is stored in this layer, which will process the data and respond
  to the clients. It is also responsible for calling the Persistency layer.
* The Persistence layer (Repositories)- All the data stored is handled by this layer, making usage of the spring data
  JPA
* The Integration layer - Responsible to propagate local data to 3rd parties API

I also used the Observer Pattern in order to notify whenever there is need to reprocess an event and also whenever we
need to propagate data to 3rd party APIs. With that in place I added SQS queues to publish and consume the events:

* One for propagate the Events to 3rd party APIs
* One to process error events while propagating Products to 3rd party APIs;

Last but not least, I have added a Circuit Breaker that will open once 70% of the calls start to fail. After 10 seconds
the Circuit Breaker will change to HALF OPEN. The application also contains Retry for GET requests. Of course the
Circuit Breaker, alongside with Retry, cares of a tune but that is not the goal right now, so I left them as they are.

#### Decisions Made

1. H2 Database - Trying to keep it very simple, I added this in-memory database. If necessary it would be simple to
   migrate to another database of choice.
2. Flyway - Simple setup and very friendly to use while versioning and migrating databases.
3. Redis - Used for distributed caching and avoid requests on the database while there was no data update.
4. AWS SQS - Used to process Error and Propagation data Events.
5. Docker - Used to set up the necessary environment to run the application.
6. Rest Assured - Easy way to implement integration tests.
7. Jacoco - Easy way to verify how much of the code is covered by unit tests.
8. Swagger Code Gen - The API first strategy is being used a lot among the developers, and it avoids having to create
   tons of code.
9. Comments - I think that a well-written code must not have comments unless it is not that simple to explain. It must
   be understandable by itself. With that in mind, I did not add too many comments over the code while coding this
   assignment, except from the JavaDoc comments.
10. Swagger - There is no need for further explanation.
11. Exclusions on coverage - I have excluded the PhilipsCodeChallengeApplication, Auto-Generated Models (Simple POJOs),
    and all the Constants files from Jacoco coverage, since those classes are not meant to be tested. Anyway,
    PhilipsCodeChallengeApplication is tested when we set up the Spring Boot context while running the integration
    tests.

#### Improvement Suggestions

1. Increase the Observability by having a way to monitor the service using tools as NewRelic, DataDog or other tools
   with this intention. They could give the developers visibility about logs, traces and metrics which helps us to
   better tune the resources and tackle bugs whenever necessary. It would also enable us to set alerts for any anomaly
   on the service.
2. The integration tests should not be run at build time during development. This should be in a different maven task
   and run during in a CI/CD or on demand.
3. Add a file to handle message properties in case of need for internationalization.
4. Improve the docker-compose file to also run the application. Currently, there is a need to run the docker and then
   later run the application.
5. There is a couple of Exceptions being thrown and logged on the logs while compiling the code, which I don't like. I
   would definitely spend some time trying to get them fixed.
