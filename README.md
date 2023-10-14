# Hibernate States Example

This project demonstrates the different states that Hibernate entities can be in when using Hibernate ORM. <br>The examples show how entities transition from Transient to Persistent, Detached, and Removed states.

## Technologies Used

- Java
- Spring Boot
- Spring Data JPA (Hibernate)
- H2 Database
- JUnit 5

## Understanding Hibernate States

This project contains multiple services and tests that demonstrate the different states of Hibernate entities. The key states include:

- **Transient**: Entities that are not associated with any Hibernate session.
- **Persistent**: Entities that are associated with a Hibernate session and will be saved to the database.
- **Detached**: Entities that were once associated with a session but are no longer attached.
- **Removed**: Entities that have been marked for removal.