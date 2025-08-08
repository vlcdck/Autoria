# AUTORIA Clone

A full-featured clone of the AUTORIA platform built with **Java 21**, **Spring Boot**, and **Docker**. The project is designed to be easily launched with a single command after minimal configuration.

---

## Features

- Built on Java 21 and Spring Boot
- Containerized using Docker and Docker Compose
- PostgreSQL database running in a container
- JWT-based authentication with configurable token lifetime
- Email notifications with SMTP using app passwords
- Profanity filter integration via API Ninjas
- Automatic admin user creation on startup

---

## Prerequisites

Before running the project, ensure you have the following installed:

- **Java 21** (for local development if needed)
- **Docker** (version 20.x or newer)
- **Docker Compose** (version 1.29.x or newer)
- Access to the project repository

---

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/vlcdck/Autoria.git
cd Autoria
```

### 2. Configure environment variables

Create a `.env` file in the root directory based on the `.env.example` file:

- **SPRING_APP_ADMIN_EMAIL** and **SPRING_APP_ADMIN_PASSWORD**: Credentials for the admin user created automatically on startup.
- **SPRING_MAIL_USERNAME** and **SPRING_MAIL_PASSWORD**: Your email address and **app password** for SMTP (requires two-factor authentication).
- **PROFANITY_FILTER_API_KEY**: Get your key by registering at [API Ninjas](https://api-ninjas.com/).

---

### 3. Build and launch the application

Run the following command to build and start all services:

```bash
docker compose up --build
```

This will start the backend service and a PostgreSQL database in containers.

---

## Accessing the API

Once running, you can use the provided Postman collection to explore the API:

- **Postman Collection:** `Autoria_Api.postman_collection.json`

Import it into Postman and start testing endpoints immediately.

---

## Mail Settings (SMTP)

- Enable **Two-Factor Authentication** on your email account.
- Generate an **app password** specifically for this project.
- Use this app password in the `.env` file as `SPRING_MAIL_PASSWORD`.

This ensures reliable mail delivery for notifications and password resets.

---

## Technologies Used

| Technology      | Description                      |
|-----------------|--------------------------------|
| Java 21         | Programming language            |
| Spring Boot     | Framework for REST API          |
| Docker          | Containerization platform       |
| Docker Compose  | Multi-container orchestration   |
| PostgreSQL      | Relational database             |
| JWT             | JSON Web Tokens for security   |
| API Ninjas      | Profanity filtering service     |

---

## Troubleshooting

- Make sure Docker and Docker Compose are installed and running correctly.
- Verify your `.env` file is correctly configured with valid credentials and keys.
- If email sending fails, check SMTP settings and app password.
- Logs are accessible via Docker Compose output for debugging.