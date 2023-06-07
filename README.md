# java-shareit

### Project description
This project is built on the principles of the sharing economy trend, and allows users to request, book, and search for items that can be shared.

### Tech stack

ShareIt adopts a microservices architecture, comprising a server and a gateway for filtering bad requests. Communication between services is made via HTTP using WebClient.

Server is a REST service built with Spring Boot and Maven, utilizing Lombok and interacting with a PostgreSQL database through an ORM framework (Hibernate).

The entire project is containerized using Docker.

### System requirements

- JVM installed
- PostgreSQL database is created using any Postgre client (e.g., pgAdmin 4)
- Docker installed (for building and running containers)
