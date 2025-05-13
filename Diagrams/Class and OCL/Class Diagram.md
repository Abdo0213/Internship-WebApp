```mermaid
classDiagram
    class Admin {
        +Long id
        +String name
        +User user
    }

    class Company {
        +Long id
        +String name
        +String industry
        +String location
    }

    class Hr {
        +Long id
        +User user
        +Company company
    }

    class Role {
        +Long id
        +String name
    }

    class Student {
        +Long id
        +String phone
        +String college
        +String faculty
        +String grade
        +String cv
        +String yearOfGraduation
        +User user
    }

    class User {
        +Long id
        +String username
        +String password
        +String email
        +String fname
        +Role role
    }

    class Application {
        +Long id
        +Long studentId
        +Internship internship
        +Status status
        +LocalDateTime appliedAt
        +LocalDateTime updatedAt
        +String resumeUrl
    }

    class Internship {
        +Long id
        +String title
        +String description
        +String companyName
        +LocalDateTime createdAt
        +Status status
        +String location
        +InternshipType type
        +String duration
        +String stipend
        +Long hrId
    }

    class Notification {
        +Long id
        +Long studentId
        +Application application
        +boolean isRead
        +LocalDateTime createdAt
        +String internshipTitle
    }

    class ApplicationService {
        +ApplicationRepository applicationRepository
        +InternshipRepository internshipRepository
        +UserServiceClient userServiceClient
        +getStudentApplications(Long studentId)
        +getOneApplication(Long id)
        +getApplicationsByHr(Long hrId)
        +getApplicationsByInternship(Long internshipId)
        +getApplicationsIdByInternship(Long internshipId)
        +createApplication(Long studentId, Long internshipId, String authToken)
        +updateApplication(Long id, Application.Status status)
        +deleteAllByIds(List~Long~ ids)
        +findIdsByInternshipIds(List~Long~ internshipIds)
    }

    class InternshipService {
        +InternshipRepository internshipRepository
        +UserServiceClient userServiceClient
        +createInternship(Internship internship, String authToken)
        +getActiveInternships()
        +getAllInternships(int page, int size, String search)
        +getOneInternship(Long id)
        +getInternshipsByHr(Long hrId, Pageable pageable, String search)
        +getInternshipsByHrId(Long hrId)
        +updateInternship(Long id, Internship updatedInternship)
        +deleteInternship(Long id)
        +getInternshipCount()
        +deleteAllByIds(List~Long~ ids)
        +findIdsByHrIds(List~Long~ hrIds)
    }

    class NotificationService {
        +NotificationRepository notificationRepository
        +getNotificationsByStudentId(Long studentId, Pageable pageable)
        +getNotificationByIdAndStudentId(Long id, Long studentId)
        +markAsRead(Long id, Long studentId)
        +createNotification(Notification notification)
        +searchByInternshipTitle(Long studentId, String searchTerm, Pageable pageable)
        +deleteNotificationsByApplicationIds(List~Long~ applicationIds)
    }

    class AdminService {
        +AdminRepository adminRepository
        +UserService userService
        +PasswordEncoder passwordEncoder
        +getOneAdmin(Long id)
        +updateAdmin(Long id, User updatedAdmin)
    }

    class AuthService {
        +AuthenticationManager authenticationManager
        +StudentRepository studentRepository
        +UserRepository userRepository
        +CompanyRepository companyRepository
        +RoleRepository roleRepository
        +PasswordEncoder passwordEncoder
        +authenticate(String username, String password)
        +registerUser(String username, String password, String email, String fName, String cv)
    }

    class CompanyService {
        +CompanyRepository companyRepository
        +UserService userService
        +getCompanyByName(String name)
        +getAllCompanies()
        +getCompanyById(Long id)
        +addCompany(CompanyDto dto)
        +updateCompany(Long id, CompanyDto updatedDto)
        +deleteCompany(Long id)
        +getCompanyCount()
    }

    class HrService {
        +HrRepository hrRepository
        +UserRepository userRepository
        +PasswordEncoder passwordEncoder
        +CompanyRepository companyRepository
        +RoleRepository roleRepository
        +getAllHr()
        +getHrByCompanyId(Long companyId)
        +getHrById(Long id)
        +updateHr(Long id, User updatedHr)
        +addHr(HrDto hrDto)
        +hrExists(Long id)
        +DeleteHr(Long id)
        +deleteAllByIds(List~Long~ ids)
        +getHrCount()
    }

    class StudentService {
        +StudentRepository studentRepository
        +UserService userService
        +PasswordEncoder passwordEncoder
        +getUserByUsername(String username)
        +getAllStudents()
        +getStudentById(Long id)
        +updateStudent(Long id, StudentDto updatedStudent)
        +studentExists(Long id)
    }

    class UserService {
        +UserRepository userRepository
        +RoleRepository roleRepository
        +PasswordEncoder passwordEncoder
        +getUserByUsername(String username)
        +getUserById(Long id)
        +AddUser(String username, String password, String email, String FName, String roleName)
        +UpdateUser(Long id, String userName, String password, String email, String FName, String roleName)
        +DeleteUser(Long id)
    }

    Admin "1" --* "1" User : extends
    Hr "1" --* "1" User : extends
    Student "1" --* "1" User : extends
    
    User "1" --> "1" Role : has
    Hr "1" --> "1" Company : works for
    
    Application "1" --> "1" Internship : applies to
    Application "1" --> "1" Student : belongs to
    Notification "1" --> "1" Application : references
    
    Internship "1" --> "1" Hr : posted by
    
    AdminService --> Admin : manages
    HrService --> Hr : manages
    StudentService --> Student : manages
    UserService --> User : manages
    CompanyService --> Company : manages
    ApplicationService --> Application : manages
    InternshipService --> Internship : manages
    NotificationService --> Notification : manages
    AuthService --> User : authenticates