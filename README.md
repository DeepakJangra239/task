# **Task**
_Spring Boot based solution which could read a CSV file (as part of the class path) at application start-up & store each record(Employee record) in a Database._

###### Have an in-memory cache solution (Spring Boot in-memory Cache (ConcurrentHashMap) stores employee record which  have been recently updated).

#### Project Specs

##### WebApplication Framework: SpringBoot 2.3.3 Release

##### Database: MySQL Server

##### Cache: SpringBoot Starter Cache

##### Additional Libraries: Lombok, Swagger, OpenCSV, JUnit

<br />

###### Current Port configuration: 8090 (Update under application properties as required)

Health Check URL: 
######http://localhost:8090/task/actuator/health

<br />

The REST end points available in the solution can be found at 
######http://localhost:8090/task/swagger-ui/
