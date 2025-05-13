```mermaid
sequenceDiagram
    actor Student
    participant API Gateway
    participant Internship Microservice
    participant Internship DB

    Student->>API Gateway: GET /internships?page=1&size=10&search=tech
    API Gateway->>Internship Microservice: Get internships
    Internship Microservice->>Internship DB: Query active internships
    Internship DB-->>Internship Microservice: Paginated results
    Internship Microservice-->>API Gateway: 200 OK with data
    API Gateway-->>Student: Display results