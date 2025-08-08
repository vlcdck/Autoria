
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

Create a `.env` file in the root directory based on the `.env.example` file.

---

## Environment Variables Guide

The project uses a `.env` file to configure essential settings for the database, authentication, email, profanity filtering, and file storage. Below is a detailed explanation of each variable.

### Database Configuration

| Variable           | Description                          | Example                 |
|--------------------|------------------------------------|-------------------------|
| `POSTGRES_DB`      | Name of the PostgreSQL database     | `autoria_db`             |
| `POSTGRES_USER`    | Username for PostgreSQL connection  | `autoria_user`           |
| `POSTGRES_PASSWORD`| Password for the PostgreSQL user    | `strongpassword123`      |

> These credentials allow the app to connect to the PostgreSQL database running inside Docker.

---

### JWT (JSON Web Token) Settings

| Variable                      | Description                                               | Example           |
|-------------------------------|-----------------------------------------------------------|-------------------|
| `JWT_SECRET`                  | Secret key used to sign and verify JWT tokens             | `verySecretKey123` |
| `JWT_ACCESS_TOKEN_EXPIRATION` | Access token expiration time in **milliseconds**          | `900000` (15 min)  |
| `JWT_REFRESH_TOKEN_EXPIRATION`| Refresh token expiration time in **milliseconds**         | `604800000` (7 days)|
| `JWT_EMAIL_TOKEN_EXPIRATION`  | Email verification/reset token expiration time in **minutes** | `30`               |

> Use strong, unpredictable values for `JWT_SECRET`. Adjust expiration times based on your security needs.

---

### Admin User Credentials

| Variable                  | Description                               | Example                |
|---------------------------|-------------------------------------------|------------------------|
| `SPRING_APP_ADMIN_EMAIL`  | Email address for the admin user created on startup | `admin@example.com`     |
| `SPRING_APP_ADMIN_PASSWORD` | Password for the admin user               | `AdminStrongPass!`      |

> This admin user is created automatically when the application starts.

---

### Email Sender (SMTP) Settings

| Variable              | Description                            | Example                      |
|-----------------------|----------------------------------------|------------------------------|
| `SPRING_MAIL_USERNAME`| Email address used to send emails      | `myemail@gmail.com`           |
| `SPRING_MAIL_PASSWORD`| App password for SMTP authentication   | `abcd-efgh-ijkl-mnop`         |

> Generate an **app password** for your email provider (especially if 2FA is enabled) and use it here instead of your regular email password.

---

### Profanity Filter

| Variable             | Description                           | Example                  |
|----------------------|-------------------------------------|--------------------------|
| `PROFANITY_FILTER_API_KEY` | API key for the profanity filter service | `your-api-ninjas-key`    |

> Obtain your API key by registering at [API Ninjas](https://api-ninjas.com/).

---

### File Upload Directory

| Variable          | Description                                      | Example                       |
|-------------------|-------------------------------------------------|-------------------------------|
| `FILE_UPLOAD_DIR` | Universal path where uploaded files are stored  | `/app/uploads` or `/var/data/uploads` |

> Ensure this directory exists and is writable by the app.

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