# dp3t-authz-backend-mt 

## Running the Authorisation Server

The Authorisation Server is a regular Spring Boot application. It can be run in standalone mode using Spring Boot Maven goals.

```
mvn spring-boot:run
```

By default, the Spring profile named 'cloud-dev' is active. This profile uses H2 to create an in-memory database and execute Flyway migrations to build the schema.

The server also requires a number of key pairs to operate: a key pair for signing responses and another to sign JWT tokens. These should be provided in the server's configuration. If ommitted, temporary key pairs will be generated for testing purposes.

The server includes a number of other profiles that can be activated on launch to enable different modes of operation.

- jwt

The 'jwt' profile activates all authentication and authorisation configuration classes.

- cloud-prod

The 'cloud-prod' activates production mode. In this mode, the server will try connect to a persistent store.

## Configuration
