```plantuml
@startuml
actor Student as student
participant "API Gateway" as gateway
participant "User Microservice" as user_ms
participant "Internship Microservice" as internship_ms
database "User DB" as user_db
database "Internship DB" as internship_db

student -> gateway: Submit Application (internshipId, authToken)
gateway -> user_ms: Validate Token (authToken)
user_ms -> user_db: Verify token validity
user_db --> user_ms: User details (studentId, role)
user_ms --> gateway: Token validation response

alt token valid
    gateway -> internship_ms: Create Application (studentId, internshipId)
    internship_ms -> internship_db: Check internship exists
    internship_db --> internship_ms: Internship details
    internship_ms -> internship_db: Create application record
    internship_db --> internship_ms: Application created
    
    internship_ms -> internship_db: Get HR ID for internship
    internship_db --> internship_ms: HR ID
    
    internship_ms -> user_ms: Get HR details (HR ID)
    user_ms -> user_db: Fetch HR info
    user_db --> user_ms: HR details
    user_ms --> internship_ms: HR details
    
    internship_ms -> internship_db: Create notification
    internship_db --> internship_ms: Notification created
    
    internship_ms --> gateway: Application success response
    gateway --> student: Application submitted successfully
else token invalid
    gateway --> student: Error: Unauthorized
end
@enduml
