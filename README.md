# MediTrack

Smart Patient Health Management API built with Spring Boot.

MediTrack is a backend-focused health tech project that simulates how a real-world clinical platform could manage patients, doctors, appointments, medical histories, medications, admin operations, search, reporting, and AI-assisted workflows in a secure environment.

## Overview

This project was built to demonstrate practical backend engineering skills across:

- secure REST API design
- role-based access control with JWT authentication
- relational data modeling with PostgreSQL and JPA
- full-text search with Elasticsearch
- AI-assisted healthcare workflows with Spring AI and Ollama
- cross-cutting logging with Spring AOP
- schema versioning with Flyway

The application is designed around three core user roles:

- `PATIENT`
- `DOCTOR`
- `ADMIN`

## Highlights

- Built with `Spring Boot 3`, `Java 21`, and modern Spring ecosystem tooling
- Implements stateless JWT authentication with role-based authorization
- Uses `PostgreSQL` for transactional healthcare data and `Elasticsearch` for search use cases
- Integrates `Spring AI` with `Ollama` for symptom triage and note summarization
- Includes real-world backend modules: appointments, medical history, medications, admin dashboard, and operational reports
- Uses `Flyway` migrations for controlled database evolution
- Exposes OpenAPI documentation for easier API review and testing

## Core Features

### Authentication and Security

- User registration and login
- Access token and refresh token flow
- Stateless JWT-based authentication
- Method-level authorization with role checks and ownership validation
- Protected routes for patients, doctors, and admins

### Patient Management

- View patient profile
- Update patient profile
- Restrict access so patients can only manage their own records while doctors and admins can access appropriate data

### Doctor Management

- List doctors with specialization filtering
- View doctor profile
- Update doctor profile

### Appointment Management

- Book appointments
- View appointment details
- Retrieve doctor appointment schedules
- Update appointment status
- Cancel appointments
- Prevent duplicate active appointments for the same doctor and time slot

### Medical History

- View paginated patient medical history
- Add medical history entries for a patient

### Medication Management

- View active medications for a patient
- Prescribe medication

### Search

- Search doctors
- Search patients
- Search medications
- Elasticsearch-backed indexing and query support for faster discovery across key healthcare records

### Reporting and Admin Operations

- List users with pagination
- Deactivate user accounts
- View admin dashboard summary metrics
- Generate hospital overview reports
- Generate doctor workload reports
- Generate doctor patient activity reports
- Generate doctor medication summary reports

### AI Features

- `POST /api/v1/ai/triage`
  - Accepts a list of symptoms
  - Uses `Spring AI` with `Ollama` to return urgency guidance, possible conditions, and a suggested specialization
- `POST /api/v1/ai/summarize-notes`
  - Accepts free-text clinical notes
  - Returns a structured summary containing chief complaint, assessment, and plan
- Graceful fallback response when the local AI model is unavailable

### Cross-Cutting Concerns

- Service-level logging with `Spring AOP`
- Global exception handling with consistent API responses
- DTO validation with Jakarta Validation

## Technology Stack

### Backend

- `Java 21`
- `Spring Boot 3.5.13`
- `Spring Web`
- `Spring Data JPA`
- `Spring Security`
- `Spring AOP`
- `Spring Validation`

### Data and Search

- `PostgreSQL 16`
- `Elasticsearch 8`
- `Flyway`

### AI

- `Spring AI`
- `Ollama`
- Model configured: `llama3.2:3b`

### API and Documentation

- `OpenAPI / Swagger UI`

### Testing

- `JUnit 5`
- `Spring Boot Test`
- `Spring Security Test`
- `H2` for test runtime

### Build and Tooling

- `Gradle (Kotlin DSL)`
- `Docker Compose`
- `Lombok`
- `JJWT`

## Architecture Summary

MediTrack follows a layered backend architecture:

- `Controllers` handle HTTP requests and responses
- `Services` contain business logic
- `Repositories` manage data access through JPA, JDBC, and Elasticsearch
- `Security` handles authentication, token validation, and authorization rules
- `AI layer` uses Spring AI to call a local Ollama model
- `AOP layer` applies logging without polluting business logic

## Main API Areas

### Auth

- `POST /api/v1/auth/register`
- `POST /api/v1/auth/login`
- `POST /api/v1/auth/refresh`

### Patients

- `GET /api/v1/patients/{id}`
- `PUT /api/v1/patients/{id}`

### Doctors

- `GET /api/v1/doctors`
- `GET /api/v1/doctors/{id}`
- `PUT /api/v1/doctors/{id}`

### Appointments

- `POST /api/v1/appointments`
- `GET /api/v1/appointments/{id}`
- `GET /api/v1/doctors/{doctorId}/appointments`
- `PUT /api/v1/appointments/{id}/status`
- `DELETE /api/v1/appointments/{id}`

### Medical History

- `GET /api/v1/patients/{patientId}/history`
- `POST /api/v1/patients/{patientId}/history`

### Medications

- `GET /api/v1/patients/{patientId}/medications`
- `POST /api/v1/patients/{patientId}/medications`

### Search

- `GET /api/v1/search/doctors?q=`
- `GET /api/v1/search/patients?q=`
- `GET /api/v1/search/medications?q=`

### AI

- `POST /api/v1/ai/triage`
- `POST /api/v1/ai/summarize-notes`

### Admin and Reports

- `GET /api/v1/admin/users`
- `DELETE /api/v1/admin/users/{id}`
- `GET /api/v1/admin/dashboard`
- `GET /api/v1/reports/admin/hospital-overview`
- `GET /api/v1/reports/admin/doctor-workload`
- `GET /api/v1/reports/doctors/{doctorId}/patient-activity`
- `GET /api/v1/reports/doctors/{doctorId}/medication-summary`

## Getting Started

### Prerequisites

- `Java 21`
- `Docker` and `Docker Compose`

### Infrastructure

The project uses Docker Compose for local services:

- `PostgreSQL`
- `Elasticsearch`
- `Ollama`

Start dependencies:

```bash
docker compose up -d
```

Pull the Ollama model used by the application:

```bash
ollama pull llama3.2:3b
```

### Run the Application

```bash
./gradlew bootRun
```

The API runs under the context path:

```text
/api
```

## API Documentation

Swagger UI:

```text
http://localhost:8080/api/swagger-ui.html
```

OpenAPI JSON:

```text
http://localhost:8080/api/api-docs
```

## Example Use Cases

- A patient registers, logs in, books an appointment, and views medications
- A doctor reviews patient history, updates appointment status, and summarizes clinical notes with AI
- An admin monitors user activity, reviews dashboard statistics, and pulls operational reports
- Clinical staff search for doctors, patients, or medications using Elasticsearch-backed queries

## Project Notes

- Combines core Spring Boot backend fundamentals with modern platform capabilities like AI and search
- Demonstrates secure API design in a domain where access control matters
- Models realistic healthcare workflows instead of generic CRUD only
- Shows practical use of multiple persistence styles: JPA, JDBC reporting queries, and Elasticsearch indexing
- Brings together API security, search, reporting, and AI-assisted workflows in one application

## Future Improvements

- Expand automated service and integration test coverage
- Add audit trail persistence and admin audit log viewing
- Add more production-style observability and deployment configuration

## Author

Built as a backend engineering project in a health tech context using Spring Boot.
