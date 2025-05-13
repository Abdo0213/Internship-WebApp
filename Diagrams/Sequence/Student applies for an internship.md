```mermaid
sequenceDiagram
    actor Student
    participant API Gateway
    participant User Microservice
    participant Internship Microservice
    participant User DB
    participant Internship DB

    Student->>API Gateway: POST /applications (internshipId, authToken)
    API Gateway->>User Microservice: Validate Token (authToken)
    User Microservice->>User DB: Verify token
    User DB-->>User Microservice: User details (studentId, role)
    User Microservice-->>API Gateway: Token valid (studentId)

    alt Token valid
        API Gateway->>Internship Microservice: Create Application (studentId, internshipId)
        Internship Microservice->>Internship DB: Check internship exists
        Internship DB-->>Internship Microservice: Internship details (hrId)
        Internship Microservice->>Internship DB: Save application
        Internship Microservice->>User Microservice: Get HR details (hrId)
        User Microservice->>User DB: Fetch HR info
        User DB-->>User Microservice: HR email/name
        User Microservice-->>Internship Microservice: HR details
        Internship Microservice->>Internship DB: Create notification
        Internship Microservice-->>API Gateway: Success (201 Created)
        API Gateway-->>Student: Application submitted
    else Token invalid
        API Gateway-->>Student: 401 Unauthorized
    end