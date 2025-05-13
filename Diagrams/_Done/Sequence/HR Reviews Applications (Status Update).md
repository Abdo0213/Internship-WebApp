```mermaid
sequenceDiagram
    actor HR
    participant API Gateway
    participant User Microservice
    participant Internship Microservice
    participant Notification Service
    participant Internship DB

    HR->>API Gateway: PATCH /applications/{id} (status=APPROVED)
    API Gateway->>User Microservice: Validate HR Token
    User Microservice-->>API Gateway: HR verification
    
    alt Valid HR
        API Gateway->>Internship Microservice: Update status
        Internship Microservice->>Internship DB: Update application
        Internship Microservice->>Notification Service: Create notification
        Notification Service-->>Internship Microservice: Done
        Internship Microservice-->>API Gateway: 200 OK
        API Gateway-->>HR: Status updated
    else Invalid HR
        API Gateway-->>HR: 403 Forbidden
    end