```mermaid
sequenceDiagram
    actor Admin
    participant API Gateway
    participant User Microservice
    participant User DB

    Admin->>API Gateway: POST /users (with authToken)
    API Gateway->>User Microservice: Validate Admin Token
    User Microservice-->>API Gateway: Admin verification
    
    alt Is Admin
        API Gateway->>User Microservice: Create/Update User
        User Microservice->>User DB: Perform operation
        User DB-->>User Microservice: Success
        User Microservice-->>API Gateway: 200 OK
        API Gateway-->>Admin: Success
    else Not Admin
        API Gateway-->>Admin: 403 Forbidden
    end