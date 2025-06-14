# Internship Application Portal

A web-based system designed to simplify and streamline the internship application process for students and HR representatives. This platform allows students to browse internships, submit applications with resumes, and track their status, while HR and admins can manage internship postings and review applicants.

## 🔧 Technologies Used

### Backend
- **Spring Boot** – Microservices-based backend
- **Java**
- **Spring Security** – JWT-based authentication
- **Spring Data JPA** – ORM for database interaction
- **MySQL** – Relational database
- **Aspect-Oriented Programming (AOP)** – For logging cross-cutting concerns
- **Eureka Server** – Service discovery (optional detail depending on your architecture)
- **API Gateway** – Routing and security (e.g., Spring Cloud Gateway)

### Frontend
- **React.js** – Consumes REST APIs for student/HR interaction

### Tools
- **Postman** – API testing
- **Git & GitHub** – Version control
- **Visual Studio Code / IntelliJ IDEA** – Development environments

---

## 🧱 System Architecture

The application is built using a **microservices architecture** with clean separation of concerns. Each service follows a layered structure including:

- **Controller Layer** – Handles HTTP requests
- **Service Layer** – Contains business logic
- **Repository Layer** – Interfaces with the database
- **Model Layer** – Contains domain entities

### Microservices
- **User Service** – Manages student and HR accounts
- **Internship Service** – Manages internship postings and applications

---

## 🎯 Core Features

### For Students
- View available internships
- Submit internship applications with personal details and CV
- Track application status: `Pending`, `Reviewed`, `Accepted`, or `Rejected`

### For HR/Admin
- Create and manage internship postings
- Review student applications and update their statuses

---

## 🔐 Security

- **JWT Authentication** is used to secure endpoints and manage user sessions across services.
- Role-based access for Students, HR, and Admins.

---

## 🧪 Testing

- APIs tested using **Postman**
- Authentication and authorization flows validated via manual and automated test cases

---


