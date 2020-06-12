# Rabobank Customer Statement Processor
 
Assignment to Rabobank Customer Statement Processor - Java Software Engineer position
 
## Decisions
 
 * I decided to use a simple rest API with spring-boot-starter-web, it does not worth use reactive API, 
 I could show my knowledge on that, but it would be over-engineering.
 *  The project has swagger even though only one endpoint is available, I think is a nice way to document the API, 
 besides that, I added a filter to redirect the root path to the swagger-UI (I do not like to type the address every time) 

### Code Decisions

* I have a domain called Validation, where is all the code related to the statement validation.
There are two other packages outside the Validation domain:
    * config: Configuration for all application, Swagger config and Swagger Filter, and a generic RestControllerAdvice
    to handle with application exception related to Internal Server Error or ConstraintViolationException for example
    * dto: a common contract to response when success or error
* I did not add comments in the code, I think it is clean and easy to comprehend.
* There is one another RestControllerAdvice inside the Validation domain to handle with the specifics domain exceptions,
this bean is set as HIGHEST_PRECEDENCE
* I created an interface for the controller, most because I do no like to pollute the controller code with 
  Rest and Swagger annotations
 
## Run the application

All final executions can be seen at [http://localhost:8080](http://localhost:8080)

The basic ways as usual:

`Debug via IntelliJ`

or

`mvn spring-boot:run`

or

```bash
 mvn clean package
 java -jar target/statement-processor-0.0.1.jar
```

or

Using docker. I am using Spring Boot 2.3, it came with Build Docker images using Cloud Native Buildpacks,
I could write the Dockerfile, but let's try something new, but it will generate an image close to 300Mb, in a real 
the project I either will research about decreases this number using Buildpacks or creating a Dockerfile using an alpine image. 

```bash
  mvn spring-boot:build-image
  docker run -p 8080:8080 --name statement-processor statement-processor:0.0.1
```
 
 ## Stack And Dependencies
 
 * Java 11
 * Spring Boot
 * JUnit
 * Unit and Integrated tests
 * Actuator - [http://localhost:8080/health](http://localhost:8080/health)
 * Lombok
 * PMD - source code analyzer, runs via maven plugin every maven compile or test command
 * Swagger2
 * spring-boot-starter-validation - spring 2.3 does not come with Jakarta Validations in the starter.
 * hamcrest - some nice assertions for test
 * spring-boot-devtools - better developer experience

 ## Endpoints
 
 Samples using 8080 port
 
 * POST http://localhost:8080/validation
 
 ### Postman Collection
 
 You can use the postman collection with the server variable to be easy to interact with the API.
 This collection has some requests sample, and all requests have tests, there are also some json samples to post

 [Statement Processor.postman_collection.json](Statement Processor.postman_collection.json)
