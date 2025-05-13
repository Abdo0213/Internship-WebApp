```mermaid
erDiagram
    INTERNSHIP ||--o{ APPLICATION : has
    INTERNSHIP ||--o{ NOTIFICATION : generates
    STUDENT ||--o{ APPLICATION : submits
    STUDENT ||--o{ NOTIFICATION : receives
    COMPANY ||--o{ INTERNSHIP : offers
    USER ||--o{ COMPANY : manages
    USER ||--o{ WEBSITE : owns

    INTERNSHIP {
        bigint id PK
        varchar(10) company_name
        datetime(6) created_at
        text description
        varchar(20) duration
        bigint size
        varchar(50) location
        varchar(50) status
        varchar(50) stipend
        varchar(100) title
        varchar(50) type
    }

    APPLICATION {
        bigint id PK
        datetime(6) applied_at
        varchar(50) resume_url
        varchar(50) status
        bigint student_id FK
        datetime(6) updated_at
        bigint internship_id FK
    }

    NOTIFICATION {
        bigint id PK
        datetime(6) created_at
        varchar(255) internship_title
        bit is_read
        bigint student_id FK
        bigint application_id FK
    }

    COMPANY {
        bigint id PK
        varchar(255) library
        varchar(255) location
        varchar(255) name
    }

    STUDENT {
        bigint id PK
        varchar(255) email
        varchar(255) name
        varchar(255) password
        varchar(255) username
        bigint role_id
    }

    USER {
        bigint id PK
        varchar(255) email
        varchar(255) name
        varchar(255) password
        varchar(255) username
        bigint role_id
    }

    WEBSITE {
        bigint id PK
        bigint user_id FK
        varchar(255) name
    }