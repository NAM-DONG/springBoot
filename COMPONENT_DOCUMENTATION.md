# Spring Boot Components Documentation

## Table of Contents

1. [Architecture Overview](#architecture-overview)
2. [Controllers](#controllers)
3. [Services](#services)
4. [Repositories](#repositories)
5. [Entities](#entities)
6. [Configuration](#configuration)
7. [Security Components](#security-components)
8. [Utility Components](#utility-components)
9. [Exception Handling](#exception-handling)
10. [Validation](#validation)

## Architecture Overview

This Spring Boot application follows a layered architecture pattern:

```
┌─────────────────────────────────────┐
│            Presentation Layer       │
│         (Controllers, DTOs)         │
├─────────────────────────────────────┤
│             Service Layer           │
│        (Business Logic)             │
├─────────────────────────────────────┤
│           Repository Layer          │
│         (Data Access)               │
├─────────────────────────────────────┤
│            Entity Layer             │
│         (Domain Models)             │
└─────────────────────────────────────┘
```

## Controllers

### UserController

**Purpose**: Handles HTTP requests related to user management.

**Location**: `src/main/java/com/example/controller/UserController.java`

```java
@RestController
@RequestMapping("/api/v1/users")
@Validated
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserDto>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sort) {
        // Implementation
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userService.isCurrentUser(#id)")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        // Implementation
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody CreateUserRequest request) {
        // Implementation
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userService.isCurrentUser(#id)")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable Long id, 
            @Valid @RequestBody UpdateUserRequest request) {
        // Implementation
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        // Implementation
    }
}
```

**Key Features**:
- RESTful endpoint design
- Input validation with `@Valid`
- Method-level security with `@PreAuthorize`
- Proper HTTP status codes
- Exception handling via global exception handler

**Usage Example**:
```java
// Injecting the controller in tests
@Autowired
private UserController userController;

// Testing with MockMvc
@Test
void shouldReturnUserById() throws Exception {
    mockMvc.perform(get("/api/v1/users/1")
            .header("Authorization", "Bearer " + validToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
}
```

### AuthController

**Purpose**: Handles authentication and authorization operations.

```java
@RestController
@RequestMapping("/api/v1/auth")
@Validated
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider tokenProvider;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        // Implementation
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        // Implementation
    }

    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        // Implementation
    }
}
```

## Services

### UserService

**Purpose**: Contains business logic for user management operations.

**Location**: `src/main/java/com/example/service/UserService.java`

```java
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, 
                      PasswordEncoder passwordEncoder,
                      UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @Transactional(readOnly = true)
    public Page<UserDto> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toDto);
    }

    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        return userMapper.toDto(user);
    }

    public UserDto createUser(CreateUserRequest request) {
        validateUniqueUsername(request.getUsername());
        validateUniqueEmail(request.getEmail());
        
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    public UserDto updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
                
        if (!user.getUsername().equals(request.getUsername())) {
            validateUniqueUsername(request.getUsername());
            user.setUsername(request.getUsername());
        }
        
        if (!user.getEmail().equals(request.getEmail())) {
            validateUniqueEmail(request.getEmail());
            user.setEmail(request.getEmail());
        }
        
        user.setUpdatedAt(LocalDateTime.now());
        
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public boolean isCurrentUser(Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        User currentUser = userRepository.findByUsername(userDetails.getUsername())
                .orElse(null);
        return currentUser != null && currentUser.getId().equals(id);
    }

    private void validateUniqueUsername(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new DuplicateResourceException("Username already exists: " + username);
        }
    }

    private void validateUniqueEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Email already exists: " + email);
        }
    }
}
```

**Key Features**:
- Transactional operations with `@Transactional`
- Input validation and business rule enforcement
- Exception handling with custom exceptions
- Password encoding for security
- Current user context checking

**Usage Example**:
```java
// Injecting the service
@Autowired
private UserService userService;

// Using the service
UserDto user = userService.getUserById(1L);
Page<UserDto> users = userService.getAllUsers(PageRequest.of(0, 10));
```

### AuthService

**Purpose**: Handles authentication logic and token management.

```java
@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        String token = tokenProvider.generateToken(authentication);
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
                
        return AuthResponse.builder()
                .token(token)
                .expiresIn(tokenProvider.getExpirationTime())
                .user(userMapper.toDto(user))
                .build();
    }

    public TokenResponse refreshToken(String refreshToken) {
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new InvalidTokenException("Invalid refresh token");
        }
        
        String username = tokenProvider.getUsernameFromToken(refreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
                
        String newToken = tokenProvider.generateTokenFromUsername(username);
        
        return TokenResponse.builder()
                .token(newToken)
                .expiresIn(tokenProvider.getExpirationTime())
                .build();
    }
}
```

## Repositories

### UserRepository

**Purpose**: Data access layer for User entities.

**Location**: `src/main/java/com/example/repository/UserRepository.java`

```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    @Query("SELECT u FROM User u WHERE u.createdAt >= :date")
    List<User> findUsersCreatedAfter(@Param("date") LocalDateTime date);
    
    @Query("SELECT u FROM User u WHERE u.username LIKE %:keyword% OR u.email LIKE %:keyword%")
    Page<User> findByUsernameOrEmailContaining(@Param("keyword") String keyword, Pageable pageable);
    
    @Modifying
    @Query("UPDATE User u SET u.lastLoginAt = :lastLoginAt WHERE u.id = :id")
    void updateLastLoginTime(@Param("id") Long id, @Param("lastLoginAt") LocalDateTime lastLoginAt);
}
```

**Key Features**:
- Extends `JpaRepository` for standard CRUD operations
- Custom query methods using Spring Data JPA conventions
- Custom JPQL queries with `@Query`
- Modifying queries with `@Modifying`
- Pagination support

**Usage Example**:
```java
// Basic operations
User user = userRepository.findById(1L).orElse(null);
List<User> allUsers = userRepository.findAll();
userRepository.save(user);

// Custom queries
Optional<User> userByUsername = userRepository.findByUsername("john_doe");
boolean exists = userRepository.existsByEmail("test@example.com");
Page<User> searchResults = userRepository.findByUsernameOrEmailContaining(
    "john", PageRequest.of(0, 10)
);
```

## Entities

### User Entity

**Purpose**: Represents the User domain model and database table.

**Location**: `src/main/java/com/example/entity/User.java`

```java
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_username", columnList = "username"),
    @Index(name = "idx_email", columnList = "email")
})
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    @NotNull
    @Size(min = 3, max = 50)
    private String username;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    @NotNull
    @Email
    @Size(max = 100)
    private String email;

    @Column(name = "password", nullable = false)
    @NotNull
    @Size(min = 8)
    @JsonIgnore
    private String password;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role = UserRole.USER;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    // Constructors
    public User() {}

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    // Utility methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", active=" + active +
                '}';
    }
}

enum UserRole {
    USER, ADMIN
}
```

**Key Features**:
- JPA annotations for database mapping
- Bean validation annotations
- Audit fields with `@CreatedDate` and `@LastModifiedDate`
- Proper `equals()` and `hashCode()` implementation
- `@JsonIgnore` on sensitive fields

## Configuration

### SecurityConfig

**Purpose**: Configures application security settings.

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtRequestFilter jwtRequestFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/public/**").permitAll()
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated()
                );

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
```

### DatabaseConfig

**Purpose**: Configures database connections and JPA settings.

```java
@Configuration
@EnableJpaRepositories(basePackages = "com.example.repository")
@EnableJpaAuditing
public class DatabaseConfig {

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan("com.example.entity");
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        
        Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto", "update");
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
        properties.setProperty("hibernate.show_sql", "false");
        properties.setProperty("hibernate.format_sql", "true");
        em.setJpaProperties(properties);
        
        return em;
    }
}
```

## Security Components

### JwtTokenProvider

**Purpose**: Handles JWT token creation, validation, and parsing.

```java
@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration:3600}")
    private int jwtExpirationInSeconds;

    public String generateToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Date expiryDate = new Date(System.currentTimeMillis() + jwtExpirationInSeconds * 1000L);

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .claim("userId", userPrincipal.getId())
                .claim("role", userPrincipal.getAuthorities())
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String generateTokenFromUsername(String username) {
        Date expiryDate = new Date(System.currentTimeMillis() + jwtExpirationInSeconds * 1000L);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            logger.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty");
        }
        return false;
    }

    public long getExpirationTime() {
        return jwtExpirationInSeconds;
    }
}
```

**Usage Example**:
```java
// Generate token
String token = jwtTokenProvider.generateToken(authentication);

// Validate token
boolean isValid = jwtTokenProvider.validateToken(token);

// Extract username
String username = jwtTokenProvider.getUsernameFromToken(token);
```

### JwtRequestFilter

**Purpose**: Filters incoming requests to validate JWT tokens.

```java
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final JwtTokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain chain) throws ServletException, IOException {
        
        String header = request.getHeader("Authorization");
        String username = null;
        String authToken = null;

        if (header != null && header.startsWith("Bearer ")) {
            authToken = header.substring(7);
            try {
                username = tokenProvider.getUsernameFromToken(authToken);
            } catch (IllegalArgumentException e) {
                logger.error("Unable to get JWT Token");
            } catch (ExpiredJwtException e) {
                logger.error("JWT Token has expired");
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (tokenProvider.validateToken(authToken)) {
                UsernamePasswordAuthenticationToken authenticationToken = 
                    new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        
        chain.doFilter(request, response);
    }
}
```

## Utility Components

### UserMapper

**Purpose**: Maps between entity objects and DTOs.

```java
@Component
public class UserMapper {

    public UserDto toDto(User user) {
        if (user == null) {
            return null;
        }

        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .active(user.getActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .lastLoginAt(user.getLastLoginAt())
                .build();
    }

    public User toEntity(CreateUserRequest request) {
        if (request == null) {
            return null;
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setRole(UserRole.valueOf(request.getRole()));
        user.setActive(true);

        return user;
    }

    public void updateEntity(User user, UpdateUserRequest request) {
        if (user == null || request == null) {
            return;
        }

        if (request.getUsername() != null) {
            user.setUsername(request.getUsername());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getRole() != null) {
            user.setRole(UserRole.valueOf(request.getRole()));
        }
        if (request.getActive() != null) {
            user.setActive(request.getActive());
        }
    }
}
```

**Usage Example**:
```java
// Entity to DTO
UserDto dto = userMapper.toDto(user);

// Request to Entity
User user = userMapper.toEntity(createRequest);

// Update entity from request
userMapper.updateEntity(existingUser, updateRequest);
```

## Exception Handling

### GlobalExceptionHandler

**Purpose**: Centralized exception handling for the application.

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex) {
        ErrorResponse error = ErrorResponse.builder()
                .error("RESOURCE_NOT_FOUND")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResourceException(DuplicateResourceException ex) {
        ErrorResponse error = ErrorResponse.builder()
                .error("DUPLICATE_RESOURCE")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ErrorResponse error = ErrorResponse.builder()
                .error("VALIDATION_ERROR")
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        ErrorResponse error = ErrorResponse.builder()
                .error("ACCESS_DENIED")
                .message("Access denied: " + ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        logger.error("Unexpected error occurred", ex);
        
        ErrorResponse error = ErrorResponse.builder()
                .error("INTERNAL_SERVER_ERROR")
                .message("An unexpected error occurred")
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
```

### Custom Exceptions

```java
@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message) {
        super(message);
    }
}
```

## Validation

### Custom Validators

**Purpose**: Custom validation logic for business rules.

```java
@Component
public class UserValidator {

    private static final String USERNAME_PATTERN = "^[a-zA-Z0-9_]{3,50}$";
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    
    private final Pattern usernamePattern = Pattern.compile(USERNAME_PATTERN);
    private final Pattern emailPattern = Pattern.compile(EMAIL_PATTERN);

    public void validateCreateUserRequest(CreateUserRequest request) {
        List<String> errors = new ArrayList<>();

        if (!isValidUsername(request.getUsername())) {
            errors.add("Username must be 3-50 characters and contain only letters, numbers, and underscores");
        }

        if (!isValidEmail(request.getEmail())) {
            errors.add("Email must be a valid email address");
        }

        if (!isValidPassword(request.getPassword())) {
            errors.add("Password must be at least 8 characters long and contain letters and numbers");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Validation failed: " + String.join(", ", errors));
        }
    }

    private boolean isValidUsername(String username) {
        return username != null && usernamePattern.matcher(username).matches();
    }

    private boolean isValidEmail(String email) {
        return email != null && emailPattern.matcher(email).matches();
    }

    private boolean isValidPassword(String password) {
        return password != null && 
               password.length() >= 8 && 
               password.matches(".*[a-zA-Z].*") && 
               password.matches(".*[0-9].*");
    }
}
```

### DTO Validation

```java
public class CreateUserRequest {

    @NotNull(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers, and underscores")
    private String username;

    @NotNull(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @NotNull(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9]).*$", message = "Password must contain letters and numbers")
    private String password;

    @NotNull(message = "Role is required")
    @Pattern(regexp = "USER|ADMIN", message = "Role must be USER or ADMIN")
    private String role = "USER";

    // Getters and setters
}
```

**Usage Example**:
```java
// Controller method with validation
@PostMapping
public ResponseEntity<UserDto> createUser(@Valid @RequestBody CreateUserRequest request) {
    userValidator.validateCreateUserRequest(request);
    UserDto user = userService.createUser(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(user);
}
```

---

*This documentation covers the core components of a Spring Boot application. Each component follows best practices for separation of concerns, security, and maintainability.*