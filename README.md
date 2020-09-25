# dp3t-authz-backend-mt 

The purpose of the Authorisation Server is maintain CovidCodes and issue tokens to be used by the contact tracing app to upload TEKs to the backend server.

## Running the Authorisation Server

The Authorisation Server is a regular Spring Boot application. It can be run in standalone mode using the following Spring Boot Maven goal:

```
mvn spring-boot:run
```

The server uses Spring profiles to determine in which mode it should run and which features to enable.

In the default application.properties configuration file, the Spring profile named 'cloud-dev' is enabled. This profile is intended for development purposes. It uses H2 to create an in-memory database and execute Flyway migrations to build the schema.

The server includes a number of other profiles that can be activated on launch to enable different modes of operation.

- jwt

The 'jwt' profile activates all authentication and authorisation configuration classes.

- cloud-prod

The 'cloud-prod' activates production mode. In this mode, the server will try connect to a persistent store.

## Configuration

Depending on the enabled profiles, the server expects the following configuration values to be provided:

| Config | Required | Default |
| ----------- | ----------- | ----------- |
| vcap.services.ecdsa_cs_prod.credentials.privateKey |
| vcap.services.ecdsa_cs_prod.credentials.publicKey |
| authz.prod.jwt.privateKey |
| authz.prod.jwt.privateKey |

The server requires a number of key pairs to operate: a key pair for signing responses and another to sign JWT tokens. These should be provided in the server's configuration. 

If ommitted, temporary key pairs will be generated for testing purposes.
