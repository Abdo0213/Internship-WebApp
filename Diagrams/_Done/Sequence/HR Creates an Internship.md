```mermaid
sequenceDiagram
    actor HR
    participant API Gateway
    participant User Microservice
    participant Internship Microservice
    participant Company DB
    participant Internship DB

    HR->>API Gateway: POST /internships (with authToken)
    API Gateway->>User Microservice: Validate Token + Check HR Role
    User Microservice-->>API Gateway: HR details (hrId, companyId)
    
    alt Valid HR
        API Gateway->>Internship Microservice: Create Internship (with companyId)
        Internship Microservice->>Company DB: Validate company exists
        Company DB-->>Internship Microservice: Company valid
        Internship Microservice->>Internship DB: Save internship
        Internship DB-->>Internship Microservice: Saved record
        Internship Microservice-->>API Gateway: 201 Created
        API Gateway-->>HR: Internship created
    else Invalid credentials
        API Gateway-->>HR: 403 Forbidden
    end