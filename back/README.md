# Chatop-back

## Overview

Chatop-back is the backend for Chatop, a rental platform. This application is built using Spring Boot and provides RESTful APIs for managing rentals and user authentication.

## Prerequisites

Before you begin, ensure you have the following installed on your machine:

- Java 17
- Maven
- MySQL

## Setup

1. **Clone the repository:**

   ```bash
   git clone https://github.com/EmySim/chatop-back.git
   cd chatop-back
   ```

2. **Create a `.env` file in the root directory and add the following environment variables:**

   ```plaintext
   DATABASE_URL=jdbc:mysql://localhost:3306/chatopdb
   DATABASE_USERNAME=ChatopUser
   DATABASE_PASSWORD=ChatopDB2025!
   JWT_SECRET=1Ux6Dnxnx1mfnAM1aQFf+fzMoUuyitQCXTEIHfgTklg=
   ```

3. **Ensure the MySQL server is running and accessible at the specified `DATABASE_URL`.**

   ```properties
   spring.datasource.url=${DATABASE_URL}
   spring.datasource.username=${DATABASE_USERNAME}
   spring.datasource.password=${DATABASE_PASSWORD}
   ```

4. **Build the project using Maven:**

   ```bash
   mvn clean install
   ```

5. **Run the application:**

   ```bash
   mvn spring-boot:run
   ```

## Running Tests

To run the unit and integration tests, use the following command:

```bash
mvn test
```

## API Documentation

The API documentation is available at `http://localhost:3001/swagger-ui.html` once the application is running.

## Additional Information

- **Logging:** The application uses Java's built-in logging framework. Logs are configured in the `application.properties` file.
- **Security:** JWT is used for authentication. Ensure that the `JWT_SECRET` environment variable is set correctly.
- **Database:** The application uses MySQL as the database. Ensure that the database is running and the connection details are correct in the `.env` file.

## Contributing

If you wish to contribute to this project, please follow these steps:

1. Fork the repository.
2. Create a new branch (`git checkout -b feature-branch`).
3. Make your changes.
4. Commit your changes (`git commit -am 'Add new feature'`).
5. Push to the branch (`git push origin feature-branch`).
6. Create a new Pull Request.

## License

This project is licensed under the MIT License.
