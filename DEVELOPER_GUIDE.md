# Developer Guide

## Table of Contents

1. [Getting Started](#getting-started)
2. [Project Structure](#project-structure)
3. [Setup Instructions](#setup-instructions)
4. [Development Workflow](#development-workflow)
5. [Testing Guide](#testing-guide)
6. [Deployment](#deployment)
7. [Best Practices](#best-practices)
8. [Troubleshooting](#troubleshooting)

## Getting Started

### Prerequisites

- **Java**: OpenJDK 17 or higher
- **Maven**: 3.8.0 or higher
- **Database**: MySQL 8.0 or PostgreSQL 13+
- **IDE**: IntelliJ IDEA, Eclipse, or VS Code with Java extensions
- **Git**: For version control

### Quick Start

```bash
# Clone the repository
git clone <repository-url>
cd springBoot

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run

# Or run the JAR file
java -jar target/spring-boot-app-1.0.0.jar
```

### Environment Setup

Create an `application-dev.yml` file in `src/main/resources`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/springboot_dev
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:password}
    driver-class-name: com.mysql.cj.jdbc.Driver

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.MySQL8Dialect

  profiles:
    active: dev

jwt:
  secret: ${JWT_SECRET:mySecretKey}
  expiration: 86400

logging:
  level:
    com.example: DEBUG
    org.springframework.security: DEBUG
```

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── example/
│   │           ├── SpringBootApplication.java           # Main application class
│   │           ├── config/                              # Configuration classes
│   │           │   ├── SecurityConfig.java
│   │           │   ├── DatabaseConfig.java
│   │           │   └── SwaggerConfig.java
│   │           ├── controller/                          # REST controllers
│   │           │   ├── UserController.java
│   │           │   ├── AuthController.java
│   │           │   └── PublicController.java
│   │           ├── service/                             # Business logic layer
│   │           │   ├── UserService.java
│   │           │   ├── AuthService.java
│   │           │   └── impl/
│   │           │       ├── UserServiceImpl.java
│   │           │       └── AuthServiceImpl.java
│   │           ├── repository/                          # Data access layer
│   │           │   ├── UserRepository.java
│   │           │   └── RefreshTokenRepository.java
│   │           ├── entity/                              # JPA entities
│   │           │   ├── User.java
│   │           │   ├── RefreshToken.java
│   │           │   └── BaseEntity.java
│   │           ├── dto/                                 # Data Transfer Objects
│   │           │   ├── request/
│   │           │   │   ├── CreateUserRequest.java
│   │           │   │   ├── UpdateUserRequest.java
│   │           │   │   └── LoginRequest.java
│   │           │   └── response/
│   │           │       ├── UserDto.java
│   │           │       ├── AuthResponse.java
│   │           │       └── ErrorResponse.java
│   │           ├── security/                            # Security components
│   │           │   ├── JwtTokenProvider.java
│   │           │   ├── JwtRequestFilter.java
│   │           │   ├── JwtAuthenticationEntryPoint.java
│   │           │   └── UserPrincipal.java
│   │           ├── exception/                           # Custom exceptions
│   │           │   ├── GlobalExceptionHandler.java
│   │           │   ├── UserNotFoundException.java
│   │           │   ├── DuplicateResourceException.java
│   │           │   └── InvalidTokenException.java
│   │           ├── util/                                # Utility classes
│   │           │   ├── UserMapper.java
│   │           │   ├── ValidationUtil.java
│   │           │   └── DateUtil.java
│   │           └── validation/                          # Custom validators
│   │               ├── UserValidator.java
│   │               └── PasswordValidator.java
│   └── resources/
│       ├── application.yml                              # Main configuration
│       ├── application-dev.yml                          # Development config
│       ├── application-prod.yml                         # Production config
│       ├── db/
│       │   └── migration/                               # Database migrations
│       │       ├── V1__Create_users_table.sql
│       │       └── V2__Add_refresh_token_table.sql
│       └── static/                                      # Static resources
└── test/
    ├── java/
    │   └── com/
    │       └── example/
    │           ├── integration/                         # Integration tests
    │           │   ├── UserControllerIntegrationTest.java
    │           │   └── AuthControllerIntegrationTest.java
    │           ├── unit/                                # Unit tests
    │           │   ├── service/
    │           │   │   ├── UserServiceTest.java
    │           │   │   └── AuthServiceTest.java
    │           │   ├── controller/
    │           │   │   ├── UserControllerTest.java
    │           │   │   └── AuthControllerTest.java
    │           │   └── repository/
    │           │       └── UserRepositoryTest.java
    │           └── config/                              # Test configurations
    │               └── TestConfig.java
    └── resources/
        ├── application-test.yml                         # Test configuration
        └── data/
            ├── test-users.sql                           # Test data
            └── cleanup.sql
```

## Setup Instructions

### 1. Database Setup

#### MySQL Setup

```sql
-- Create database
CREATE DATABASE springboot_dev CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE springboot_test CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Create user
CREATE USER 'springboot'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON springboot_dev.* TO 'springboot'@'localhost';
GRANT ALL PRIVILEGES ON springboot_test.* TO 'springboot'@'localhost';
FLUSH PRIVILEGES;
```

#### PostgreSQL Setup

```sql
-- Create database
CREATE DATABASE springboot_dev;
CREATE DATABASE springboot_test;

-- Create user
CREATE USER springboot WITH PASSWORD 'password';
GRANT ALL PRIVILEGES ON DATABASE springboot_dev TO springboot;
GRANT ALL PRIVILEGES ON DATABASE springboot_test TO springboot;
```

### 2. Environment Variables

Create a `.env` file in the project root:

```bash
# Database Configuration
DB_HOST=localhost
DB_PORT=3306
DB_NAME=springboot_dev
DB_USERNAME=springboot
DB_PASSWORD=password

# JWT Configuration
JWT_SECRET=myVerySecretKeyThatIsAtLeast256BitsLong
JWT_EXPIRATION=86400

# Application Configuration
SPRING_PROFILES_ACTIVE=dev
SERVER_PORT=8080

# Logging
LOG_LEVEL=INFO
```

### 3. Maven Dependencies

Add these dependencies to your `pom.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
        <relativePath/>
    </parent>
    
    <groupId>com.example</groupId>
    <artifactId>spring-boot-app</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>
    
    <properties>
        <java.version>17</java.version>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
    </properties>
    
    <dependencies>
        <!-- Spring Boot Starters -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        
        <!-- Database -->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>test</scope>
        </dependency>
        
        <!-- JWT -->
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>0.11.5</version>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <version>0.11.5</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <version>0.11.5</version>
            <scope>runtime</scope>
        </dependency>
        
        <!-- Documentation -->
        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
            <version>2.2.0</version>
        </dependency>
        
        <!-- Testing -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>junit-jupiter</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>mysql</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

### 4. IDE Configuration

#### IntelliJ IDEA

1. Import the project as a Maven project
2. Configure JDK 17 in Project Settings
3. Enable annotation processing in Settings > Build > Compiler > Annotation Processors
4. Install plugins: Lombok, Spring Boot

#### VS Code

Install these extensions:
- Extension Pack for Java
- Spring Boot Extension Pack
- MySQL/PostgreSQL extension

## Development Workflow

### 1. Feature Development

```bash
# Create feature branch
git checkout -b feature/user-management

# Make changes and commit
git add .
git commit -m "feat: add user management endpoints"

# Push branch
git push origin feature/user-management

# Create pull request
```

### 2. Code Standards

#### Java Code Style

```java
// Class naming: PascalCase
public class UserService {
    
    // Method naming: camelCase
    public UserDto getUserById(Long id) {
        // Implementation
    }
    
    // Constants: UPPER_SNAKE_CASE
    private static final String DEFAULT_ROLE = "USER";
    
    // Variables: camelCase
    private final UserRepository userRepository;
}
```

#### REST API Conventions

```java
// Resource naming: plural nouns
@RequestMapping("/api/v1/users")

// HTTP methods and status codes
@GetMapping("/{id}")           // 200 OK
@PostMapping                   // 201 Created
@PutMapping("/{id}")          // 200 OK
@DeleteMapping("/{id}")       // 204 No Content
```

### 3. Database Migrations

Using Flyway for database migrations:

```sql
-- V1__Create_users_table.sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('USER', 'ADMIN') NOT NULL DEFAULT 'USER',
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP NULL,
    
    INDEX idx_username (username),
    INDEX idx_email (email)
);
```

### 4. Configuration Management

#### Profile-based Configuration

```yaml
# application.yml (common settings)
spring:
  application:
    name: spring-boot-app
  jpa:
    open-in-view: false

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics

---
# application-dev.yml (development)
spring:
  config:
    activate:
      on-profile: dev
  
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
  
  h2:
    console:
      enabled: true

logging:
  level:
    com.example: DEBUG

---
# application-prod.yml (production)
spring:
  config:
    activate:
      on-profile: prod
  
  datasource:
    url: ${DATABASE_URL}
    hikari:
      maximum-pool-size: 20

logging:
  level:
    com.example: INFO
```

## Testing Guide

### 1. Unit Testing

#### Service Layer Tests

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private UserMapper userMapper;
    
    @InjectMocks
    private UserService userService;

    @Test
    void shouldCreateUser_WhenValidRequest() {
        // Given
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("password123");
        
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        
        UserDto expectedDto = new UserDto();
        expectedDto.setId(1L);
        expectedDto.setUsername("testuser");
        
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(expectedDto);
        
        // When
        UserDto result = userService.createUser(request);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUsername()).isEqualTo("testuser");
        
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    void shouldThrowException_WhenUserNotFound() {
        // Given
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> userService.getUserById(userId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found with id: 999");
    }
}
```

#### Controller Tests

```java
@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UserService userService;
    
    @MockBean
    private JwtTokenProvider tokenProvider;

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnUser_WhenValidId() throws Exception {
        // Given
        Long userId = 1L;
        UserDto userDto = new UserDto();
        userDto.setId(userId);
        userDto.setUsername("testuser");
        
        when(userService.getUserById(userId)).thenReturn(userDto);
        
        // When & Then
        mockMvc.perform(get("/api/v1/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"));
    }
    
    @Test
    void shouldReturnUnauthorized_WhenNoAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isUnauthorized());
    }
}
```

### 2. Integration Testing

#### Full Stack Integration Tests

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Transactional
class UserIntegrationTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private UserRepository userRepository;
    
    @LocalServerPort
    private int port;

    @Test
    void shouldCreateAndRetrieveUser() {
        // Given
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("integrationtest");
        request.setEmail("integration@test.com");
        request.setPassword("password123");
        
        // When - Create user
        ResponseEntity<UserDto> createResponse = restTemplate.exchange(
                "/api/v1/users",
                HttpMethod.POST,
                new HttpEntity<>(request, getAuthHeaders()),
                UserDto.class
        );
        
        // Then
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResponse.getBody().getUsername()).isEqualTo("integrationtest");
        
        Long userId = createResponse.getBody().getId();
        
        // When - Retrieve user
        ResponseEntity<UserDto> getResponse = restTemplate.exchange(
                "/api/v1/users/" + userId,
                HttpMethod.GET,
                new HttpEntity<>(getAuthHeaders()),
                UserDto.class
        );
        
        // Then
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody().getUsername()).isEqualTo("integrationtest");
    }
    
    private HttpHeaders getAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + getValidJwtToken());
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
```

### 3. Test Data Management

#### Test Data Builder

```java
public class UserTestDataBuilder {
    
    private String username = "testuser";
    private String email = "test@example.com";
    private String password = "password123";
    private UserRole role = UserRole.USER;
    private Boolean active = true;
    
    public static UserTestDataBuilder aUser() {
        return new UserTestDataBuilder();
    }
    
    public UserTestDataBuilder withUsername(String username) {
        this.username = username;
        return this;
    }
    
    public UserTestDataBuilder withEmail(String email) {
        this.email = email;
        return this;
    }
    
    public UserTestDataBuilder withRole(UserRole role) {
        this.role = role;
        return this;
    }
    
    public User build() {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);
        user.setActive(active);
        user.setCreatedAt(LocalDateTime.now());
        return user;
    }
    
    public CreateUserRequest buildRequest() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername(username);
        request.setEmail(email);
        request.setPassword(password);
        request.setRole(role.name());
        return request;
    }
}

// Usage
User user = UserTestDataBuilder.aUser()
    .withUsername("admin")
    .withRole(UserRole.ADMIN)
    .build();
```

## Deployment

### 1. Docker Configuration

#### Dockerfile

```dockerfile
FROM openjdk:17-jre-slim

# Create app directory
WORKDIR /app

# Copy the JAR file
COPY target/spring-boot-app-*.jar app.jar

# Expose port
EXPOSE 8080

# Set JVM options
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

#### docker-compose.yml

```yaml
version: '3.8'

services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
      - DB_HOST=mysql
      - DB_PORT=3306
      - DB_NAME=springboot
      - DB_USERNAME=springboot
      - DB_PASSWORD=password
      - JWT_SECRET=mySecretKey123456789012345678901234567890
    depends_on:
      mysql:
        condition: service_healthy
    networks:
      - app-network

  mysql:
    image: mysql:8.0
    environment:
      - MYSQL_ROOT_PASSWORD=rootpassword
      - MYSQL_DATABASE=springboot
      - MYSQL_USER=springboot
      - MYSQL_PASSWORD=password
    ports:
      - "3306:3306"
    volumes:
      - mysql-data:/var/lib/mysql
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      timeout: 20s
      retries: 10
    networks:
      - app-network

volumes:
  mysql-data:

networks:
  app-network:
    driver: bridge
```

### 2. Production Configuration

#### application-prod.yml

```yaml
spring:
  config:
    activate:
      on-profile: prod
      
  datasource:
    url: ${DATABASE_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      idle-timeout: 300000
      max-lifetime: 1200000
      
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    
server:
  port: ${PORT:8080}
  
jwt:
  secret: ${JWT_SECRET}
  expiration: ${JWT_EXPIRATION:3600}
  
logging:
  level:
    com.example: INFO
    org.springframework.security: WARN
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: /var/log/spring-boot-app.log

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: when-authorized
```

### 3. CI/CD Pipeline

#### GitHub Actions

```yaml
# .github/workflows/ci-cd.yml
name: CI/CD Pipeline

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  test:
    runs-on: ubuntu-latest
    
    services:
      mysql:
        image: mysql:8.0
        env:
          MYSQL_ROOT_PASSWORD: password
          MYSQL_DATABASE: testdb
        options: >-
          --health-cmd="mysqladmin ping"
          --health-interval=10s
          --health-timeout=5s
          --health-retries=3
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
    
    - name: Cache Maven packages
      uses: actions/cache@v3
      with:
        path: ~/.m2
        key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}
    
    - name: Run tests
      run: mvn clean test
      env:
        DB_HOST: localhost
        DB_PORT: 3306
        DB_NAME: testdb
        DB_USERNAME: root
        DB_PASSWORD: password
    
    - name: Generate test report
      uses: dorny/test-reporter@v1
      if: success() || failure()
      with:
        name: Maven Tests
        path: target/surefire-reports/*.xml
        reporter: java-junit

  build:
    needs: test
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
    
    - name: Build with Maven
      run: mvn clean package -DskipTests
    
    - name: Build Docker image
      run: |
        docker build -t spring-boot-app:${{ github.sha }} .
        docker tag spring-boot-app:${{ github.sha }} spring-boot-app:latest
    
    - name: Deploy to staging
      run: |
        # Add deployment script here
        echo "Deploying to staging environment"
```

## Best Practices

### 1. Code Quality

#### SonarQube Configuration

```xml
<!-- Add to pom.xml -->
<plugin>
    <groupId>org.sonarsource.scanner.maven</groupId>
    <artifactId>sonar-maven-plugin</artifactId>
    <version>3.9.1.2184</version>
</plugin>
```

#### Code Coverage with JaCoCo

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.8</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### 2. Security Best Practices

#### Password Encoding

```java
@Configuration
public class SecurityConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // Higher strength
    }
}
```

#### Rate Limiting

```java
@Component
public class RateLimitingFilter implements Filter {
    
    private final Map<String, List<Long>> requestCounts = new ConcurrentHashMap<>();
    private static final int MAX_REQUESTS = 100;
    private static final long TIME_WINDOW = 3600000; // 1 hour
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, 
                        FilterChain chain) throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String clientIP = httpRequest.getRemoteAddr();
        
        if (isRateLimited(clientIP)) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setStatus(429); // Too Many Requests
            return;
        }
        
        chain.doFilter(request, response);
    }
    
    private boolean isRateLimited(String clientIP) {
        long currentTime = System.currentTimeMillis();
        requestCounts.compute(clientIP, (key, timestamps) -> {
            if (timestamps == null) {
                timestamps = new ArrayList<>();
            }
            
            // Remove old requests outside time window
            timestamps.removeIf(timestamp -> currentTime - timestamp > TIME_WINDOW);
            
            // Add current request
            timestamps.add(currentTime);
            
            return timestamps;
        });
        
        return requestCounts.get(clientIP).size() > MAX_REQUESTS;
    }
}
```

### 3. Performance Optimization

#### Database Indexing

```sql
-- Add indexes for frequently queried columns
CREATE INDEX idx_user_email ON users(email);
CREATE INDEX idx_user_created_at ON users(created_at);
CREATE INDEX idx_user_active_role ON users(active, role);

-- Composite indexes for common query patterns
CREATE INDEX idx_user_search ON users(username, email, active);
```

#### Caching Strategy

```java
@Service
@CacheConfig(cacheNames = "users")
public class UserService {
    
    @Cacheable(key = "#id")
    public UserDto getUserById(Long id) {
        // Implementation
    }
    
    @CacheEvict(key = "#id")
    public UserDto updateUser(Long id, UpdateUserRequest request) {
        // Implementation
    }
    
    @CacheEvict(allEntries = true)
    public void clearUserCache() {
        // Clear all user cache entries
    }
}
```

#### Connection Pool Configuration

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      idle-timeout: 300000
      max-lifetime: 1200000
      connection-timeout: 20000
      leak-detection-threshold: 60000
```

## Troubleshooting

### Common Issues

#### 1. Database Connection Issues

**Error**: `Cannot create PoolableConnectionFactory`

**Solution**:
```yaml
# Check database configuration
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/springboot?useSSL=false&allowPublicKeyRetrieval=true
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    driver-class-name: com.mysql.cj.jdbc.Driver
```

#### 2. JWT Token Issues

**Error**: `JWT signature does not match`

**Solution**:
```java
// Ensure consistent secret key
@Value("${jwt.secret}")
private String jwtSecret;

// Use proper key length (256+ bits)
private Key getSigningKey() {
    byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
    return Keys.hmacShaKeyFor(keyBytes);
}
```

#### 3. Validation Errors

**Error**: `MethodArgumentNotValidException`

**Solution**:
```java
@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<ErrorResponse> handleValidationException(
        MethodArgumentNotValidException ex) {
    
    List<String> errors = ex.getBindingResult().getFieldErrors()
            .stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.toList());
    
    return ResponseEntity.badRequest().body(
        ErrorResponse.builder()
            .error("VALIDATION_ERROR")
            .message(String.join(", ", errors))
            .timestamp(LocalDateTime.now())
            .build()
    );
}
```

### Debugging Tips

#### 1. Enable Debug Logging

```yaml
logging:
  level:
    com.example: DEBUG
    org.springframework.security: DEBUG
    org.springframework.web: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
```

#### 2. Profile Application Startup

```java
@Component
public class StartupProfiler implements ApplicationListener<ApplicationReadyEvent> {
    
    private static final Logger logger = LoggerFactory.getLogger(StartupProfiler.class);
    
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        long startupTime = System.currentTimeMillis() - event.getTimestamp();
        logger.info("Application startup completed in {} ms", startupTime);
    }
}
```

#### 3. Health Checks

```java
@Component
public class DatabaseHealthIndicator implements HealthIndicator {
    
    @Autowired
    private DataSource dataSource;
    
    @Override
    public Health health() {
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(1)) {
                return Health.up()
                    .withDetail("database", "Available")
                    .build();
            }
        } catch (SQLException e) {
            return Health.down()
                .withDetail("database", "Unavailable")
                .withException(e)
                .build();
        }
        
        return Health.down()
            .withDetail("database", "Unknown")
            .build();
    }
}
```

---

*This developer guide provides comprehensive setup and development instructions for the Spring Boot project. Follow these practices to ensure code quality, security, and maintainability.*